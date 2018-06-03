package com.aigamelabs.mcts

import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.utils.Util

class ActionSelection {

    companion object {
        /**
         * Calculates the value of a list of nodes using a given evaluator (i.e., a function mapping a tree node to a
         * numeric value).
         *
         * @param evaluator the evaluator to use
         * @param nodes the nodes to evaluate
         *
         * @return the values of the nodes passed, in the same order they were passed
         */
        private fun <T: AbstractGameState<T>> computeValues(evaluator: (TreeNode<T>) -> Double, nodes: Array<TreeNode<T>>): DoubleArray {
            val values = DoubleArray(nodes.size)
            for (i in nodes.indices) {
                values[i] = evaluator(nodes[i])
            }
            return values
        }

        /**
         * Returns a function that, given a node, calculates the average score of all the times it was descended into.
         *
         * @return a function returning the average score of all the times a node was descended into
         */
        fun <T: AbstractGameState<T>> averageScore(): (TreeNode<T>) -> Double {
            return {
                it.score / it.games.toDouble()
            }
        }

        /**
         * Returns a function that, given a node, returns the number of times the node was descended into.
         *
         * @return a function returning the number of times a node was descended into
         */
        private fun <T: AbstractGameState<T>> numVisits(): (TreeNode<T>) -> Double {
            return {
                it.games.toDouble()
            }
        }

        /**
         * Given a numerical range, returns a function that calculates the distance of a number to the range
         * \[[rangeMin], [rangeMax]\]
         *
         * @param rangeMin the lower bound of the range
         * @param rangeMax the upper bound of the range
         *
         * @return a function calculating the distance of a number to the range \[[rangeMin], [rangeMax]\]
         */
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

        /**
         * Given a value, returns a function that calculates the distance of a number to the given value.
         *
         * @param value the value to compare to
         *
         * @return a function calculating the distance of a number to [value]
         */
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

        /**
         * Returns a function that selects a node from a pool of given nodes, based on the chosen characteristics.
         *
         * @param actionSelector the criterion to use to choose the node
         * @param rankPercentile the percentile rank of the action to choose (relevant only if
         * [actionSelector] == [ActionSelector.SCORE_BY_PERCENTILE]
         * @param rangeMin the lower bound of the range the action closest to which will be chosen (relevant only if
         * [actionSelector] == [ActionSelector.SCORE_CLOSEST_TO_RANGE]
         * @param rangeMax the lower bound of the range the action closest to which will be chosen (relevant only if
         * [actionSelector] == [ActionSelector.SCORE_CLOSEST_TO_RANGE]
         * @param target the value the action closest to which will be chosen (relevant only if
         * [actionSelector] == [ActionSelector.SCORE_CLOSEST_TO_VALUE]
         *
         * @return a function that selects a node from a pool of given nodes
         */
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

/**
 * An enumeration of the possible types of action selectors that [ActionSelection.get] can return
 */
enum class ActionSelector {
    HIGHEST_SCORE,
    MEDIAN_SCORE,
    MOST_VISITS,
    SCORE_BY_PERCENTILE,
    SCORE_CLOSEST_TO_RANGE,
    SCORE_CLOSEST_TO_VALUE
}