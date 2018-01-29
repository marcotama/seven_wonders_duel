package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import java.util.*

/**
 * Executes UCT on the given node.
 */
class SelectionWorker(internal var manager: PhaseParallelizationManager) : Runnable {

    private val generator = RandomWithTracker(Random().nextLong())

    override fun run() {
        while (true) {
            try {
                while (!manager.running) {
                    Thread.sleep(1)
                }

                val readyForExpansion = select()
                manager.readyForExpansion.put(readyForExpansion)
            } catch (ignored: Exception) {
            }

        }
    }

    /**
     * Performs an iteration of UCT.
     */
    private fun select(): TreeNode {

        var currentNode = manager.rootNode!!

        // Descend the root node
        while (currentNode.hasChildren()) {
            if (currentNode.nodeType == NodeType.STOCHASTIC_NODE) {
                val parent = currentNode.parent!!
                // Re-apply parent action to parent game state to sample another game state for the child
                val newGameState = parent.gameState.applyAction(parent.selectedAction!!, generator)
                // The random integers generated during the action application are the unique identifier for the child
                val childId = generator.popAll()
                // If a child with that id does not exist, create it
                if (!currentNode.children!!.containsKey(childId))
                    currentNode.children!![childId] = TreeNode(currentNode, currentNode.childrenType, null, newGameState, manager)
                // Descend in the child with the calculated id
                currentNode = currentNode.children!![childId]!!
            }
            else {
                // Search non-visited nodes
                val nonVisitedChildren = currentNode.children!!.values
                        .filterTo(LinkedList()) { it.games == 0 }

                // If any, choose one
                if (nonVisitedChildren.size > 0) {
                    currentNode = nonVisitedChildren[rnd.nextInt(nonVisitedChildren.size)]
                } else {
                    val childrenValues = DoubleArray(currentNode.children!!.size)
                    for (i in 0 until currentNode.children!!.size) {
                        childrenValues[i] = currentNode.children!![listOf(i)]!!.calcUcb()
                    }
                    currentNode = currentNode.children!![listOf(Util.indexOfMax(childrenValues))]!!
                }// Otherwise choose the child with highest UCB score
            }
        }
        return currentNode
    }

    companion object {
        private val rnd = Random()
    }
}
