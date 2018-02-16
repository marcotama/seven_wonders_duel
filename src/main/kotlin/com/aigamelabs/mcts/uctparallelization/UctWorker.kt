package com.aigamelabs.mcts.uctparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.Util
import com.aigamelabs.swduel.enums.GamePhase
import java.util.*
import java.util.logging.Level

/**
 * Executes UCT on the given node.
 */
class UctWorker(private var manager: UctParallelizationManager, private val workerId: String) : Runnable {

    var timeout: Long = 0

    private val generator = RandomWithTracker(Random().nextLong())

    override fun run() {
        while (System.nanoTime() <= timeout) {
            try {
                uct()
            }
            catch (e: Exception) {
                manager.logger.log(Level.WARNING, "Exception in worker $workerId", e)
                throw e
            }
        }
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState used for evaluation
     * @return The score of a node
     */
    private fun getPlayerScore(gameState: GameState): Double {
        return manager.playerStateEvaluator.calcScore(gameState)
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState FrameData used for evaluation
     * @return The score of a node
     */
    private fun getOpponentScore(gameState: GameState): Double {
        return manager.opponentStateEvaluator.calcScore(gameState)
    }


    /**
     * Performs an iteration of UCT.
     */
    private fun uct() {

        var currentNode = manager.rootNode!!

        // Descend from the root node to a leaf, and expand the leaf if appropriate
        while (currentNode.nodeType == NodeType.STOCHASTIC_NODE || (
                (currentNode.hasChildren() || currentNode.games >= manager.uctNodeCreateThreshold)
                && currentNode.depth < manager.maxUctTreeDepth)) {

            if (currentNode.nodeType == NodeType.STOCHASTIC_NODE) {
                val (childId, childNode) = currentNode.sampleChild(generator)
                if (childNode != null)
                    currentNode.children!![childId] = childNode
                currentNode = currentNode.children!![childId]!!

                manager.logger.log(Level.FINE, "Worker $workerId: stochastically descending into \"$childId\"")
                manager.logger.log(Level.FINEST, "Worker $workerId: new state is ${currentNode.gameState}")
            }
            else {
                // If node has no children, create them using decision options
                currentNode.createChildren(generator)

                // Terminal node
                if (currentNode.children == null)
                    break

                // Retrieve non-visited nodes
                val nonVisitedChildren = currentNode.children!!.values
                        .filterTo(LinkedList()) { it.games == 0 }

                // If there are any, choose one and descend in it
                if (nonVisitedChildren.size > 0) {
                    val childIdx = rnd.nextInt(nonVisitedChildren.size)
                    currentNode = nonVisitedChildren[childIdx]
                    manager.logger.log(Level.FINE, "Worker $workerId: descending into \"${nonVisitedChildren[childIdx].selectedAction}\"")
                    manager.logger.log(Level.FINEST, "Worker $workerId: new state is ${currentNode.gameState}")
                }
                // Otherwise choose the child with highest UCB score
                else {
                    val childrenValues = DoubleArray(currentNode.children!!.size)
                    for (i in 0 until currentNode.children!!.size) {
                        childrenValues[i] = currentNode.children!![listOf(i)]!!.calcUcb()
                    }
                    currentNode = currentNode.children!![listOf(Util.indexOfMax(childrenValues))]!!
                }
            }
        }

        // Run a playout
        val endGameState = playout(currentNode.gameState!!) // The while loop never quits on a stochastic node, and non-stochastic nodes have a game state
        // Calculate score for end game
        val playerScore = getPlayerScore(endGameState)
        val opponentScore = getOpponentScore(endGameState)
        manager.logger.log(Level.FINE, "Worker $workerId: playout ended with ${endGameState.gamePhase}, scores are P1: $playerScore and P2: $opponentScore")
        // Backpropagate
        while (true) {
            currentNode.updateScore(playerScore, opponentScore)
            if (currentNode.parent == null)
                break
            currentNode = currentNode.parent!!
        }
    }

    /**
     * Performs a playout (a simulated execution of tree moves followed by random moves).
     *
     * @return Result of the playout
     */
    private fun playout(gameState_: GameState): GameState {

        var gameState = gameState_

        // Apply random actions to the playout
        val activeGamePhases = setOf(GamePhase.FIRST_AGE, GamePhase.SECOND_AGE, GamePhase.THIRD_AGE, GamePhase.WONDERS_SELECTION)
        while (activeGamePhases.contains(gameState.gamePhase)) {
            val dequeueOutcome = gameState.dequeAction()
            gameState = dequeueOutcome.first
            val decision = dequeueOutcome.second
            val options = decision.options
            val choice = rnd.nextInt(options.size())
            gameState = gameState.applyAction(options[choice], generator)
            generator.popAll()
            manager.logger.log(Level.FINE, "Worker $workerId: randomly choosing \"$choice\" in playout")
            manager.logger.log(Level.FINEST, "Worker $workerId: new state is $gameState")
        }
        return gameState
    }

    companion object {
        private val rnd = Random()
    }
}
