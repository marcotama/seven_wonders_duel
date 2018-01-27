package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.swduel.enums.GamePhase
import java.util.*

/**
 * Executes UCT on the given node.
 */
class PlayoutWorker(internal var manager: PhaseParallelizationManager) : Runnable {

    override fun run() {
        while (true) {
            try {
                val node = manager.readyForPlayout.take()
                val gameState = playout(node)
                manager.readyForBackprop.put(Pair<TreeNode, GameState>(node, gameState))
            } catch (ignored: Exception) {
            }

        }
    }

    /**
     * Performs a playout (a simulated execution of tree moves followed by random moves).
     *
     * @return Result of the playout
     */
    private fun playout(node: TreeNode): GameState {

        var gameState = node.gameState

        // Apply random actions to the playout
        val activeGamePhases = setOf(GamePhase.FIRST_AGE, GamePhase.SECOND_AGE, GamePhase.THIRD_AGE, GamePhase.WONDERS_SELECTION)
        while (activeGamePhases.contains(gameState.gamePhase)) {
            val dequeueOutcome = gameState.dequeAction()
            gameState = dequeueOutcome.first
            val decision = dequeueOutcome.second
            val options = decision.options
            val choice = rnd.nextInt(options.size())
            gameState = gameState.applyAction(options[choice])
        }
        return gameState
    }

    companion object {
        private val rnd = Random()
    }
}
