package com.aigamelabs.mcts.stateevaluation

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn

abstract class StateEvaluator(protected var playerNumber: PlayerTurn) {
    abstract fun calcScore(gameState: GameState): Double
}