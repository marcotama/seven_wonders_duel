package com.aigamelabs.mcts.uctparallelization

import com.aigamelabs.game.Action
import com.aigamelabs.game.IAbstractGameState
import com.aigamelabs.mcts.Manager
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.utils.RandomWithTracker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class UctParallelizationManager<T: IAbstractGameState<T>>(
        player: PlayerTurn,
        actionSelector: (Array<TreeNode<T>>) -> Int,
        playerNodeEvaluator: (Double) -> Double,
        opponentNodeEvaluator: (Double) -> Double,
        playerStateEvaluator: (T) -> Double,
        opponentStateEvaluator: (T) -> Double,
        outPath: String?,
        private val exportTree: Boolean,
        private val gameId: String?,
        private val playerId: String?
) : Manager<T>(
        player,
        actionSelector,
        playerNodeEvaluator,
        opponentNodeEvaluator,
        playerStateEvaluator,
        opponentStateEvaluator,
        outPath,
        playerId
) {

    private val generator = RandomWithTracker(Random().nextLong())

    /** Workers  */
    private var workers: Array<UctWorker<T>>

    /** Pool of workers  */
    private var executor: ExecutorService

    init {
        val processors = Runtime.getRuntime().availableProcessors()
        workers = (0 until processors)
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
    override fun run(gameState: T): Action<T> {
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
        val selected = actionSelector(childrenNodes)

        // Return selected
        return if (selected >= rootNode!!.children!!.size)
            childrenNodes[0].selectedAction!! // Default: first action in the list
        else
            childrenNodes[selected].selectedAction!!
    }
}
