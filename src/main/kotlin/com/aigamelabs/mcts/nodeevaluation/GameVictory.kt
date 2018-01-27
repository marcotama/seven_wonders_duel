package com.aigamelabs.mcts.nodeevaluation

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.PlayerTurn

/**
 * A NodeEvaluator that uses the HP difference of the players as score. A positive value means that the player is
 * winning, a negative value means that the player is losing. This value is then normalized (to be 0 and 1 respectively
 * at -BHPD and +BHPD, where BHPD is the HP difference in a baseline frame (or 50 if BHPD is less than that) and clipped.
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
