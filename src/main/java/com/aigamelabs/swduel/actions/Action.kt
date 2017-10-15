package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState

abstract class Action {
    abstract fun process(gameState: GameState) : GameState
}