package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class ChooseNextPlayer(playerTurn: PlayerTurn, private val chosenPlayer: PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {
        val newDecision = DecisionFactory.makeTurnDecision(chosenPlayer, gameState)
        val updatedDecisionQueue = gameState.decisionQueue.enqueue(newDecision)
        return gameState.update(decisionQueue_ = updatedDecisionQueue, nextPlayer_ = chosenPlayer)
    }

    override fun toString(): String {
        return "Choose $chosenPlayer as next player"
    }
}