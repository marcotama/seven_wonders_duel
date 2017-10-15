package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import io.vavr.collection.Vector

abstract class Player(var name: String) {
    abstract fun decide(gameState: GameState, options: Vector<Action>) : Action
}