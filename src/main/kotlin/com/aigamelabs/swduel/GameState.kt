package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.ChooseNextPlayer
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import java.util.Random

data class GameState (
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
        private val defaultPlayer : PlayerTurn
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
            defaultPlayer_: PlayerTurn? = null
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
                defaultPlayer_ ?: defaultPlayer
        )
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return playerCities.get(playerTurn)
                .getOrElseThrow { Exception("Player city not found") }
    }

    fun switchToNextAge(generator: Random?) : GameState {
        when (gamePhase) {
            GamePhase.WONDERS_SELECTION -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure(generator)
                // Add main turn decision
                val decision = DecisionFactory.makeTurnDecision(defaultPlayer, this, true)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.FIRST_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }
            GamePhase.FIRST_AGE -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeSecondCardStructure(generator)
                // Create decision for starting player
                val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: defaultPlayer
                val actions = PlayerTurn.values().map { p -> ChooseNextPlayer(choosingPlayer, p) }
                val decision = Decision(choosingPlayer, Vector.ofAll(actions), false)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.SECOND_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }
            GamePhase.SECOND_AGE -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure(generator)
                // Create decision for starting player
                val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: defaultPlayer
                val actions = PlayerTurn.values().map { p -> ChooseNextPlayer(choosingPlayer, p) }
                val decision = Decision(choosingPlayer, Vector.ofAll(actions), false)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.THIRD_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }

            GamePhase.THIRD_AGE -> {
                return update(gamePhase_ = GamePhase.CIVILIAN_VICTORY)
            }
            else -> {
                throw Exception("There is no next phase after " + gamePhase.name)
            }
        }
    }

    fun checkScienceSupremacy(playerTurn: PlayerTurn) : GameState {
        // Count science symbols
        val playerCity = getPlayerCity(playerTurn)
        val hasLawToken = !playerCity.progressTokens.filter { c -> c.enhancement == Enhancement.LAW }.isEmpty
        val symbolsFromGreenCards = playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size()
        val distinctScienceSymbols = symbolsFromGreenCards + if (hasLawToken) 1 else 0

        return if (distinctScienceSymbols >= 6) {
            update(gamePhase_ = GamePhase.SCIENCE_SUPREMACY)
        }
        else {
            this
        }
    }

    fun checkMilitarySupremacy() : GameState {
        return if (militaryBoard.isMilitarySupremacy()) {
            update(gamePhase_ = GamePhase.MILITARY_SUPREMACY)
        }
        else {
            this
        }
    }

    fun calculateVictoryPoints(player : PlayerTurn) : Int {
        val playerCity = getPlayerCity(player)
        val opponentCity = getPlayerCity(player.opponent())
        val totalFromBuildings = playerCity.buildings
                .filter { it.victoryPointsFormula == Formula.ABSOLUTE }
                .map { it.victoryPoints * getMultiplier(it.victoryPointsFormula, it.referenceCity, playerCity, opponentCity) }
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
}