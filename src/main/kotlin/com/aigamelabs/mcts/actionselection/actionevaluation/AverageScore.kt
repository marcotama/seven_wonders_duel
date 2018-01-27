package com.aigamelabs.mcts.actionselection.actionevaluation

import com.aigamelabs.mcts.TreeNode

/**
 * Returns an ActionEvaluator that returns the score of the action.
 */
class AverageScore: ActionEvaluator {
    override fun getValue(n: TreeNode): Double {
        return n.score / n.games.toDouble()
    }
}