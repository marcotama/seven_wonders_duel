package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.Random

class ChooseNextPlayer(playerTurn: PlayerTurn, private val chosenPlayer: PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : Random?) : GameState {
        val decision = DecisionFactory.makeTurnDecision(chosenPlayer, gameState, true)
        return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision),
                defaultPlayer_ = chosenPlayer)
    }
}