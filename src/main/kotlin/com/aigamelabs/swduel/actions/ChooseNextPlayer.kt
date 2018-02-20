package com.aigamelabs.swduel.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.swduel.GameState
import java.util.logging.Logger

class ChooseNextPlayer(playerTurn: PlayerTurn, private val chosenPlayer: PlayerTurn) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState.update(nextPlayer_ = chosenPlayer)
                .addMainTurnDecision(generator, logger)
    }

    override fun toString(): String {
        return "Choose $chosenPlayer as next player"
    }
}