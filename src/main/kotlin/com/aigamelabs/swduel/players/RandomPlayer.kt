package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.Player
import com.aigamelabs.swduel.GameState
import java.util.*

class RandomPlayer(name: String, gameData: GameData) : Player(name, gameData) {
    private val generator = Random()

    override fun decide(gameState: GameState): Action {
        val (_, thisDecision) = gameState.dequeAction()
        val options = thisDecision.options
        return options[generator.nextInt(options.size())]
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}