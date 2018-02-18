package com.aigamelabs.game

import com.aigamelabs.utils.RandomWithTracker
import java.util.logging.Logger

abstract class Action<T: AbstractGameState<T>>(val player: PlayerTurn) {
    abstract fun process(gameState: T, generator : RandomWithTracker, logger: Logger? = null) : T
}