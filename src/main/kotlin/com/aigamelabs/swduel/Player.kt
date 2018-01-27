package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import io.vavr.collection.Vector

abstract class Player(
        var name: String,
        val gameData: GameData
) {
    abstract fun decide(gameState: GameState, options: Vector<Action>) : Action
    abstract fun finalize(gameState: GameState)
    abstract fun close()
}