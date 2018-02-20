package com.aigamelabs.swduel.players

import com.aigamelabs.game.GameData
import com.aigamelabs.game.Action
import com.aigamelabs.game.Player
import com.aigamelabs.swduel.GameState
import java.util.*

class RandomPlayer(name: String, gameData: GameData) : Player<GameState>(name, gameData) {
    private val generator = Random()

    override fun decide(gameState: GameState): Action<GameState> {
        val (_, thisDecision) = gameState.dequeAction()
        val options = thisDecision.options
        return options[generator.nextInt(options.size())]
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}