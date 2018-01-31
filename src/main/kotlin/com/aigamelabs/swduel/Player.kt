package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action

abstract class Player(
        var name: String,
        val gameData: GameData
) {
    abstract fun init()
    /**
     * Decide an action to undertake. The action is one of the options contained in the first decision in the decision
     * queue of the game state.
     */
    abstract fun decide(gameState: GameState) : Action
    abstract fun finalize(gameState: GameState)
    abstract fun close()
}