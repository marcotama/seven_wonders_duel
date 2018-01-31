package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.TreeNode
import io.vavr.collection.Vector

/**
 * Executes UCT on the given node.
 */
class ExpansionWorker(
        /** Reference to the manager  */
        internal var manager: PhaseParallelizationManager) : Runnable {

    override fun run() {
        while (true) {
            try {
                val node = manager.readyForExpansion.take()
                expand(node, node.gameState.decisionQueue.first().options)
                manager.readyForPlayout.put(node)
            } catch (ignored: Exception) {
            }

        }
    }

    /**
     * Performs an iteration of UCT.
     */
    private fun expand(node: TreeNode, options: Vector<Action>) {
        if (!node.hasChildren()
                && node.games >= manager.uctNodeCreateThreshold
                && node.depth < manager.maxUctTreeDepth) {
            node.createChildren(options)
        }
    }
}
