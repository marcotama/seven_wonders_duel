package com.aigamelabs.mcts

class NodeScoreMapping {

    companion object {
        fun get(
                nodeScoreMapper: NodeScoreMapper
        ): (Double) -> Double {
            when (nodeScoreMapper) {
                NodeScoreMapper.DISTANCE_FROM_MIDPOINT -> return { v -> 1 - 2 * Math.abs(0.5 - v) }
                NodeScoreMapper.IDENTITY -> return { v -> v }
            }
        }
    }
}

enum class NodeScoreMapper {
    DISTANCE_FROM_MIDPOINT,
    IDENTITY
}