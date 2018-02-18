package com.aigamelabs.game

abstract class Player<T: AbstractGameState<T>>(
        var name: String,
        val gameData: GameData
) {
    abstract fun init()
    /**
     * Decide an action to undertake. The action is one of the options contained in the first decision in the decision
     * queue of the game state.
     */
    abstract fun decide(gameState: T) : Action<T>
    abstract fun finalize(gameState: T)
    abstract fun close()
}