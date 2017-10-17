package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.actions.Build
import io.vavr.control.Option

data class GameState (
        val activeDeck : GameDeck,
        val decks : HashMap<GameDeck, Deck>,
        val currentGraph : Graph<Card>,
        val progressTokens : HashSet<ProgressToken>,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val decisionQueue: Queue<Decision>
) {

    fun update(
            activeDeck_ : GameDeck? = null,
            decks_ : HashMap<GameDeck, Deck>? = null,
            currentGraph_ : Graph<Card>? = null,
            progressTokens_ : HashSet<ProgressToken>? = null,
            militaryBoard_ : MilitaryBoard? = null,
            playerCities_ : HashMap<PlayerTurn,PlayerCity>? = null,
            decisionQueue_ : Queue<Decision>? = null
    ) : GameState {
        return GameState(
                activeDeck_ ?: activeDeck,
                decks_ ?: decks,
                currentGraph_ ?: currentGraph,
                progressTokens_ ?: progressTokens,
                militaryBoard_ ?: militaryBoard,
                playerCities_ ?: playerCities,
                decisionQueue_ ?: decisionQueue
        )
    }

    fun getActiveDeck() : Deck {
        return decks.get(activeDeck).getOrElseThrow { -> Exception("Active deck not found") }
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return playerCities.get(playerTurn).getOrElseThrow { -> Exception("Active deck not found") }
    }

    fun enqueueDecision (decision: Decision) : GameState {
        return update(decisionQueue_ = decisionQueue.enqueue(decision))
    }


    fun dequeDecision() : Pair<Decision,GameState> {
        val dequeueOutcome = decisionQueue.dequeue()
        val decision = dequeueOutcome._1
        val newQueue = dequeueOutcome._2
        val newGameState = update(decisionQueue_ = newQueue)
        return Pair(decision, newGameState)
    }

    fun allAvailableAction () : Vector<Action> {
        return Vector.empty() //TODO build all available actions
    }
}