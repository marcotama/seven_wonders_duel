package com.aigamelabs.mcts.actionselection.actionevaluation

import com.aigamelabs.mcts.TreeNode

/**
 * An ActionEvaluator that returns the distance of the score of the action from a target `value`.
 *
 * @param value Half the size of the interval specified above.
 */
class ScoreDistanceToValue(private val value: Double): ActionEvaluator {
    override fun getValue(n: TreeNode): Double {
        val avgScore = Math.abs(n.score / n.games)
        return if (avgScore <= value) {
            value - avgScore
        } else {
            avgScore - value
        }
    }
}