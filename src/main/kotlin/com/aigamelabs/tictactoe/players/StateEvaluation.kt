package com.aigamelabs.tictactoe.players

import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.tictactoe.GameState

class StateEvaluation {

    companion object {
        fun get(
                stateEvaluator: StateEvaluator
        ): (GameState) -> Double {
            when (stateEvaluator) {
                StateEvaluator.PLAYER_1_VICTORY -> return {
                    val winners = it.calcWinner()
                    when {
                        winners.contains(PlayerTurn.PLAYER_1) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_1) -> 0.5
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_2_VICTORY -> return {
                    val winners = it.calcWinner()
                    when {
                        winners.contains(PlayerTurn.PLAYER_2) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_2) -> 0.5
                        else -> 0.0
                    }
                }

            }
        }

        fun getVictoryEvaluator(player: PlayerTurn): (GameState) -> Double {
            return when (player) {
                PlayerTurn.PLAYER_1 -> get(StateEvaluator.PLAYER_1_VICTORY)
                PlayerTurn.PLAYER_2 -> get(StateEvaluator.PLAYER_2_VICTORY)
                else -> throw Exception("This game only allows 2 players; $player does not exist.")
            }
        }

    }
}

enum class StateEvaluator {
    PLAYER_1_VICTORY,
    PLAYER_2_VICTORY
}