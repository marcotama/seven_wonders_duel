package com.aigamelabs.mcts.actionselection.actionevaluation

import com.aigamelabs.mcts.TreeNode

/**
 * Returns an ActionEvaluator that returns the number of visits of the action.
 */
class NumVisits: ActionEvaluator {
    override fun getValue(n: TreeNode): Double {
        return n.games.toDouble()
    }
}