package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class ChoosePenguin(player: PlayerTurn, private val penguinId: Int) : Action<GameState>(player) {
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
        return if (placementDone)
            gameState.addMovePenguinDecision(player, penguinId)
        else
            gameState.addPlacePenguinDecision(player)
    }

    override fun toString(): String {
        return "Choose penguin $penguinId for next move"
    }
}