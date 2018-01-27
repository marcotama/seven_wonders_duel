package com.aigamelabs.mcts.actionselection

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.mcts.actionselection.actionevaluation.ScoreDistanceToRange

/**
 * An ActionSelector that returns the action with the score closest to the interval
 * [-fuzzyIntervalSize,+fuzzyIntervalSize]. Actions inside the interval are evaluated equally.
 * Ties are broken randomly.
 */
class ScoreClosestToRange(private val rangeMin: Double, private val rangeMax: Double) : ActionSelector() {
    override fun chooseBestNode(nodes: Array<TreeNode>): Int {
        val values = computeValues(ScoreDistanceToRange(rangeMin, rangeMax), nodes)
        return Util.indexOfMin(values)
    }
}
