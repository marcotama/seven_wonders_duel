package com.aigamelabs.mcts.actionselection

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.mcts.actionselection.actionevaluation.AverageScore

/**
 * An ActionSelector that returns the action with the median score.
 * Ties are broken randomly.
 */
class MedianScore : ActionSelector() {
    override fun chooseBestNode(nodes: Array<TreeNode>): Int {
        val values = computeValues(AverageScore(), nodes)
        return Util.indexOfMedian(values)
    }
}
