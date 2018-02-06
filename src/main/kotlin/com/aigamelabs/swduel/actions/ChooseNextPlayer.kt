package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class ChooseNextPlayer(playerTurn: PlayerTurn, private val chosenPlayer: PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState.update(nextPlayer_ = chosenPlayer)
                .addMainTurnDecision(chosenPlayer)
    }

    override fun toString(): String {
        return "Choose $chosenPlayer as next player"
    }
}