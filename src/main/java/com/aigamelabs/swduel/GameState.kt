package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.ChooseNextPlayer
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector

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
        private val gamePhase: GamePhase,
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

    fun switchToNextAge() : GameState {
        when (gamePhase) {
            GamePhase.WONDERS_SELECTION -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
                // Add main turn decision
                val decision = DecisionFactory.makeTurnDecision(defaultPlayer, this, true)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.FIRST_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }
            GamePhase.FIRST_AGE -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
                // Create decision for starting player
                val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: defaultPlayer
                val actions = PlayerTurn.values().map { p -> ChooseNextPlayer(choosingPlayer, p) }
                val decision = Decision(choosingPlayer, Vector.ofAll(actions), false)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.SECOND_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }
            GamePhase.SECOND_AGE -> {
                // Setup cards structure
                val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure()
                // Create decision for starting player
                val choosingPlayer = militaryBoard.getDisadvantagedPlayer() ?: defaultPlayer
                val actions = PlayerTurn.values().map { p -> ChooseNextPlayer(choosingPlayer, p) }
                val decision = Decision(choosingPlayer, Vector.ofAll(actions), false)

                return update(cardStructure_ = newCardStructure, gamePhase_ = GamePhase.THIRD_AGE,
                        decisionQueue_ = decisionQueue.enqueue(decision))
            }

            GamePhase.THIRD_AGE -> {
                // TODO calculate victory points
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
        val hasLawToken = !playerCity.scienceTokens.filter { c -> c.enhancement == Enhancement.LAW }.isEmpty
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
}