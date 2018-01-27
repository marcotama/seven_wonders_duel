package com.aigamelabs.mcts.actionselection


import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.actionselection.actionevaluation.ActionEvaluator

abstract class ActionSelector {
    abstract fun chooseBestNode(nodes: Array<TreeNode>): Int

    protected fun computeValues(evaluator: ActionEvaluator, nodes: Array<TreeNode>): DoubleArray {
        val values = DoubleArray(nodes.size)
        for (i in nodes.indices) {
            values[i] = evaluator.getValue(nodes[i])
        }
        return values
    }
}
