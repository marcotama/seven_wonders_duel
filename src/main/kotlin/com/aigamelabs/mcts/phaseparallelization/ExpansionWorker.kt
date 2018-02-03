package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.utils.RandomWithTracker
import java.util.*

/**
 * Executes UCT on the given node.
 */
class ExpansionWorker(
        /** Reference to the manager  */
        internal var manager: PhaseParallelizationManager) : Runnable {

    private val generator = RandomWithTracker(Random().nextLong())

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
            node.createChildren(generator)
        }
    }
}
