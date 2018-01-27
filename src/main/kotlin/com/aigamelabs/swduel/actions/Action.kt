package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.utils.RandomWithTracker

abstract class Action(val playerTurn: PlayerTurn) {
    abstract fun process(gameState: GameState, generator : RandomWithTracker?) : GameState
}