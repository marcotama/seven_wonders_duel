package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.utils.RandomWithTracker

import javax.json.Json
import javax.json.stream.JsonGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Node used in MCTS
 *
 * @author Marco Tamassia
 */
class TreeNode(
        /**
         * The parent of this node
         */
        val parent: TreeNode?,
        /**
         * A flag signaling whether this node represents a decision of a player or an imaginary game (stochastic) decision
         */
        val nodeType: NodeType,
        /**
         * The action that needs to be taken at the parent's game state to get to this node's game state (null for the root)
         */
        val selectedAction: Action?,
        /**
         * The game state represented by this node; the state should *include* the decision that its children represent
         */
        val gameState: GameState?,
        /**
         * The manager of all the UCT workers
         */
        val manager: Manager) {

    /** Children nodes  */
    var children: HashMap<List<Int>, TreeNode>? = null

    /** Depth of the node  */
    val depth: Int = if (parent == null) 0 else parent.depth + 1

    /** Number of times this node was searched  */
    var games: Int = 0

    /** Evaluation from the point of view of the player  */
    var playerScore: Double = 0.0

    /** Evaluation from the point of view of the opponent  */
    var opponentScore: Double = 0.0

    // If this a player node, the parent is an opponent node and is trying to maximize the opponent score
    val score: Double
        get() = when (parent!!.nodeType) {
            NodeType.PLAYER_NODE -> playerScore  // player node
            NodeType.OPPONENT_NODE -> opponentScore  // opponent node
            NodeType.STOCHASTIC_NODE -> Double.NaN  // stochastic node
        }

    fun hasChildren(): Boolean {
        return children != null
    }

    /**
     * Computes the UCB1 score of the node.
     *
     * @return UCB1 score
     */
    fun calcUcb(): Double {
        return score / games + UCB_C * Math.sqrt(2 * Math.log(parent!!.games.toDouble()) / games)
    }

    @Synchronized
    fun updateScore(playerScore: Double, opponentScore: Double) {
        this.playerScore += playerScore
        this.opponentScore += opponentScore
        games++
    }


    /**
     * Exports the tree starting from this node to a file in JSON format.
     * @param jsonName Name of the output file.
     */
    fun export(jsonName: String) {
        try {
            val file = File(jsonName)
            val generator: JsonGenerator
            val fos = FileOutputStream(file, false)
            generator = Json.createGenerator(fos)

            exportNode(this, generator)

            generator.flush()
            generator.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     * Creates children for the current node, if the node has no children yet.
     */
    @Synchronized
    fun createChildren(generator: RandomWithTracker) {

        if (!generator.isEmpty())
            throw Exception("Generator is dirty")

        if (nodeType == NodeType.STOCHASTIC_NODE)
            throw Exception("Creating children of a stochastic node should be done via sampling")

        if (children == null) {
            val (unqueuedGameState, decision) = gameState!!.dequeAction() // Non-stochastic nodes always have a game state
            children = HashMap()

            decision.options.forEachIndexed { index, action ->
                val updatedGameState =  unqueuedGameState.applyAction(action, generator)
                val childGameStatePlayer = updatedGameState.dequeAction().second.player
                val childType = when (childGameStatePlayer) {
                    manager.player -> NodeType.PLAYER_NODE
                    else -> NodeType.OPPONENT_NODE
                }

                // If new game state was generated deterministically
                if (generator.isEmpty()) {
                    val child = TreeNode(this, childType, action, updatedGameState, manager)
                    children!![listOf(index)] = child
                }
                // Otherwise, add a stochastic node to model non-determinism
                else {
                    val child = TreeNode(this, NodeType.STOCHASTIC_NODE, action, null, manager)
                    children!![listOf(index)] = child
                    child.children = HashMap()
                    val grandchildId = generator.popAll() // The random integers identify the child
                    // Save the outcome as the first child of the stochastic node
                    val grandChild = TreeNode(this, childType, null, updatedGameState, manager)
                    child.children!![grandchildId] = grandChild
                }
            }
        }
    }

    @Synchronized
    fun sampleChild(generator: RandomWithTracker): Pair<List<Int>,TreeNode?> {

        if (nodeType != NodeType.STOCHASTIC_NODE)
            throw Exception("Sampling of children can only be done on stochastic nodes")

        // Re-apply parent action to parent game state to sample another game state for the child
        val parent = parent!! // Root is never stochastic, so all stochastic nodes have a parent
        val unqueuedParentGameState = parent.gameState!!.dequeAction().first // Stochastic nodes have non-stochastic parents, which always have a game state
        val updatedGameState = unqueuedParentGameState.applyAction(selectedAction!!, generator)

        // The random integers identify the child
        val childId = generator.popAll()

        // If a child with that id does not exist, create it
        return if (children!!.containsKey(childId)) {
            Pair(childId, null)
        }
        else {
            val childGameStatePlayer = updatedGameState.dequeAction().second.player
            val childType = when (childGameStatePlayer) {
                manager.player -> NodeType.PLAYER_NODE
                else -> NodeType.OPPONENT_NODE
            }
            Pair(childId, TreeNode(this, childType, null, updatedGameState, manager))
        }
    }

    companion object {

        /** The value of the constant C of UCB 1  */
        private const val UCB_C = 3.0


        private fun exportNode(node: TreeNode, generator: JsonGenerator) {
            generator.writeStartObject()

            generator.writeStartObject("attributes")
            generator.write("games", node.games)
            generator.write("player_score", node.playerScore)
            generator.write("opponent_score", node.opponentScore)
            generator.write("selected_action", if (node.selectedAction == null) "" else node.selectedAction.toString())
            generator.write("node_type", node.nodeType.toString())
            if (node.gameState != null)
                node.gameState.toJson(generator, "game_state")
            generator.writeEnd()

            generator.writeStartArray("children")
            if (node.hasChildren()) {
                for (child in node.children!!.values) {
                    exportNode(child, generator)
                }
            }
            generator.writeEnd()

            generator.writeEnd()
        }
    }
}
