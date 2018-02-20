package com.aigamelabs.mcts

import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.utils.Util

class ActionSelection {

    companion object {
        private fun <T: AbstractGameState<T>> computeValues(evaluator: (TreeNode<T>) -> Double, nodes: Array<TreeNode<T>>): DoubleArray {
            val values = DoubleArray(nodes.size)
            for (i in nodes.indices) {
                values[i] = evaluator(nodes[i])
            }
            return values
        }

        fun <T: AbstractGameState<T>> averageScore(): (TreeNode<T>) -> Double {
            return {
                it.score / it.games.toDouble()
            }
        }
        private fun <T: AbstractGameState<T>> numVisits(): (TreeNode<T>) -> Double {
            return {
                it.games.toDouble()
            }
        }
        private fun <T: AbstractGameState<T>> scoreDistanceToRange(rangeMin: Double, rangeMax: Double): (TreeNode<T>) -> Double {
            return {
                val avgScore = Math.abs(it.score / it.games)
                when {
                    avgScore < rangeMin -> rangeMin - avgScore
                    avgScore > rangeMax -> avgScore - rangeMax
                    else -> 0.0
                }
            }
        }

        private fun <T: AbstractGameState<T>> scoreDistanceToValue(value: Double): (TreeNode<T>) -> Double {
            return {
                val avgScore = Math.abs(it.score / it.games)
                if (avgScore <= value) {
                    value - avgScore
                } else {
                    avgScore - value
                }
            }
        }

        fun <T: AbstractGameState<T>> get(
                actionSelector: ActionSelector,
                rankPercentile: Double? = null,
                rangeMin: Double? = null,
                rangeMax: Double? = null,
                target: Double? = null
        ): (Array<TreeNode<T>>) -> Int {
            when (actionSelector) {
                ActionSelector.HIGHEST_SCORE -> return { nodes ->
                    val values = computeValues(averageScore(), nodes)
                    Util.indexOfMax(values)
                }
                ActionSelector.MEDIAN_SCORE -> return { nodes ->
                    val values = computeValues(averageScore(), nodes)
                    Util.indexOfMedian(values)
                }
                ActionSelector.MOST_VISITS -> return { nodes ->
                    val values = computeValues(numVisits(), nodes)
                    Util.indexOfMax(values)
                }
                ActionSelector.SCORE_BY_PERCENTILE -> return { nodes ->
                    rankPercentile!!
                    val rank = when {
                        rankPercentile >= 1 -> nodes.size - 1
                        rankPercentile < 0 -> 0
                        else -> (rankPercentile * nodes.size).toInt()
                    }
                    val values = computeValues(averageScore(), nodes)
                    Util.indexOfRank(values, rank)
                }
                ActionSelector.SCORE_CLOSEST_TO_RANGE -> return { nodes ->
                    rangeMin!!
                    rangeMax!!
                    val values = computeValues(scoreDistanceToRange(rangeMin, rangeMax), nodes)
                    Util.indexOfMin(values)
                }
                ActionSelector.SCORE_CLOSEST_TO_VALUE -> return { nodes ->
                    target!!
                    val values = computeValues(scoreDistanceToValue(target), nodes)
                    Util.indexOfMin(values)
                }
            }
        }
    }
}

enum class ActionSelector {
    HIGHEST_SCORE,
    MEDIAN_SCORE,
    MOST_VISITS,
    SCORE_BY_PERCENTILE,
    SCORE_CLOSEST_TO_RANGE,
    SCORE_CLOSEST_TO_VALUE
}