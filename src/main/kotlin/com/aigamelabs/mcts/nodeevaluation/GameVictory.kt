package com.aigamelabs.mcts.nodeevaluation

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.PlayerTurn

/**
 * A NodeEvaluator that uses the outcome of the game as a measure.
 */
class GameVictory(playerNumber: PlayerTurn) : NodeEvaluator(playerNumber) {
    override fun calcScore(gameState: GameState): Double {
        val (outcome, _, _) = gameState.calculateWinner()
        return when (playerNumber) {
            PlayerTurn.PLAYER_1 ->
                when (outcome) {
                    GameOutcome.PLAYER_1_VICTORY -> 1.0
                    GameOutcome.PLAYER_2_VICTORY -> 0.0
                    GameOutcome.TIE -> 0.5
                }
            PlayerTurn.PLAYER_2 ->
                when (outcome) {
                    GameOutcome.PLAYER_1_VICTORY -> 0.0
                    GameOutcome.PLAYER_2_VICTORY -> 1.0
                    GameOutcome.TIE -> 0.5
                }
        }
    }
}
