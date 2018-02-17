package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.PlayerTurn

class StateEvaluation {

    companion object {
        fun get(
                stateEvaluator: StateEvaluator
        ): (GameState) -> Double {
            when (stateEvaluator) {
                StateEvaluator.PLAYER_1_VICTORY -> return { gameState ->
                    val (outcome, _, _) = gameState.calculateWinner()
                    when (outcome) {
                        GameOutcome.PLAYER_1_VICTORY -> 1.0
                        GameOutcome.PLAYER_2_VICTORY -> 0.0
                        GameOutcome.TIE -> 0.5
                    }
                }
                StateEvaluator.PLAYER_2_VICTORY -> return { gameState ->
                    val (outcome, _, _) = gameState.calculateWinner()
                    when (outcome) {
                        GameOutcome.PLAYER_1_VICTORY -> 0.0
                        GameOutcome.PLAYER_2_VICTORY -> 1.0
                        GameOutcome.TIE -> 0.5
                    }
                }
            }
        }

        fun getVictoryEvaluator(player: PlayerTurn): (GameState) -> Double {
            return when (player) {
                PlayerTurn.PLAYER_1 -> get(StateEvaluator.PLAYER_1_VICTORY)
                PlayerTurn.PLAYER_2 -> get(StateEvaluator.PLAYER_2_VICTORY)
            }
        }

    }
}

enum class StateEvaluator {
    PLAYER_1_VICTORY,
    PLAYER_2_VICTORY
}