package com.aigamelabs.mcts.actionselection

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.mcts.actionselection.actionevaluation.ScoreDistanceToValue

/**
 * An ActionSelector that returns the action with the score closest to zero.
 * Ties are broken randomly.
 */
class ScoreClosestToValue(private val target: Double) : ActionSelector() {

    override fun chooseBestNode(nodes: Array<TreeNode>): Int {
        val values = computeValues(ScoreDistanceToValue(target), nodes)
        return Util.indexOfMin(values)
    }
}
