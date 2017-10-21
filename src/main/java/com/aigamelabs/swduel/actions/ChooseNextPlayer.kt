package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseNextPlayer(playerTurn: PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        val decision = DecisionFactory.makeTurnDecision(playerTurn, gameState, true)
        return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision))
    }
}