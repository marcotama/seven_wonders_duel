package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.actions.ChooseNextPlayer
import com.aigamelabs.swduel.actions.ChooseProgressToken
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import java.util.logging.Level
import java.util.logging.Logger
import javax.json.stream.JsonGenerator

data class GameState(
        val availableProgressTokens: Deck,
        val discardedProgressTokens: Deck,
        val wondersForPick: Deck,
        val discardedWonders: Deck,
        val burnedCards: Deck,
        val cardStructure : CardStructure?,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val decisionQueue: Queue<Decision>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
) {

    fun update(
            activeScienceDeck_ : Deck? = null,
            unusedScienceDeck_ : Deck? = null,
            wondersForPickDeck_ : Deck? = null,
            unusedWondersDeck_ : Deck? = null,
            burnedDeck_ : Deck? = null,
            cardStructure_ : CardStructure? = null,
            militaryBoard_ : MilitaryBoard? = null,
            playerCities_ : HashMap<PlayerTurn,PlayerCity>? = null,
            decisionQueue_ : Queue<Decision>? = null,
            gamePhase_: GamePhase? = null,
            nextPlayer_: PlayerTurn? = null
    ) : GameState {
        return GameState(
                activeScienceDeck_ ?: availableProgressTokens,
                unusedScienceDeck_ ?: discardedProgressTokens,
                wondersForPickDeck_ ?: wondersForPick,
                unusedWondersDeck_ ?: discardedWonders,
                burnedDeck_ ?: burnedCards,
                cardStructure_ ?: cardStructure,
                militaryBoard_ ?: militaryBoard,
                playerCities_ ?: playerCities,
                decisionQueue_ ?: decisionQueue,
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
                return if (wondersForPick.size() == 0 && discardedWonders.size() == 4) {
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
                    val actions = PlayerTurn.values().map { ChooseNextPlayer(choosingPlayer, it) }
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


    private fun testScienceSupremacy(playerTurn: PlayerTurn) : Boolean {
        val playerCity = getPlayerCity(playerTurn)
        val hasLawToken = !playerCity.progressTokens.filter { it.enhancement == Enhancement.LAW }.isEmpty
        val symbolsFromGreenCards = playerCity.buildings.map { it.scienceSymbol }.distinct().size()
        val distinctScienceSymbols = symbolsFromGreenCards + if (hasLawToken) 1 else 0

        return distinctScienceSymbols >= 6
    }

    fun checkScienceSupremacy(playerTurn: PlayerTurn) : GameState {
        return if (testScienceSupremacy(playerTurn))
            update(gamePhase_ = GamePhase.SCIENCE_SUPREMACY, decisionQueue_ = Queue.empty())
        else
            this
    }

    fun checkMilitarySupremacy() : GameState {
        return if (militaryBoard.isMilitarySupremacy()) {
            update(gamePhase_ = GamePhase.MILITARY_SUPREMACY, decisionQueue_ = Queue.empty())
        }
        else this
    }

    private fun calculateVictoryPoints(player : PlayerTurn, logger: Logger?) : Int {
        val playerCity = getPlayerCity(player)
        val opponentCity = getPlayerCity(player.opponent())
        val logMsg = StringBuilder()
        var total = 0

        playerCity.buildings.forEach{
            val multiplier = getMultiplier(it.victoryPointsFormula, it.victoryPointsReferenceCity, playerCity, opponentCity)
            val contribution = it.victoryPoints * multiplier
            if (contribution > 0)
                logMsg.append("  $contribution pts from ${it.name}\n")
            total += contribution
        }

        playerCity.wonders.forEach{
            val multiplier = getMultiplier(it.victoryPointsFormula, it.victoryPointsReferenceCity, playerCity, opponentCity)
            val contribution = it.victoryPoints * multiplier
            if (contribution > 0)
                logMsg.append("  $contribution pts from ${it.name}\n")
            total += contribution
        }

        playerCity.progressTokens.forEach{
            val multiplier = getMultiplier(it.victoryPointsFormula, it.victoryPointsReferenceCity, playerCity, opponentCity)
            val contribution = it.victoryPoints * multiplier
            if (contribution > 0)
                logMsg.append("  $contribution pts from ${it.name}\n")
            total += contribution
        }

        val mathTokenContribution = if (playerCity.hasProgressToken(Enhancement.MATHEMATICS))
            3 * playerCity.progressTokens.size() else 0
        if (mathTokenContribution > 0)
            logMsg.append("  $mathTokenContribution pts from Math progress token\n")

        val coinsContribution = playerCity.coins / 3
        if (coinsContribution > 0)
            logMsg.append("  $coinsContribution pts from coins\n")

        val militaryContribution = militaryBoard.getVictoryPoints(player)
        if (militaryContribution > 0)
            logMsg.append("  $militaryContribution pts from military advantage\n")

        logger?.log(Level.INFO, logMsg.toString())
        return total + mathTokenContribution + coinsContribution + militaryContribution
    }

    private fun getMultiplier(formula: Formula, cityForFormula: CityForFormula, playerCity: PlayerCity, opponentCity: PlayerCity) : Int {

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
    fun calculateWinner(logger: Logger? = null): Triple<GameOutcome, Int, Int> {
        return when (gamePhase) {
            GamePhase.CIVILIAN_VICTORY -> {
                val p1VictoryPoints = calculateVictoryPoints(PlayerTurn.PLAYER_1, logger)
                val p2VictoryPoints = calculateVictoryPoints(PlayerTurn.PLAYER_2, logger)
                when {
                    p1VictoryPoints > p2VictoryPoints -> Triple(GameOutcome.PLAYER_1_VICTORY, p1VictoryPoints, p2VictoryPoints)
                    p2VictoryPoints > p1VictoryPoints -> Triple(GameOutcome.PLAYER_2_VICTORY, p1VictoryPoints, p2VictoryPoints)
                    else -> Triple(GameOutcome.TIE, p1VictoryPoints, p2VictoryPoints)
                }
            }
            GamePhase.SCIENCE_SUPREMACY -> when {
                testScienceSupremacy(PlayerTurn.PLAYER_1) -> Triple(GameOutcome.PLAYER_1_VICTORY, 0, 0)
                testScienceSupremacy(PlayerTurn.PLAYER_2) -> Triple(GameOutcome.PLAYER_2_VICTORY, 0, 0)
                else -> throw Exception("Phase is science supremacy, but none of the players satisfy the conditions")
            }
            GamePhase.MILITARY_SUPREMACY -> when (militaryBoard.getDisadvantagedPlayer()) {
                PlayerTurn.PLAYER_2 -> Triple(GameOutcome.PLAYER_1_VICTORY, 0, 0)
                PlayerTurn.PLAYER_1 -> Triple(GameOutcome.PLAYER_2_VICTORY, 0, 0)
                else -> throw Exception("Phase is military supremacy but conflict pawn is in the middle")
            }
            else -> throw Exception("The game has not finished yet; current phase: $gamePhase")
        }
    }

    fun buildBuilding(playerTurn: PlayerTurn, card: Card, generator: RandomWithTracker?): GameState {


        // Add card to appropriate player city
        val playerCity = getPlayerCity(playerTurn)
        val opponentCity = getPlayerCity(playerTurn.opponent())
        val coins = if (card.coinsProduced > 0)
            card.coinsProduced * getMultiplier(card.coinsProducedFormula, card.coinsProducedReferenceCity, playerCity, opponentCity)
        else
            0
        val newPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card), coins_ = playerCity.coins + coins)
        var newPlayerCities = playerCities.put(playerTurn, newPlayerCity)

        // Handle military cards
        val newMilitaryBoard: MilitaryBoard
        if (card.color == CardColor.RED) {
            val additionOutcome = militaryBoard.addMilitaryPointsTo(card.militaryPoints, playerTurn)
            newMilitaryBoard = additionOutcome.second
            // Apply penalty to opponent city, if any
            val opponentPenalty = additionOutcome.first
            if (opponentPenalty > 0) {
                val newOpponentCity = opponentCity.update(coins_ = opponentCity.coins - opponentPenalty)
                newPlayerCities = playerCities.put(playerTurn.opponent(), newOpponentCity)
            }
        } else {
            // Unchanged
            newMilitaryBoard = militaryBoard
        }

        val updatedDecisionQueue =
                if (card.color == CardColor.GREEN &&
                        playerCity.buildings.filter { it.scienceSymbol == card.scienceSymbol }.size() == 2) {
                    val actions: Vector<Action> = availableProgressTokens.cards
                            .map { ChooseProgressToken(playerTurn, it) }
                    val decision = Decision(playerTurn, actions, "BuildBuilding.process")
                    decisionQueue.enqueue(decision)
                } else
                    null

        val newGameState = update(
                playerCities_ = newPlayerCities,
                militaryBoard_ = newMilitaryBoard,
                nextPlayer_ = nextPlayer.opponent(),
                decisionQueue_ = updatedDecisionQueue
        ).updateBoard(generator)

        return when {
            card.color == CardColor.GREEN -> newGameState.checkScienceSupremacy(playerTurn)
            card.color == CardColor.RED -> newGameState.checkMilitarySupremacy()
            else -> newGameState
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
        availableProgressTokens.toJson(generator, "available_progress_tokens")
        discardedProgressTokens.toJson(generator, "discarded_progress_tokens")
        wondersForPick.toJson(generator, "wonders_for_pick")
        discardedWonders.toJson(generator, "unused_wonders")
        burnedCards.toJson(generator, "burned_cards")

        cardStructure?.toJson(generator, "card_structure")

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

    override fun toString(): String {
        val ret = StringBuilder()
        ret.append(
                "Game phase: $gamePhase\n\n",
                "Player 1 city:\n\n  ${getPlayerCity(PlayerTurn.PLAYER_1).toString()
                        .replace("\n", "\n  ")}\n",
                "Player 2 city:\n\n  ${getPlayerCity(PlayerTurn.PLAYER_2).toString()
                        .replace("\n", "\n  ")}\n",
                "Available progress tokens:\n",
                availableProgressTokens.cards.map { "  ${it.name}\n" }
                        .fold("", { acc, s -> "$acc$s"}) + "\n",
                "Discarded progress tokens:\n",
                discardedProgressTokens.cards.map { "  ${it.name}\n" }
                        .fold("", { acc, s -> "$acc$s"}) + "\n",
                "Wonders for picking:\n",
                wondersForPick.cards.map { "  ${it.name}\n" }
                        .fold("", { acc, s -> "$acc$s"}) + "\n",
                "Discarded wonders:\n",
                discardedWonders.cards.map { "  ${it.name}\n" }
                        .fold("", { acc, s -> "$acc$s"}) + "\n",
                "Burned cards:\n",
                burnedCards.cards.map { "  ${it.name}\n" }
                        .fold("", { acc, s -> "$acc$s"}) + "\n",
                "Military board:\n${militaryBoard.toString().replace("\n", "\n  ")}\n\n",
                "Next player: $nextPlayer\n",
                "Decision queue:\n" +
                        decisionQueue.forEachIndexed { index, decision -> "  #$index (${decision.player}) options:\n" +
                                decision.options.map { "    $it\n" }
                                        .fold("", { acc, s -> "$acc$s"}) + "\n"
                                }
                )
        return ret.toString()
    }

}