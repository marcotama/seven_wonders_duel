package com.aigamelabs.tictactoe.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.tictactoe.GameState
import java.util.logging.Logger

class PlaceTile(player: PlayerTurn, val location: Pair<Int, Int>) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState
                    .place(player, location)
                    .addDecision(player)
    }

    override fun toString(): String {
        return "Place tile $location"
    }

    fun compareLocation(sameLocation: Pair<Int,Int>): Boolean {
        return location.first == sameLocation.first && location.second == sameLocation.second
    }
}