package com.aigamelabs.mcts.uctparallelization

import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.mcts.NodeType
import com.aigamelabs.mcts.TreeNode
import com.aigamelabs.mcts.Util
import com.aigamelabs.swduel.enums.GamePhase
import java.util.*

import java.util.stream.IntStream

/**
 * Executes UCT on the given node.
 */
class UctWorker(internal var manager: UctParallelizationManager) : Runnable {

    var timeout: Long = 0

    private val generator = RandomWithTracker(Random().nextLong())

    override fun run() {
        while (System.nanoTime() <= timeout) {
            uct()
        }
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState used for evaluation
     * @return The score of a node
     */
    private fun getPlayerScore(gameState: GameState): Double {
        return manager.playerNodeEvaluator.calcScore(gameState)
    }

    /**
     * Returns the score of a node.
     *
     * @param gameState FrameData used for evaluation
     * @return The score of a node
     */
    private fun getOpponentScore(gameState: GameState): Double {
        return manager.opponentNodeEvaluator.calcScore(gameState)
    }


    /**
     * Performs an iteration of UCT.
     */
    private fun uct() {

        var currentNode = manager.rootNode!!
        var gameState = manager.rootGameState!!

        // Descend from the root node to a leaf, and expand the leaf if appropriate
        while ((currentNode.hasChildren() || currentNode.games >= manager.uctNodeCreateThreshold)
                && currentNode.depth < manager.maxUctTreeDepth) {

            if (currentNode.nodeType == NodeType.STOCHASTIC_NODE) {
                val parent = currentNode.parent!!
                // Re-apply parent action to parent game state to sample another game state for the child
                val newGameState = parent.gameState.applyAction(parent.selectedAction!!, generator)
                // The random integers generated during the action application are the unique identifier for the child
                val childId = generator.popAll()
                // If a child with that id does not exist, create it
                if (!currentNode.children!!.containsKey(childId))
                    currentNode.children!![childId] = TreeNode(currentNode, currentNode.childrenType, null, newGameState, manager)
                // Descend in the child with the calculated id
                currentNode = currentNode.children!![childId]!!
            }
            else {
                // Deque next decision to be made
                val (gameState_, thisDecision) = gameState.dequeAction()
                gameState = gameState_

                // If node has no children, create them using decision options
                currentNode.createChildren(thisDecision.options.toJavaList())

                // Retrieve non-visited nodes
                val nonVisitedChildren = currentNode.children!!.values
                        .filterTo(LinkedList()) { it.games == 0 }

                // If there are any, choose one and descend in it
                if (nonVisitedChildren.size > 0) {
                    currentNode = nonVisitedChildren[rnd.nextInt(nonVisitedChildren.size)]
                }
                // Otherwise choose the child with highest UCB score
                else {
                    val childrenValues = DoubleArray(currentNode.children!!.size)
                    for (i in 0 until currentNode.children!!.size) {
                        childrenValues[i] = currentNode.children!![arrayOf(i)]!!.calcUcb()
                    }
                    currentNode = currentNode.children!![arrayOf(Util.indexOfMax(childrenValues))]!!
                }
            }
        }

        // Run a playout
        val endGameState = playout(currentNode)
        // Calculate score for end game
        val playerScore = getPlayerScore(endGameState)
        val opponentScore = getOpponentScore(endGameState)
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
    private fun playout(node: TreeNode): GameState {

        var gameState = node.gameState

        // Apply random actions to the playout
        val activeGamePhases = setOf(GamePhase.FIRST_AGE, GamePhase.SECOND_AGE, GamePhase.THIRD_AGE, GamePhase.WONDERS_SELECTION)
        while (activeGamePhases.contains(gameState.gamePhase)) {
                val dequeueOutcome = gameState.dequeAction()
                gameState = dequeueOutcome.first
                val decision = dequeueOutcome.second
                val options = decision.options
                val choice = rnd.nextInt(options.size())
                gameState = gameState.applyAction(options[choice])
            }
        return gameState
    }

    companion object {
        private val rnd = Random()
    }
}
