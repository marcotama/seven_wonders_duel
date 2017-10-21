package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.*

class Game(private val players : HashMap<PlayerTurn,Player>) {
    fun mainLoop(startingGameState : GameState, generator : Random?) {
        var gameState = startingGameState
        while (!gameState.decisionQueue.isEmpty) {

            // Dequeue decision and enqueue the next one
            val dequeueOutcome = gameState.decisionQueue.dequeue()
            val thisDecision = dequeueOutcome._1
            val newDecisionsQueue = dequeueOutcome._2
            gameState = gameState.update(decisionQueue_ = newDecisionsQueue)

            // Query player for action
            val action = players[thisDecision.player]!!.decide(gameState, thisDecision.options)

            // Check for cheating
            if (!thisDecision.options.contains(action)) {
                throw Exception("Player cheated: selected action\n" + action +
                        "\nbut available actions are\n" + thisDecision.options)
            }

            // Process action
            gameState = action.process(gameState, generator)

            // If the cards structure is empty, switch to next age
            if (gameState.cardStructure!!.isEmpty()) {
                gameState = gameState.switchToNextAge(generator)
            }
        }
    }
}