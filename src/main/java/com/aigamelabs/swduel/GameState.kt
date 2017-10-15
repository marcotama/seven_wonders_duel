package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.Queue

data class GameState (
        val firstAgeDeck : Deck,
        val secondAgeDeck : Deck,
        val thirdAgeDeck : Deck,
        val wondersDeck : Deck,
        val burnedCardsDeck : Deck,
        val currentGraph : Graph<Card>?,
        val progressTokens : HashSet<ProgressToken>,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val gamePhase: GamePhase,
        val decisionQueue: Queue<Decision>
)

{
//    fun drawCard (card: Card) : GameState {
//
//
//    }

    fun enqueueDecision (decision: Decision) : GameState {
        return GameState(firstAgeDeck,secondAgeDeck,thirdAgeDeck,wondersDeck,burnedCardsDeck,currentGraph,progressTokens,militaryBoard,playerCities,gamePhase,decisionQueue.enqueue(decision))
    }

    fun dequeDecision() : Pair<Decision,GameState> {
        val dequeueOutcome = decisionQueue.dequeue()
        val decision = dequeueOutcome._1
        val newQueue = dequeueOutcome._2
        val newGameState = GameState(firstAgeDeck,secondAgeDeck,thirdAgeDeck,wondersDeck,burnedCardsDeck,currentGraph,progressTokens,militaryBoard,playerCities,gamePhase,newQueue)
        return Pair(decision, newGameState)

    }





}