package com.aigamelabs.mcts.uctparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.Manager
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.utils.RandomWithTracker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class UctParallelizationManager(
        player: PlayerTurn,
        actionSelector: ActionSelector,
        playerNodeEvaluator: NodeEvaluator,
        opponentNodeEvaluator: NodeEvaluator,
        outPath: String?,
        private val exportTree: Boolean,
        private val gameId: String?,
        private val playerId: String?
) : Manager(player, actionSelector, playerNodeEvaluator, opponentNodeEvaluator, outPath, playerId) {

    private val generator = RandomWithTracker(Random().nextLong())

    /** Workers  */
    private var workers: Array<UctWorker>

    /** Pool of workers  */
    private var executor: ExecutorService

    init {
        val processors = Runtime.getRuntime().availableProcessors()
        workers = (0 until Math.max(1, processors - 1))
                .map { UctWorker(this, "#$it") }
                .toTypedArray()
        logger.info("Setup ${workers.size} workers")
        executor = Executors.newFixedThreadPool(workers.size)
    }

    override fun shutdown() {
        executor.shutdown()
    }

    /**
     * Performs MCTS on this node.
     *
     * @return Action with the highest number of visits
     */
    override fun run(gameState: GameState): Action {
        rootGameState = gameState
        rootNode = TreeNode(null, NodeType.PLAYER_NODE, null, rootGameState!!, this)
        rootNode!!.createChildren(generator)

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
        if (exportTree) {
            val time = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Calendar.getInstance().time)
            rootNode!!.export(outPath + "${gameId}_player_${playerId}_mcts-tree_$time.json")
        }
        if (verbose)
            logger.info("UCT run " + rootNode!!.games + " times.")

        // Choose best action based on MCTS scores
        val childrenNodes = rootNode!!.children!!.values.toTypedArray()
        val selected = actionSelector.chooseBestNode(childrenNodes)

        // Return selected
        return if (selected >= rootNode!!.children!!.size)
            childrenNodes[0].selectedAction!! // Default: first action in the list
        else
            childrenNodes[selected].selectedAction!!
    }
}
