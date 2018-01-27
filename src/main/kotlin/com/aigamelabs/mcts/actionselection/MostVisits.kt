package com.aigamelabs.mcts.actionselection

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.mcts.actionselection.actionevaluation.NumVisits

/**
 * An ActionSelector that returns the action with the highest number of visits.
 * Ties are broken randomly.
 */
class MostVisits : ActionSelector() {
    override fun chooseBestNode(nodes: Array<TreeNode>): Int {
        val values = computeValues(NumVisits(), nodes)
        return Util.indexOfMax(values)
    }
}
