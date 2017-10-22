package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.HashSet
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

        val endGamePhases = HashSet.of(GamePhase.MILITARY_SUPREMACY, GamePhase.SCIENCE_SUPREMACY, GamePhase.CIVILIAN_VICTORY)
        if (endGamePhases.contains(gameState.gamePhase)) {
            val p1VictoryPoints = gameState.calculateVictoryPoints(PlayerTurn.PLAYER_1)
            val p2VictoryPoints = gameState.calculateVictoryPoints(PlayerTurn.PLAYER_2)
            when {
                p1VictoryPoints > p2VictoryPoints -> System.out.println("Player 1 wins with $p1VictoryPoints versus $p2VictoryPoints")
                p2VictoryPoints > p1VictoryPoints -> System.out.println("Player 2 wins with $p1VictoryPoints versus $p2VictoryPoints")
                else -> System.out.println("Players scored the same amount of points: $p1VictoryPoints")
            }
        }
        else {
            throw Exception("Something went wrong: we left the game loop but the game phase is not final:\n${gameState.gamePhase}")
        }
    }
}