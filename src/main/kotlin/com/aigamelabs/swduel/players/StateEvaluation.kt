package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.GamePhase

class StateEvaluation {

    companion object {
        fun get(
                stateEvaluator: StateEvaluator
        ): (GameState) -> Double {
            when (stateEvaluator) {
                StateEvaluator.PLAYER_1_VICTORY -> return {
                    val (outcome, _, _) = it.calculateWinner()
                    when (outcome) {
                        GameOutcome.PLAYER_1_VICTORY -> 1.0
                        GameOutcome.PLAYER_2_VICTORY -> 0.0
                        GameOutcome.TIE -> 0.5
                    }
                }
                StateEvaluator.PLAYER_2_VICTORY -> return {
                    val (outcome, _, _) = it.calculateWinner()
                    when (outcome) {
                        GameOutcome.PLAYER_1_VICTORY -> 0.0
                        GameOutcome.PLAYER_2_VICTORY -> 1.0
                        GameOutcome.TIE -> 0.5
                    }
                }
                StateEvaluator.PLAYER_1_SCIENCE_SUPREMACY -> return {
                    when {
                        it.testScienceSupremacy(PlayerTurn.PLAYER_1) -> 1.0
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_2_SCIENCE_SUPREMACY -> return {
                    when {
                        it.testScienceSupremacy(PlayerTurn.PLAYER_2) -> 1.0
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_1_MILITARY_SUPREMACY -> return {
                    when {
                        it.testMilitarySupremacy(PlayerTurn.PLAYER_1) -> 1.0
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_2_MILITARY_SUPREMACY -> return {
                    when {
                        it.testMilitarySupremacy(PlayerTurn.PLAYER_2) -> 1.0
                        else -> 0.0
                    }
                }
                StateEvaluator.PLAYER_1_CIVILIAN_VICTORY -> return {
                    if (it.gamePhase != GamePhase.CIVILIAN_VICTORY)
                        0.0
                    else {
                        val (outcome, _, _) = it.calculateWinner()
                        when (outcome) {
                            GameOutcome.PLAYER_1_VICTORY -> 1.0
                            GameOutcome.PLAYER_2_VICTORY -> 0.0
                            GameOutcome.TIE -> 0.5
                        }
                    }
                }
                StateEvaluator.PLAYER_2_CIVILIAN_VICTORY -> return {
                    if (it.gamePhase != GamePhase.CIVILIAN_VICTORY)
                        0.0
                    else {
                        val (outcome, _, _) = it.calculateWinner()
                        when (outcome) {
                            GameOutcome.PLAYER_1_VICTORY -> 0.0
                            GameOutcome.PLAYER_2_VICTORY -> 1.0
                            GameOutcome.TIE -> 0.5
                        }
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

        fun getCivilianVictoryEvaluator(player: PlayerTurn): (GameState) -> Double {
            return when (player) {
                PlayerTurn.PLAYER_1 -> get(StateEvaluator.PLAYER_1_CIVILIAN_VICTORY)
                PlayerTurn.PLAYER_2 -> get(StateEvaluator.PLAYER_2_CIVILIAN_VICTORY)
                else -> throw Exception("This game only allows 2 players; $player does not exist.")
            }
        }

        fun getScienceSupremacyEvaluator(player: PlayerTurn): (GameState) -> Double {
            return when (player) {
                PlayerTurn.PLAYER_1 -> get(StateEvaluator.PLAYER_1_SCIENCE_SUPREMACY)
                PlayerTurn.PLAYER_2 -> get(StateEvaluator.PLAYER_2_SCIENCE_SUPREMACY)
                else -> throw Exception("This game only allows 2 players; $player does not exist.")
            }
        }

        fun getMilitarySupremacyEvaluator(player: PlayerTurn): (GameState) -> Double {
            return when (player) {
                PlayerTurn.PLAYER_1 -> get(StateEvaluator.PLAYER_1_MILITARY_SUPREMACY)
                PlayerTurn.PLAYER_2 -> get(StateEvaluator.PLAYER_2_MILITARY_SUPREMACY)
                else -> throw Exception("This game only allows 2 players; $player does not exist.")
            }
        }

    }
}

enum class StateEvaluator {
    PLAYER_1_VICTORY,
    PLAYER_2_VICTORY,
    PLAYER_1_SCIENCE_SUPREMACY,
    PLAYER_2_SCIENCE_SUPREMACY,
    PLAYER_1_MILITARY_SUPREMACY,
    PLAYER_2_MILITARY_SUPREMACY,
    PLAYER_1_CIVILIAN_VICTORY,
    PLAYER_2_CIVILIAN_VICTORY,
}