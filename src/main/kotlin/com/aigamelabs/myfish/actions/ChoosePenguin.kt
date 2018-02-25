package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class ChoosePenguin(player: PlayerTurn, private val penguinId: Int) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState.addMovePenguinDecision(player, penguinId)
    }

    override fun toString(): String {
        return "Choose penguin $penguinId for next move"
    }
}