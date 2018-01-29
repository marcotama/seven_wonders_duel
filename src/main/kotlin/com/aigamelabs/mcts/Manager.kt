package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import io.vavr.collection.Vector

abstract class Manager(
        val actionSelector: ActionSelector,
        val playerNodeEvaluator: NodeEvaluator,
        val opponentNodeEvaluator: NodeEvaluator,
        val outPath: String?
) {

    var rootNode: TreeNode? = null
    var rootGameState: GameState? = null

    /** Number of games to be played on a node before it is expanded  */
    val uctNodeCreateThreshold = 10

    /** Depth of the search tree  */
    var maxUctTreeDepth: Int = 20

    /** UCT execution time budget (in frames)  */
    var uctBudgetInNanoseconds: Long = 1_000_000_000


    // --- Settings ---

    /** Whether to output additional information  */
    protected var verbose = false

    abstract fun run(gameState: GameState, options: Vector<Action>) : Action
    abstract fun shutdown()
}
