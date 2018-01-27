package com.aigamelabs.mcts.actionselection.actionevaluation

import com.aigamelabs.mcts.TreeNode

interface ActionEvaluator {
    fun getValue(n: TreeNode): Double
}