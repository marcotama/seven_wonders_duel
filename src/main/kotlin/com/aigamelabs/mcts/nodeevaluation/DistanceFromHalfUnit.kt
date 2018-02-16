package com.aigamelabs.mcts.nodeevaluation

class DistanceFromHalfUnit : NodeEvaluator() {
    override fun remap(v: Double): Double {
        // target = 0.5
        // 0 <= v <= 1
        // abs(target - v)
        // 0 <= v <= 0.5, 0.5 indicates v is the farthest from target
        // 2 * abs(target - v)
        // 0 <= v <= 1, 1 indicates v is the farthest from target
        // 1 - 2 * Math.abs(target - v)
        // 0 <= v <= 1, 1 indicates v is the closest from target
        return 1 - 2 * Math.abs(0.5 - v)
    }
}