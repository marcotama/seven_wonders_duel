package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.Player
import com.aigamelabs.swduel.GameState
import io.vavr.collection.Vector
import java.util.*

class RandomPlayer(name: String, gameData: GameData) : Player(name, gameData) {
    private val generator = Random()

    override fun decide(gameState: GameState, options: Vector<Action>): Action {
        return options[generator.nextInt(options.size())]
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}