package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.*
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.game.*
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashSet
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import org.json.JSONObject
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
        val player1City : PlayerCity,
        val player2City : PlayerCity,
        val decisionQueue: Queue<Decision<GameState>>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
): AbstractGameState<GameState>() {

    fun update(
            activeScienceDeck_ : Deck? = null,
            unusedScienceDeck_ : Deck? = null,
            wondersForPickDeck_ : Deck? = null,
            unusedWondersDeck_ : Deck? = null,
            burnedDeck_ : Deck? = null,
            cardStructure_ : CardStructure? = null,
            militaryBoard_ : MilitaryBoard? = null,
            player1City_ : PlayerCity? = null,
            player2City_ : PlayerCity? = null,
            decisionQueue_ : Queue<Decision<GameState>>? = null,
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
                player1City_ ?: player1City,
                player2City_ ?: player2City,
                decisionQueue_ ?: decisionQueue,
                gamePhase_ ?: gamePhase,
                nextPlayer_ ?: nextPlayer
        )
    }

    override fun isGameOver(): Boolean {
        val gameOverPhases = setOf(GamePhase.MILITARY_SUPREMACY, GamePhase.SCIENCE_SUPREMACY, GamePhase.CIVILIAN_VICTORY)
        return gameOverPhases.contains(gamePhase)
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1 -> player1City
            PlayerTurn.PLAYER_2 -> player2City
        }
    }

    private fun updateBoard(generator: RandomWithTracker, logger: Logger? = null) : GameState {
        if (cardStructure != null && !cardStructure.isEmpty())
            return this

        when (gamePhase) {
            GamePhase.WONDERS_SELECTION -> {
                return if (wondersForPick.size() == 0 && discardedWonders.size() == 4) {
                    logger?.info("Switching to Age I")
                    // Setup cards structure
                    val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure(generator)
                    // Add main turn decision
                    update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.FIRST_AGE)
                } else {
                    // Wonder selection is handled by ChooseStartingWonder
                    this
                }
            }
            GamePhase.FIRST_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Switching to Age II")
                    val newCardStructure = CardStructureFactory.makeSecondCardStructure(generator)
                    update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.SECOND_AGE)
                            .addSelectStartingPlayerDecision()
                } else {
                    this
                }
            }
            GamePhase.SECOND_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Switching to Age III")
                    val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure(generator)
                    update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.THIRD_AGE)
                            .addSelectStartingPlayerDecision()
                } else {
                    this
                }
            }

            GamePhase.THIRD_AGE -> {
                return if (cardStructure!!.isEmpty()) {
                    logger?.info("Civilian victory")
                    update(gamePhase_ = GamePhase.CIVILIAN_VICTORY)
                } else {
                    this
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
        val symbolsFromGreenCards = playerCity.buildings
                .filter{ it.scienceSymbol != ScienceSymbol.NONE}
                .map { it.scienceSymbol }
                .distinct().size()
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
    override fun dequeAction() : Pair<GameState, Decision<GameState>> {
        val dequeueOutcome = decisionQueue.dequeue()
        val thisDecision = dequeueOutcome._1
        val updatedDecisionsQueue = dequeueOutcome._2
        val returnGameState = update(decisionQueue_ = updatedDecisionsQueue)
        return Pair(returnGameState, thisDecision)
    }

    /* Queue management functions */

    private fun enqueue(decision: Decision<GameState>): GameState {
        val updatedDecisionQueue = decisionQueue.insert(0, decision)
        return update(decisionQueue_ = updatedDecisionQueue)
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

    fun buildBuilding(player: PlayerTurn, card: Card, generator: RandomWithTracker, logger: Logger?, forFree: Boolean): GameState {
        // Add card to appropriate player city
        val playerCity = getPlayerCity(player)
        val opponentCity = getPlayerCity(player.opponent())
        val coins = if (card.coinsProduced > 0)
            card.coinsProduced * getMultiplier(card.coinsProducedFormula, card.coinsProducedReferenceCity, playerCity, opponentCity)
        else
            0
        val cost = if (forFree)
            0
        else
            playerCity.canBuild(card, opponentCity) ?: throw Exception("Building not affordable $card")
        val updatedPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card), coins_ = playerCity.coins + coins - cost)

        // Handle military cards
        val updatedMilitaryBoard: MilitaryBoard
        val updatedOpponentCity: PlayerCity
        if (card.color == CardColor.RED) {
            val hasStrategyToken = !playerCity.progressTokens.filter { it.enhancement == Enhancement.STRATEGY }.isEmpty
            val militaryShift = if (hasStrategyToken) card.militaryPoints + 1 else card.militaryPoints
            val additionOutcome = militaryBoard.addMilitaryPointsTo(militaryShift, player)
            updatedMilitaryBoard = additionOutcome.second
            // Apply penalty to opponent city, if any
            val opponentPenalty = additionOutcome.first
            updatedOpponentCity = if (opponentPenalty > 0)
                opponentCity.update(coins_ = opponentCity.coins - opponentPenalty)
            else
                opponentCity
        } else {
            // Unchanged
            updatedMilitaryBoard = militaryBoard
            updatedOpponentCity = opponentCity
        }

        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else updatedOpponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else updatedOpponentCity
        var updatedGameState = update(
                player1City_ = updatedPlayer1City,
                player2City_ = updatedPlayer2City,
                militaryBoard_ = updatedMilitaryBoard
        )

        updatedGameState = if (card.color == CardColor.GREEN && updatedPlayerCity.twoScienceCardsWithSymbol(card.scienceSymbol))
            updatedGameState.addSelectProgressTokenDecision(player)
        else
            updatedGameState.addMainTurnDecision(generator, logger)

        return when {
            card.color == CardColor.GREEN -> updatedGameState.checkScienceSupremacy(player)
            card.color == CardColor.RED -> updatedGameState.checkMilitarySupremacy()
            else -> updatedGameState
        }
    }

    fun addMilitaryProgress(strength: Int, player: PlayerTurn): GameState {

        // Move military tokens
        val militaryOutcome = militaryBoard.addMilitaryPointsTo(strength, player)

        // Deal with any burning any coins
        return if (militaryOutcome.first == 0) {
            update(militaryBoard_ = militaryOutcome.second)
        } else {
            val playerCity = getPlayerCity(player)
            val opponentCity = getPlayerCity(player.opponent())
            val updatedOpponentCity = opponentCity.removeCoins(militaryOutcome.first)
            val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) playerCity else updatedOpponentCity
            val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) playerCity else updatedOpponentCity
            update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City,
                    militaryBoard_ = militaryOutcome.second)
        }
    }

    fun addSelectStartingPlayerDecision(): GameState {
        val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: nextPlayer.opponent() // Last player of the previous age
        val actions: List<Action<GameState>> = PlayerTurn.values().map { ChooseNextPlayer(choosingPlayer, it) }
        val decision = Decision(choosingPlayer, Vector.ofAll(actions))
        return enqueue(decision)
    }

    fun addMainTurnDecision(generator: RandomWithTracker, logger: Logger?): GameState {
        val updatedGameState = updateBoard(generator, logger)
        return if (updatedGameState.decisionQueue.isEmpty)
            updatedGameState.addMainTurnDecisionHelper()
        else
            updatedGameState
    }

    private fun addMainTurnDecisionHelper(): GameState {

        val playerCity = getPlayerCity(nextPlayer)
        val opponentCity = getPlayerCity(nextPlayer.opponent())
        val canBuildSomeWonders =
                !playerCity.unbuiltWonders.filter { playerCity.canBuild(it, opponentCity) != null }.isEmpty &&
                        getPlayerCity(nextPlayer.opponent()).wonders.size() < 4

        val availCards = cardStructure!!.availableCards()
        // The player can always burn any uncovered card for money
        var actions: Vector<Action<GameState>> = availCards.map { BurnForMoney(nextPlayer, it) }
        // If the player can afford at least a wonder, then he can also sacrifice any uncovered card to build the wonder
        if (canBuildSomeWonders) {
            actions = actions.appendAll(availCards.map { BurnForWonder(nextPlayer, it) })
        }
        // The player can also build a card, if the city can afford it
        actions = actions.appendAll(availCards
                .filter { playerCity.canBuild(it, opponentCity) != null }
                .map { BuildBuilding(nextPlayer, it) }
        )
        val decision = Decision(nextPlayer, actions)
        return enqueue(decision)
    }

    fun addBurnOpponentBuildingDecision(player: PlayerTurn, color: CardColor): GameState {
        val opponentCity = getPlayerCity(player.opponent())
        val burnable = opponentCity.getBurnableBuildings(color)
        if (burnable.isEmpty)
            throw Exception("There are no $color buildings to burn")
        else {
            val actions: HashSet<Action<GameState>> = burnable.map { BurnOpponentCard(player, it) }
            val decision = Decision(player, Vector.ofAll(actions))
            return enqueue(decision)
        }
    }

    fun addSelectProgressTokenDecision(player: PlayerTurn): GameState {
        val actions: Vector<Action<GameState>> = availableProgressTokens.cards
                .map { ChooseProgressToken(player, it) }
        val decision = Decision(player, Vector.ofAll(actions))
        return enqueue(decision)
    }

    fun addSelectDiscardedProgressTokenDecision(player: PlayerTurn): GameState {
        val actions: Vector<Action<GameState>> = discardedProgressTokens.cards
                .map { ChooseUnusedProgressToken(player, it) }
        val decision = Decision(player, Vector.ofAll(actions))
        return enqueue(decision)
    }

    fun addSelectBurnedBuildingToBuildDecision(player: PlayerTurn): GameState {
        val actions: Vector<Action<GameState>> = burnedCards.cards
                .map { BuildBurned(player, it) }
        val decision = Decision(player, Vector.ofAll(actions))
        return enqueue(decision)
    }

    fun addSelectWonderToBuildDecision(player: PlayerTurn): GameState {
        val playerCity = getPlayerCity(player)
        val opponentCity = getPlayerCity(player.opponent())
        val options: HashSet<Action<GameState>> = playerCity.unbuiltWonders
                .filter { playerCity.canBuild(it, opponentCity) != null }
                .map { BuildWonder(player, it) }
        val decision = Decision(player, Vector.ofAll(options))
        return enqueue(decision)

    }

    fun swapNextPlayer(): GameState {
        return update(nextPlayer_ = nextPlayer.opponent())
    }

    /**
     * Advances the game by one step by applying the given action to the next decision in the queue. Does not detect
     * cheating.
     */
    override fun applyAction(action: Action<GameState>, generator: RandomWithTracker): GameState {
        return action.process(this, generator)
    }

    override fun isQueueEmpty(): Boolean {
        return decisionQueue.isEmpty
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)
        availableProgressTokens.toJson(generator, "available_progress_tokens")
        discardedProgressTokens.toJson(generator, "discarded_progress_tokens")
        wondersForPick.toJson(generator, "wonders_for_pick")
        discardedWonders.toJson(generator, "unused_wonders")
        burnedCards.toJson(generator, "burned_cards")

        cardStructure?.toJson(generator, "card_structure")

        militaryBoard.toJson(generator, "military_board")

        player1City.toJson(generator, "player_1_city")
        player2City.toJson(generator, "player_2_city")

        generator.writeStartArray("decision_queue")
        decisionQueue.forEach { it.toJson(generator, null) }
        generator.writeEnd()

        generator.write("game_phase", gamePhase.toString())
        generator.write("next_player", nextPlayer.toString())

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
                availableProgressTokens.cards.fold("", { acc, s -> "$acc  ${s.name}\n"}) + "\n",
                "Discarded progress tokens:\n",
                discardedProgressTokens.cards.fold("", { acc, s -> "$acc  ${s.name}\n"}) + "\n",
                "Wonders for picking:\n",
                wondersForPick.cards.fold("", { acc, s -> "$acc  ${s.name}\n"}) + "\n",
                "Discarded wonders:\n",
                discardedWonders.cards.fold("", { acc, s -> "$acc  ${s.name}\n"}) + "\n",
                "Burned cards:\n",
                burnedCards.cards.fold("", { acc, s -> "$acc  ${s.name}\n"}) + "\n",
                "Military board:\n${militaryBoard.toString().replace("\n", "\n  ")}\n\n",
                "Next player: $nextPlayer\n",
                "Decision queue:\n" +
                        decisionQueue.mapIndexed { index, decision -> "  #$index (${decision.player}) options:\n" +
                                decision.options.fold("", { acc, s -> "$acc    $s\n"}) + "\n"
                        }
        )
        return ret.toString()
    }

    companion object {

        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

        fun loadFromJson(obj: JSONObject): GameState {
            val availableProgressTokens = Deck.loadFromJson(obj.getJSONObject("available_progress_tokens"))
            val discardedProgressTokens = Deck.loadFromJson(obj.getJSONObject("discarded_progress_tokens"))
            val wondersForPick = Deck.loadFromJson(obj.getJSONObject("wonders_for_pick"))
            val discardedWonders = Deck.loadFromJson(obj.getJSONObject("unused_wonders"))
            val burnedCards = Deck.loadFromJson(obj.getJSONObject("burned_cards"))
            val militaryBoard = MilitaryBoard.loadFromJson(obj.getJSONObject("military_board"))
            val cardStructure = CardStructure.loadFromJson(obj.getJSONObject("card_structure"))
            val player1City = PlayerCity.loadFromJson(obj.getJSONObject("player_1_city"))
            val player2City = PlayerCity.loadFromJson(obj.getJSONObject("player_2_city"))

            val nextPlayer = when (obj.getString("next_player")) {
                "PLAYER_1" -> PlayerTurn.PLAYER_1
                "PLAYER_2" -> PlayerTurn.PLAYER_2
                else -> throw Exception("Player unknown ${obj.getString("next_player")}")
            }

            val buildBuildingPattern = Regex("Build ([A-Za-z ]+)")
            val buildBurnedPattern = Regex("Build burned card ([A-Za-z ]+)")
            val buildWonderPattern = Regex("Build wonder ([A-Za-z ]+)")
            val burnForCoinsPattern = Regex("Burn ([A-Za-z ]+) for coins")
            val burnForWonderPattern = Regex("Burn ([A-Za-z ]+) to build wonder")
            val burnOpponentCardPattern = Regex("Burn opponent card ([A-Za-z ]+)")
            val chooseNextPlayerPattern = Regex("Choose ([A-Za-z_90-9]+) as next player")
            val chooseStartingWonderPattern = Regex("Choose ([A-Za-z ]+) as starting wonder")
            val chooseProgressTokenPattern = Regex("Choose progress token ([A-Za-z ]+)")
            val chooseUnusedProgressTokenPattern = Regex("Choose unused progress token ([A-Za-z ]+)")

            val decisionQueue = Queue.ofAll<Decision<GameState>>(obj.getJSONArray("decision_queue").map { decisionObj ->
                decisionObj as JSONObject
                val player = when (decisionObj.getString("player")) {
                    "PLAYER_1" -> PlayerTurn.PLAYER_1
                    "PLAYER_2" -> PlayerTurn.PLAYER_2
                    else -> throw Exception("Player unknown ${obj.getString("next_player")}")
                }
                val options = Vector.ofAll(decisionObj.getJSONArray("options").map { option ->
                    option as String
                    when (option) {
                        in buildBurnedPattern -> {
                            val cardName = buildBurnedPattern.matchEntire(option)!!.groupValues[1]
                            BuildBurned(player, CardFactory.getByName(cardName))
                        }
                        in buildWonderPattern -> {
                            val cardName = buildWonderPattern.matchEntire(option)!!.groupValues[1]
                            BuildWonder(player, CardFactory.getByName(cardName))
                        }
                        in buildBuildingPattern -> {
                            val cardName = buildBuildingPattern.matchEntire(option)!!.groupValues[1]
                            BuildBuilding(player, CardFactory.getByName(cardName))
                        }
                        in burnForCoinsPattern -> {
                            val cardName = burnForCoinsPattern.matchEntire(option)!!.groupValues[1]
                            BurnForMoney(player, CardFactory.getByName(cardName))
                        }
                        in burnForWonderPattern -> {
                            val cardName = burnForWonderPattern.matchEntire(option)!!.groupValues[1]
                            BurnForWonder(player, CardFactory.getByName(cardName))
                        }
                        in burnOpponentCardPattern -> {
                            val cardName = burnOpponentCardPattern.matchEntire(option)!!.groupValues[1]
                            BurnOpponentCard(player, CardFactory.getByName(cardName))
                        }
                        in chooseNextPlayerPattern -> {
                            val playerStr = chooseNextPlayerPattern.matchEntire(option)!!.groupValues[1]
                            val playerChoice = when (playerStr) {
                                "PLAYER_1" -> PlayerTurn.PLAYER_1
                                "PLAYER_2" -> PlayerTurn.PLAYER_2
                                else -> throw Exception("Player unknown ${obj.getString("next_player")}")
                            }
                            ChooseNextPlayer(player, playerChoice)
                        }
                        in chooseStartingWonderPattern -> {
                            val cardName = chooseStartingWonderPattern.matchEntire(option)!!.groupValues[1]
                            BuildBuilding(player, CardFactory.getByName(cardName))
                        }
                        in chooseProgressTokenPattern -> {
                            val cardName = chooseProgressTokenPattern.matchEntire(option)!!.groupValues[1]
                            ChooseProgressToken(player, CardFactory.getByName(cardName))
                        }
                        in chooseUnusedProgressTokenPattern -> {
                            val cardName = chooseUnusedProgressTokenPattern.matchEntire(option)!!.groupValues[1]
                            ChooseUnusedProgressToken(player, CardFactory.getByName(cardName))
                        }
                        else -> throw Exception("Action $option not found")
                    }
                })
                Decision(player, options)
            })

            val gamePhase = when (obj.getString("game_phase")) {
                "WONDERS_SELECTION" -> GamePhase.WONDERS_SELECTION
                "FIRST_AGE" -> GamePhase.FIRST_AGE
                "SECOND_AGE" -> GamePhase.SECOND_AGE
                "THIRD_AGE" -> GamePhase.THIRD_AGE
                "SCIENCE_SUPREMACY" -> GamePhase.SCIENCE_SUPREMACY
                "MILITARY_SUPREMACY" -> GamePhase.MILITARY_SUPREMACY
                "CIVILIAN_VICTORY" -> GamePhase.CIVILIAN_VICTORY
                else -> throw Exception("Player unknown ${obj.getString("next_player")}")
            }
            return GameState(
                    availableProgressTokens = availableProgressTokens,
                    discardedProgressTokens = discardedProgressTokens,
                    wondersForPick = wondersForPick,
                    discardedWonders = discardedWonders,
                    burnedCards = burnedCards,
                    militaryBoard = militaryBoard,
                    cardStructure = cardStructure,
                    player1City = player1City,
                    player2City = player2City,
                    gamePhase = gamePhase,
                    nextPlayer = nextPlayer,
                    decisionQueue = decisionQueue
            )
        }
    }

}