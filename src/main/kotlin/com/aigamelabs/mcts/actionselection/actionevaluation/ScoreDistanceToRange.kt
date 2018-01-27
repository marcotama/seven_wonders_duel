package com.aigamelabs.mcts.actionselection.actionevaluation

import com.aigamelabs.mcts.TreeNode

/**
 * Returns an ActionEvaluator that returns the distance of the score of the action from the interval
 * [`rangeMin`,`rangeMax`]. 0 is returned if the score is within the interval.
 *
 * @param rangeMin The lower end of the range.
 * @param rangeMax The higher end of the range.
 */
class ScoreDistanceToRange(private val rangeMin: Double, private val rangeMax: Double): ActionEvaluator {
    override fun getValue(n: TreeNode): Double {
        val avgScore = Math.abs(n.score / n.games)
        return when {
            avgScore < rangeMin -> rangeMin - avgScore
            avgScore > rangeMax -> avgScore - rangeMax
            else -> 0.0
        }
    }
}