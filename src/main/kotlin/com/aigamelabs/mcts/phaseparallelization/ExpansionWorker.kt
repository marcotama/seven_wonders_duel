package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.mcts.TreeNode

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
                expand(node)
                manager.readyForPlayout.put(node)
            } catch (ignored: Exception) {
            }

        }
    }

    /**
     * Performs an iteration of UCT.
     */
    private fun expand(node: TreeNode) {
        if (!node.hasChildren()
                && node.games >= manager.uctNodeCreateThreshold
                && node.depth < manager.maxUctTreeDepth) {
            node.createChildren()
        }
    }
}
