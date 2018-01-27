package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import io.vavr.collection.Vector

abstract class Manager(
        val actionSelector: ActionSelector,
        val playerNodeEvaluator: NodeEvaluator,
        val opponentNodeEvaluator: NodeEvaluator
) {

    var rootNode: TreeNode? = null
    var rootGameState: GameState? = null

    /** Number of games to be played on a node before it is expanded  */
    val uctNodeCreateThreshold = 10

    /** Depth for playouts (playing till the end of the fight would take too long)  */
    val playoutDepth = 2


    // --- Settings computed after construction ---

    /** Depth of the search tree  */
    var maxUctTreeDepth: Int = 0

    /** How many frames to run the simulation for (regardless of action queues)  */
    var simulationLimit: Int = 0

    /** UCT execution time budget (in frames)  */
    var uctBudgetInNanoseconds: Long = 0


    // --- Settings ---

    /** Whether to output additional information  */
    protected var verbose = false

    /** Whether to export the final search tree   */
    protected var exportTree = false

    abstract fun run(gameState: GameState, options: Vector<Action>) : Action
    abstract fun shutdown()
}
