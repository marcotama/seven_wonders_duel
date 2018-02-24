package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class PlacePenguin(player: PlayerTurn, private val location: Triple<Int, Int, Int>) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        val penguinId = gameState.penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .size()
        return gameState
                .placePenguin(player, penguinId, location)
    }

    override fun toString(): String {
        return "Place penguin to location $location"
    }
}