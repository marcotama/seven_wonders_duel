package com.aigamelabs.game

import com.aigamelabs.utils.RandomWithTracker
import javax.json.stream.JsonGenerator

abstract class IAbstractGameState<T: IAbstractGameState<T>> {
    abstract fun applyAction(action: Action<T>, generator: RandomWithTracker): T
    abstract fun isQueueEmpty(): Boolean
    abstract fun isGameOver(): Boolean
    abstract fun dequeAction() : Pair<T, Decision<T>>
    abstract override fun toString(): String
    abstract fun toJson(generator: JsonGenerator, name: String? = null)
}