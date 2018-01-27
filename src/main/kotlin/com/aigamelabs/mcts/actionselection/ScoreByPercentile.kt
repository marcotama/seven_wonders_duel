package com.aigamelabs.mcts.actionselection

import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.mcts.actionselection.actionevaluation.AverageScore

/**
 * An ActionSelector that returns the action with the score of the given percentile.
 * Ties are broken randomly.
 */
class ScoreByPercentile(private val rankPercentile: Double) : ActionSelector() {
    override fun chooseBestNode(nodes: Array<TreeNode>): Int {
        val rank: Int
        if (rankPercentile >= 1)
            rank = nodes.size - 1
        else if (rankPercentile < 0)
            rank = 0
        else
            rank = (rankPercentile * nodes.size).toInt()
        val values = computeValues(AverageScore(), nodes)
        return Util.indexOfRank(values, rank)
    }
}
