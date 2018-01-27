package com.aigamelabs.mcts.nodeevaluation

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn

abstract class NodeEvaluator(protected var playerNumber: PlayerTurn) {
    abstract fun calcScore(gameState: GameState): Double
}