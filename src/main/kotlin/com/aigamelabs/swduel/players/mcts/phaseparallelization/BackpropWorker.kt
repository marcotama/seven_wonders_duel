package com.aigamelabs.swduel.players.mcts.phaseparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.players.mcts.TreeNode

/**
 * Executes UCT on the given node.
 */
class BackpropWorker(
        /** Reference to the manager  */
        internal var manager: PhaseParallelizationManager) : Runnable {

    override fun run() {
        while (true) {
            try {
                val pair = manager.readyForBackprop.take()
                backpropagate(pair.first, pair.second)
            } catch (ignored: Exception) {
            }

        }
    }


    /**
     * Performs an iteration of UCT.
     */
    private fun backpropagate(leafNode: TreeNode, gameState: GameState) {
        var node = leafNode
        val playerScore = getPlayerScore(gameState)
        val opponentScore = getOpponentScore(gameState)
        while (true) {
            node.updateScore(playerScore, opponentScore)
            if (node.parent == null)
                break
            node = node.parent!!
        }
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState the game state used for evaluation
     * @return The score of a node
     */
    private fun getPlayerScore(gameState: GameState): Double {
        return manager.playerNodeEvaluator.calcScore(gameState)
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState the game state used for evaluation
     * @return The score of a node
     */
    private fun getOpponentScore(gameState: GameState): Double {
        return manager.opponentNodeEvaluator.calcScore(gameState)
    }
}
