package com.aigamelabs.tictactoe.players

import com.aigamelabs.game.Action
import com.aigamelabs.game.GameData
import com.aigamelabs.game.Player
import com.aigamelabs.tictactoe.GameState
import java.util.*

class RandomPlayer(name: String, gameData: GameData) : Player<GameState>(name, gameData) {
    private val generator = Random()

    override fun decide(gameState: GameState): Action<GameState> {
        val (_, thisDecision) = gameState.dequeDecision()
        val options = thisDecision.options
        return options[generator.nextInt(options.size())]
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}