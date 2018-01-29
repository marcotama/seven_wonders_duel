package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseNextPlayer(playerTurn: PlayerTurn, private val chosenPlayer: PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {
        val decision = DecisionFactory.makeTurnDecision(chosenPlayer, gameState)
        val newDecisionQueue = gameState.decisionQueue.enqueue(decision)
        return gameState.update(decisionQueue_ = newDecisionQueue, nextPlayer_ = chosenPlayer)
    }

    override fun toString(): String {
        return "Choose $chosenPlayer as next player"
    }
}