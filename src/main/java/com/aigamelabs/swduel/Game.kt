package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn

class Game(private val players : HashMap<PlayerTurn,Player>) {
    fun mainLoop(startingGameState : GameState) {
        var gameState = startingGameState
        while (!gameState.decisionQueue.isEmpty) {
            // Deque decision
            val dequeOutcome = gameState.dequeueDecision() // TODO rename to deque
            val nextDecision = dequeOutcome.first
            gameState = dequeOutcome.second

            // Query player for action
            val action = players[nextDecision.player]!!.decide(gameState, nextDecision.options)

            // Check for cheating
            if (!nextDecision.options.contains(action)) {
                throw Exception("Player cheated: selected action\n" + action +
                        "\nbut available actions are\n" + nextDecision.options)
            }

            // Process action
            gameState = action.process(gameState)
        }
    }
}