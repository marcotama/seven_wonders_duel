package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class MovePenguin(player: PlayerTurn, private val penguinId: Int, private val location: Triple<Int, Int, Int>) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState
                .movePenguin(player, penguinId, location)
                .addChoosePenguinDecision(player)
    }

    override fun toString(): String {
        return "Move penguin $penguinId to location $location"
    }
}