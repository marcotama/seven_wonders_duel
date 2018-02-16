package com.aigamelabs.mcts.nodeevaluation

abstract class NodeEvaluator {
    abstract fun remap(v: Double): Double
}