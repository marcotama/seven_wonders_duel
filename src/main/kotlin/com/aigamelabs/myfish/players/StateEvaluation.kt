package com.aigamelabs.myfish.players

import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState

class StateEvaluation {

    companion object {
        fun get(
                stateEvaluator: StateEvaluator
        ): (GameState) -> Double {
            when (stateEvaluator) {
                StateEvaluator.PLAYER_1_VICTORY -> return {
                    val winners = it.calcWinner()._1
                    when {
                        winners.contains(PlayerTurn.PLAYER_1) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_1) -> 0.5
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_2_VICTORY -> return {
                    val winners = it.calcWinner()._1
                    when {
                        winners.contains(PlayerTurn.PLAYER_2) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_2) -> 0.5
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_3_VICTORY -> return {
                    val winners = it.calcWinner()._1
                    when {
                        winners.contains(PlayerTurn.PLAYER_3) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_3) -> 0.5
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_4_VICTORY -> return {
                    val winners = it.calcWinner()._1
                    when {
                        winners.contains(PlayerTurn.PLAYER_4) && winners.size() == 1 -> 1.0
                        winners.contains(PlayerTurn.PLAYER_4) -> 0.5
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
    PLAYER_2_VICTORY,
    PLAYER_3_VICTORY,
    PLAYER_4_VICTORY,
}