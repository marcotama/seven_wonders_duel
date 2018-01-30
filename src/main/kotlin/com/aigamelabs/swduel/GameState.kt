package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.actions.ChooseNextPlayer
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import java.util.logging.Logger
import javax.json.stream.JsonGenerator

data class GameState(
        val activeScienceDeck : Deck,
        val unusedScienceDeck : Deck,
        val wondersForPickDeck : Deck,
        val unusedWondersDeck : Deck,
        val burnedDeck : Deck,
        val cardStructure : CardStructure?,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val decisionQueue: Queue<Decision>,
        private val progressTokens : HashSet<ProgressToken>,
        val gamePhase: GamePhase,
        val nextPlayer: PlayerTurn
) {

    fun update(
            activeScienceDeck_ : Deck? = null,
            unusedScienceDeck_ : Deck? = null,
            wondersForPickDeck_ : Deck? = null,
            unusedWondersDeck_ : Deck? = null,
            burnedDeck_ : Deck? = null,
            cardStructure_ : CardStructure? = null,
            progressTokens_ : HashSet<ProgressToken>? = null,
            militaryBoard_ : MilitaryBoard? = null,
            playerCities_ : HashMap<PlayerTurn,PlayerCity>? = null,
            decisionQueue_ : Queue<Decision>? = null,
            gamePhase_: GamePhase? = null,
            nextPlayer_: PlayerTurn? = null
    ) : GameState {
        return GameState(
                activeScienceDeck_ ?: activeScienceDeck,
                unusedScienceDeck_ ?: unusedScienceDeck,
                wondersForPickDeck_ ?: wondersForPickDeck,
                unusedWondersDeck_ ?: unusedWondersDeck,
                burnedDeck_ ?: burnedDeck,
                cardStructure_ ?: cardStructure,
                militaryBoard_ ?: militaryBoard,
                playerCities_ ?: playerCities,
                decisionQueue_ ?: decisionQueue,
                progressTokens_ ?: progressTokens,
                gamePhase_ ?: gamePhase,
                nextPlayer_ ?: nextPlayer
        )
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return playerCities.get(playerTurn)
                .getOrElseThrow { Exception("Player city not found") }
    }

    fun updateBoard(generator: RandomWithTracker?, logger: Logger? = null) : GameState {
        when (gamePhase) {
            GamePhase.WONDERS_SELECTION -> {
                return if (wondersForPickDeck.size() == 0 && unusedWondersDeck.size() == 4) {
                    logger?.info("Switching to Age I")
                    // Setup cards structure
                    val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure(generator)
                    // Add main turn decision
                    val newGameState = update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.FIRST_AGE)
                    val decision = DecisionFactory.makeTurnDecision(PlayerTurn.PLAYER_1, newGameState)
                    newGameState.update(decisionQueue_ = decisionQueue.enqueue(decision))
                } else {
                    // Wonder selection is handled by ChooseStartingWonder
                    this
                }
            }
            GamePhase.FIRST_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Switching to Age II")
                    // Setup cards structure
                    val newCardStructure = CardStructureFactory.makeSecondCardStructure(generator)
                    // Create decision for starting player
                    val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: nextPlayer.opponent() // Last player of the previous age
                    val actions = PlayerTurn.values().map { ChooseNextPlayer(choosingPlayer, it) }
                    val decision = Decision(choosingPlayer, Vector.ofAll(actions), "GameState.updateBoard")

                    update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.SECOND_AGE,
                            decisionQueue_ = decisionQueue.enqueue(decision))
                } else {
                    val decision = DecisionFactory.makeTurnDecision(nextPlayer, this)
                    update(decisionQueue_ = decisionQueue.enqueue(decision))
                }
            }
            GamePhase.SECOND_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Switching to Age III")
                    // Setup cards structure
                    val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure(generator)
                    // Create decision for starting player
                    val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: nextPlayer.opponent() // Last player of the previous age
                    val actions = PlayerTurn.values().map { p -> ChooseNextPlayer(choosingPlayer, p) }
                    val decision = Decision(choosingPlayer, Vector.ofAll(actions), "GameState.updateBoard")

                    update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.THIRD_AGE,
                            decisionQueue_ = decisionQueue.enqueue(decision))
                } else {
                    val decision = DecisionFactory.makeTurnDecision(nextPlayer, this)
                    update(decisionQueue_ = decisionQueue.enqueue(decision))
                }
            }

            GamePhase.THIRD_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Civilian victory")
                    update(gamePhase_ = GamePhase.CIVILIAN_VICTORY)
                } else {
                    val decision = DecisionFactory.makeTurnDecision(nextPlayer, this)
                    update(decisionQueue_ = decisionQueue.enqueue(decision))
                }
            }
            else -> {
                return this
            }
        }
    }

    fun checkScienceSupremacy(playerTurn: PlayerTurn, logger: Logger? = null) : GameState {
        // Count science symbols
        val playerCity = getPlayerCity(playerTurn)
        val hasLawToken = !playerCity.progressTokens.filter { it.enhancement == Enhancement.LAW }.isEmpty
        val symbolsFromGreenCards = playerCity.buildings.map { it.scienceSymbol }.distinct().size()
        val distinctScienceSymbols = symbolsFromGreenCards + if (hasLawToken) 1 else 0

        return if (distinctScienceSymbols >= 6) {
            logger?.info("Science supremacy")
            update(gamePhase_ = GamePhase.SCIENCE_SUPREMACY)
        }
        else this
    }

    fun checkMilitarySupremacy(logger: Logger? = null) : GameState {
        return if (militaryBoard.isMilitarySupremacy()) {
            logger?.info("Military supremacy")
            update(gamePhase_ = GamePhase.MILITARY_SUPREMACY)
        }
        else this
    }

    private fun calculateVictoryPoints(player : PlayerTurn) : Int {
        val playerCity = getPlayerCity(player)
        val opponentCity = getPlayerCity(player.opponent())
        val totalFromBuildings = playerCity.buildings
                .filter { it.victoryPointsFormula == Formula.ABSOLUTE }
                .map { it.victoryPoints * getMultiplier(it.victoryPointsFormula, it.victoryPointsReferenceCity, playerCity, opponentCity) }
                .fold(0, { a, b -> a + b } )
        val totalFromWonders = playerCity.wonders
                .filter { c -> c.victoryPointsFormula == Formula.ABSOLUTE }
                .map { c -> c.victoryPoints }
                .fold(0, { a, b -> a + b } )
        val totalFromProgressTokens = playerCity.progressTokens
                .filter { c -> c.victoryPointsFormula == Formula.ABSOLUTE }
                .map { c -> c.victoryPoints }
                .fold(0, { a, b -> a + b } )
        val totalFromMathToken = if (playerCity.hasProgressToken(Enhancement.MATHEMATICS))
            3 * playerCity.progressTokens.size() else 0

        return totalFromBuildings + totalFromWonders + totalFromProgressTokens + totalFromMathToken
    }

    fun getMultiplier(formula: Formula, cityForFormula: CityForFormula, playerCity: PlayerCity, opponentCity: PlayerCity) : Int {

        // From highest city
        val fhc = { c : CardColor -> Math.max(playerCity.countBuildingsByColor(c), opponentCity.countBuildingsByColor(c)) }
        // From player city
        val fpc = { c : CardColor -> playerCity.countBuildingsByColor(c) }

        return when (cityForFormula) {
            CityForFormula.NOT_APPLICABLE -> when (formula) {
                Formula.ABSOLUTE -> { 1 }
                else -> { throw Exception("Formula for victory points is not ABSOLUTE but reference city is NOT_APPLICABLE")}
            }
            CityForFormula.CITY_WITH_MOST_UNITS  -> when (formula) {
                Formula.PER_BROWN_AND_GRAY_CARD -> { fhc(CardColor.BROWN) + fhc(CardColor.GRAY) }
                Formula.PER_BROWN_CARD -> { fhc(CardColor.BROWN) }
                Formula.PER_GRAY_CARD -> { fhc(CardColor.GRAY) }
                Formula.PER_GREEN_CARD -> { fhc(CardColor.GREEN) }
                Formula.PER_BLUE_CARD -> { fhc(CardColor.BLUE) }
                Formula.PER_GOLD_CARD -> { fhc(CardColor.GOLD) }
                Formula.PER_RED_CARD -> { fhc(CardColor.RED) }
                Formula.PER_THREE_COINS -> { Math.max(playerCity.coins, opponentCity.coins) / 3 }
                Formula.PER_WONDER -> { Math.max(playerCity.wonders.size(), opponentCity.wonders.size()) }
                Formula.ABSOLUTE -> { throw Exception("Formula for victory points is ABSOLUTE but reference city is not NOT_APPLICABLE")}

            }
            CityForFormula.YOUR_CITY-> when (formula) {
                Formula.PER_BROWN_AND_GRAY_CARD -> { fhc(CardColor.BROWN) + fhc(CardColor.GRAY) }
                Formula.PER_BROWN_CARD -> { fpc(CardColor.BROWN) }
                Formula.PER_GRAY_CARD -> { fpc(CardColor.GRAY) }
                Formula.PER_GREEN_CARD -> { fpc(CardColor.GREEN) }
                Formula.PER_BLUE_CARD -> { fpc(CardColor.BLUE) }
                Formula.PER_GOLD_CARD -> { fpc(CardColor.GOLD) }
                Formula.PER_RED_CARD -> { fpc(CardColor.RED) }
                Formula.PER_THREE_COINS -> { playerCity.coins / 3 }
                Formula.PER_WONDER -> { playerCity.wonders.size() }
                Formula.ABSOLUTE -> { throw Exception("Formula for victory points is ABSOLUTE but reference city is not NOT_APPLICABLE")}
            }
        }
    }

    /**
     * Deques a decision and returns the updated game state (without the decision in the queue) and the extracted
     * decision.
     */
    fun dequeAction() : Pair<GameState, Decision> {
        val dequeueOutcome = decisionQueue.dequeue()
        val thisDecision = dequeueOutcome._1
        val newDecisionsQueue = dequeueOutcome._2
        val returnGameState = update(decisionQueue_ = newDecisionsQueue)
        return Pair(returnGameState, thisDecision)
    }

    /**
     * Calculates the winner and the victory points
     */
    fun calculateWinner(): Triple<GameOutcome, Int, Int> {
        val endGamePhases = HashSet.of(GamePhase.MILITARY_SUPREMACY, GamePhase.SCIENCE_SUPREMACY, GamePhase.CIVILIAN_VICTORY)
        return if (endGamePhases.contains(gamePhase)) {
            val p1VictoryPoints = calculateVictoryPoints(PlayerTurn.PLAYER_1)
            val p2VictoryPoints = calculateVictoryPoints(PlayerTurn.PLAYER_2)
            when {
                p1VictoryPoints > p2VictoryPoints -> Triple(GameOutcome.PLAYER_1_VICTORY, p1VictoryPoints, p2VictoryPoints)
                p2VictoryPoints > p1VictoryPoints -> Triple(GameOutcome.PLAYER_2_VICTORY, p1VictoryPoints, p2VictoryPoints)
                else -> Triple(GameOutcome.TIE, p1VictoryPoints, p2VictoryPoints)
            }
        } else {
            throw Exception("The game has not finished yet; current phase: $gamePhase")
        }
    }

    /**
     * Advances the game by one step by applying the given action to the next decision in the queue. Does not detect
     * cheating.
     */
    fun applyAction(action: Action, generator: RandomWithTracker? = null): GameState {

        // Process action
        var newGameState = action.process(this, generator)

        // If the cards structure is empty, switch to next age
        if (newGameState.cardStructure == null || newGameState.cardStructure!!.isEmpty()) {
            newGameState = newGameState.updateBoard(generator)
        }

        return newGameState
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        activeScienceDeck.toJson(generator, "active_science_tokens")
        unusedScienceDeck.toJson(generator, "unused_science_tokens")
        wondersForPickDeck.toJson(generator, "wonders_for_pick")
        unusedWondersDeck.toJson(generator, "unused_wonders")
        burnedDeck.toJson(generator, "burned_cards")

        cardStructure?.toJson(generator, "card_structure")

        generator.writeStartArray("progress_tokens")
        progressTokens.forEach { generator.write(it.toString()) }
        generator.writeEnd()

        militaryBoard.toJson(generator, "military_board")

        generator.writeStartObject("player_cities")
        playerCities.forEach { it._2.toJson(generator, it._1.toString()) }
        generator.writeEnd()

        generator.writeStartArray("decision_queue")
        decisionQueue.forEach { it.toJson(generator, null) }
        generator.writeEnd()

        generator.write("game_phase", gamePhase.toString())
        generator.write("default_player", nextPlayer.toString())

        generator.writeEnd()
    }

}