package com.aigamelabs.swduel.players.mcts.uctparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.players.mcts.Manager
import com.aigamelabs.swduel.players.mcts.NodeType
import com.aigamelabs.swduel.players.mcts.TreeNode
import com.aigamelabs.swduel.players.mcts.actionselection.ActionSelector
import com.aigamelabs.swduel.players.mcts.nodeevaluation.NodeEvaluator
import io.vavr.collection.Vector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class UctParallelizationManager(
        rootNode: TreeNode,
        rootGameState: GameState,
        playerNumber: Boolean,
        actionSelector: ActionSelector,
        playerNodeEvaluator: NodeEvaluator,
        opponentNodeEvaluator: NodeEvaluator
) : Manager(rootNode, rootGameState, playerNumber, actionSelector, playerNodeEvaluator, opponentNodeEvaluator) {

    /** Workers  */
    private var workers: Array<UctWorker>

    /** Pool of workers  */
    private var executor: ExecutorService


    init {
        val processors = Runtime.getRuntime().availableProcessors()
        this.workers = (0 until Math.max(1, processors - 1))
                .map { UctWorker(this) }
                .toTypedArray()
        this.executor = Executors.newFixedThreadPool(workers.size)
    }

    override fun shutdown() {
        executor.shutdown()
    }

    /**
     * Performs MCTS on this node.
     *
     * @return Action with the highest number of visits
     */
    override fun run(gameState: GameState, options: Vector<Action>): Action {
        setup(options)

        val timeout = System.nanoTime() + uctBudgetInNanoseconds

        // Run MCTS and wait
        for (worker in workers) {
            worker.timeout = timeout
            executor.submit(worker)
        }
        try {
            executor.awaitTermination(timeout - System.nanoTime(), TimeUnit.NANOSECONDS)
        } catch (ignored: InterruptedException) {}

        // Logging
        if (exportTree)
            rootNode.export("./logs/tree.json")
        if (verbose)
            System.out.println("UCT run " + rootNode.games + " times.")

        // Choose best action based on MCTS scores
        val selected = actionSelector.chooseBestNode(rootNode.children!!.values.toTypedArray())

        // Return selected
        return if (selected >= rootNode.children!!.size)
            rootNode.children!![arrayOf(0)]!!.selectedAction!! // Default: first action in the list
        else
            rootNode.children!![arrayOf(selected)]!!.selectedAction!!
    }

    private fun setup(options: Vector<Action>) {
        val rootNode = TreeNode(null, NodeType.PLAYER_NODE, null, rootGameState, this)
        rootNode.createChildren(options.toJavaList())
    }


}
