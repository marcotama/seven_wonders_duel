package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import com.aigamelabs.myfish.enums.PenguinId
import com.aigamelabs.utils.RandomWithTracker
import java.util.logging.Logger

class ChoosePenguin(player: PlayerTurn, val penguinId: PenguinId) : Action<GameState>(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        return gameState.addMovePenguinDecision(player, penguinId)
    }

    override fun toString(): String {
        return "Choose penguin $penguinId for next move"
    }

    fun getPlayerTurn(): PlayerTurn {return player}
}