package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class PlacePenguin(player: PlayerTurn, private val location: Triple<Int, Int, Int>) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {

        val targetNumPenguins = when (gameState.numPlayers) {
            2 -> 4
            3 -> 3
            4 -> 2
            else -> throw Exception("Number of players not allowed: ${gameState.numPlayers}")
        }
        val placementDone = gameState.penguins.keySet()
                .all {
                    val playerPenguins = gameState.penguins
                            .get(it)
                            .getOrElseThrow { Exception("There is no such player: $player") }
                            .size()
                    playerPenguins == targetNumPenguins
                }


        val penguinId = gameState.penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .size()

        return if (placementDone)
            gameState
                    .placePenguin(player, penguinId, location)
                    .addChoosePenguinDecision(player)
        else
            gameState
                    .placePenguin(player, penguinId, location)
                    .addPlacePenguinDecision(player)
    }

    override fun toString(): String {
        return "Place penguin to location $location"
    }
}