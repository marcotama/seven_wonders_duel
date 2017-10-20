package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.enums.GamePhase

data class GameState (
        val activeDeck : GameDeck,
        val decks : HashMap<GameDeck, Deck>,
        val cardStructure : CardStructure,
        val progressTokens : HashSet<ProgressToken>,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val decisionQueue: Queue<Decision>,
        val gamePhase: GamePhase
) {

    fun update(
            activeDeck_ : GameDeck? = null,
            decks_ : HashMap<GameDeck, Deck>? = null,
            cardStructure_ : CardStructure? = null,
            progressTokens_ : HashSet<ProgressToken>? = null,
            militaryBoard_ : MilitaryBoard? = null,
            playerCities_ : HashMap<PlayerTurn,PlayerCity>? = null,
            decisionQueue_ : Queue<Decision>? = null,
            gamePhase_: GamePhase? = null
    ) : GameState {
        return GameState(
                activeDeck_ ?: activeDeck,
                decks_ ?: decks,
                cardStructure_ ?: cardStructure,
                progressTokens_ ?: progressTokens,
                militaryBoard_ ?: militaryBoard,
                playerCities_ ?: playerCities,
                decisionQueue_ ?: decisionQueue,
                gamePhase_ ?: gamePhase
        )
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return playerCities.get(playerTurn).getOrElseThrow { Exception("Active deck not found") }
    }

    fun enqueueDecision (decision: Decision) : GameState {
        return update(decisionQueue_ = decisionQueue.enqueue(decision))
    }


    fun dequeueDecision() : Pair<Decision,GameState> {
        val dequeueOutcome = decisionQueue.dequeue()
        val decision = dequeueOutcome._1
        val newQueue = dequeueOutcome._2
        val newGameState = update(decisionQueue_ = newQueue)
        return Pair(decision, newGameState)
    }

    fun allAvailableAction () : Vector<Action> {
        return Vector.empty() //TODO build all available actions
    }

    fun initGamePhase() {
        when (gamePhase) {
            GamePhase.WONDERS_SELECTION_1 -> {
                // create deck of 4 wonders
                // add decision for P1 to choose 1 wonder
                // ChooseStartingWonder.process should take care of adding the following ChooseStartingWonder decision
            }
            GamePhase.WONDERS_SELECTION_2 -> {
                // create deck of 4 wonders
                // add decision for P2 to choose 1 wonder
                // ChooseStartingWonder.process should take care of adding the following ChooseStartingWonder decision
            }
            GamePhase.FIRST_AGE -> {
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
            }
            GamePhase.SECOND_AGE -> {
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
                // add decision for who starts this age
            }
            GamePhase.THIRD_AGE -> {
                val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure()
                // add decision for who starts this age
            }
            GamePhase.MILITARY_SUPREMACY -> {}
            GamePhase.SCIENCE_SUPREMACY -> {}
            GamePhase.CIVILIAN_VICTORY -> {}
        }
    }
}