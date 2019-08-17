package com.aigamelabs.mcts

import kotlin.math.abs

class NodeScoreMapping {

    companion object {
        /**
         * Returns a function that maps a score to a new score. This is useful when implementing DDA agents that aim at
         * a (for example) 0.5 score. In this case, the original score can be mapped to a value that decreases the
         * further the original score is from 0.5.
         *
         * @param nodeScoreMapper the type of mapper desired
         *
         * @return a function mapping a score to a new value (if the input is 0 <= x <= 1, the output will also be in
         * that interval)
         */
        fun get(
                nodeScoreMapper: NodeScoreMapper
        ): (Double) -> Double {
            when (nodeScoreMapper) {
                NodeScoreMapper.DISTANCE_FROM_MIDPOINT -> return { v -> 1 - 2 * abs(0.5 - v) }
                NodeScoreMapper.IDENTITY -> return { v -> v }
            }
        }
    }
}

/**
 * An enumeration fo the types of mappers that can be produced by [NodeScoreMapping.get]
 */
enum class NodeScoreMapper {
    /**
     * Indicates a mapper that returns a value between 0 and 1 that is higher the closer to 0.5 is the input.
     */
    DISTANCE_FROM_MIDPOINT,

    /**
     * Indicates a mapper that returns the input value.
     */
    IDENTITY
}