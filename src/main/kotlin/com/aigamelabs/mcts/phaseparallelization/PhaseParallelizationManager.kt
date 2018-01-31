package com.aigamelabs.mcts.phaseparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.Manager
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator

import java.util.LinkedList
import java.util.concurrent.*


class PhaseParallelizationManager(
        actionSelector: ActionSelector,
        playerNodeEvaluator: NodeEvaluator,
        opponentNodeEvaluator: NodeEvaluator,
        outPath: String?,
        id: String?
) : Manager(actionSelector, playerNodeEvaluator, opponentNodeEvaluator, outPath, id) {

    /** Workers  */
    private var workers: LinkedList<Runnable>

    /** Pool of workers  */
    private var executor: ExecutorService

    /** Queue of nodes ready for expansion  */
    internal var readyForExpansion: BlockingQueue<TreeNode> = ArrayBlockingQueue<TreeNode>(5)
    internal var readyForPlayout: BlockingQueue<TreeNode> = ArrayBlockingQueue<TreeNode>(5)
    internal var readyForBackprop: BlockingQueue<Pair<TreeNode, GameState>> = ArrayBlockingQueue<Pair<TreeNode, GameState>>(20)

    /** Flag indicating whether a computation is running  */
    internal var running: Boolean = false

    init {
        this.running = false
        this.workers = createWorkers()
        this.executor = Executors.newFixedThreadPool(workers.size)
        for (worker in workers) {
            executor.submit(worker)
        }
    }

    private fun createWorkers(): LinkedList<Runnable> {

        val workers = LinkedList<Runnable>()

        val limit = 4//Math.max(4, Runtime.getRuntime().availableProcessors() - 1);

        val proportionSelection = 1
        val proportionExpansion = 1
        val proportionPlayout = 1
        val proportionBackprop = 1

        var totSelection = 0
        var totExpansion = 0
        var totPlayout = 0
        var totBackprop = 0

        while (totSelection + totExpansion + totPlayout + totBackprop < limit) {
            val ratioSelection = totSelection.toDouble() / proportionSelection
            val ratioExpansion = totExpansion.toDouble() / proportionExpansion
            val ratioPlayout = totPlayout.toDouble() / proportionPlayout
            val ratioBackprop = totBackprop.toDouble() / proportionBackprop

            when {
                ratioSelection <= Math.min(ratioExpansion, Math.min(ratioPlayout, ratioBackprop)) -> {
                    workers.add(SelectionWorker(this))
                    totSelection++
                }
                ratioExpansion <= Math.min(ratioSelection, Math.min(ratioPlayout, ratioBackprop)) -> {
                    workers.add(ExpansionWorker(this))
                    totExpansion++
                }
                ratioPlayout <= Math.min(ratioSelection, Math.min(ratioExpansion, ratioBackprop)) -> {
                    workers.add(PlayoutWorker(this))
                    totPlayout++
                }
                else -> {
                    workers.add(BackpropWorker(this))
                    totBackprop++
                }
            }
        }

        println("Workers created:")
        println(totSelection.toString() + " selection workers")
        println(totExpansion.toString() + " expansion workers")
        println(totPlayout.toString() + " playout workers")
        println(totBackprop.toString() + " backprop workers")

        return workers
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

        running = true
        try {
            TimeUnit.NANOSECONDS.sleep(uctBudgetInNanoseconds - 1000000)
        } catch (ignored: InterruptedException) {
        }

        running = false

        // Logging
        if (outPath != null)
            rootNode!!.export(outPath + "tree.json")
        if (verbose)
            System.out.println("UCT run " + rootNode!!.games + " times.")

        // Choose best action based on MCTS scores
        val selected = actionSelector.chooseBestNode(rootNode!!.children!!.values.toTypedArray())

        // Return selected
        return if (selected >= rootNode!!.children!!.size)
            rootNode!!.children!![listOf(0)]!!.selectedAction!! // Default: first action in the list
        else
            rootNode!!.children!![listOf(selected)]!!.selectedAction!!
    }
}
