package com.aigamelabs.mcts

import com.aigamelabs.game.Action
import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.utils.RandomWithTracker

import javax.json.Json
import javax.json.stream.JsonGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Nodes used in MCTS trees.
 *
 * @author Marco Tamassia
 */
class TreeNode<T: AbstractGameState<T>>(
        /**
         * The parent of this node
         */
        val parent: TreeNode<T>?,
        /**
         * A flag signaling whether this node represents a decision of a player, a stochastic decision or a leaf node
         */
        val nodeType: NodeType?,
        /**
         * A flag signaling what player made this decision, if any (e.g., not stochastic)
         */
        val player: PlayerTurn?,
        /**
         * The action that needs to be taken at the parent's game state to get to this node's game state (null for the root)
         */
        val selectedAction: Action<T>?,
        /**
         * The game state represented by this node; the state should *include* the decision that its children represent
         */
        val gameState: T?,
        /**
         * The manager of all the UCT workers
         */
        private val manager: Manager<T>
) {

    /** Children nodes  */
    var children: HashMap<List<Int>, TreeNode<T>>? = null

    /** Depth of the node  */
    val depth: Int = if (parent == null) 0 else parent.depth + 1

    /** Number of times this node was searched  */
    var games: Int = 0

    /** A map containing the score of every player at this node */
    val playersScore = HashMap<PlayerTurn, Double>()

    // If this a player node, the parent is an opponent node and is trying to maximize the opponent score
    /** Returns the score of `player` */
    val score: Double
        get() = when (parent!!.nodeType) {
            NodeType.PLAYER_NODE -> playersScore.getOrDefault(parent.player, 0.0)  // player node
            NodeType.STOCHASTIC_NODE -> Double.NaN  // stochastic node
            else -> Double.NaN  // terminal node
        }

    /**
     * Checks whether this node has had any children created.
     *
     * @return true if any child was create, false otherwise
     */
    fun hasChildren(): Boolean {
        return children != null
    }

    /**
     * Computes the UCB1 score of the node.
     *
     * @return UCB1 score
     */
    fun calcUcb(): Double {
        val remappedScore = when (parent!!.nodeType) {
            NodeType.PLAYER_NODE -> manager.nodeEvaluators[parent.player]!!(score)
            NodeType.STOCHASTIC_NODE -> throw Exception("UCB was called on the child of a stochastic node")
            NodeType.TERMINAL_NODE -> throw Exception("UCB was called on the child of a leaf node... what?!")
            null -> throw Exception("Node type was not set on this node")
        }
        return remappedScore / games + UCB_C * Math.sqrt(2 * Math.log(parent.games.toDouble()) / games)
    }

    /**
     * Updates the node scores by adding to each player a certain amount.
     *
     * @param newScore the quantities to be added to the score of each player
     */
    @Synchronized
    fun updateScores(newScore: Map<PlayerTurn,Double>) {
        newScore.forEach {
            val oldScore = playersScore.getOrDefault(it.key, 0.0)
            playersScore[it.key] = oldScore + it.value
        }
        games++
    }


    /**
     * Exports the tree starting from this node to a file in JSON format.
     *
     * @param jsonName Name of the output file.
     */
    fun export(jsonName: String) {
        try {
            val file = File(jsonName)
            val fos = FileOutputStream(file, false)
            val properties = mapOf(Pair(JsonGenerator.PRETTY_PRINTING, true))
            val jgf = Json.createGeneratorFactory(properties)
            val generator = jgf.createGenerator(fos)

            exportNode(this, generator)

            generator.flush()
            generator.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     * Creates children for the current node, if the node has no children yet.
     *
     * @param generator a random generator
     */
    @Synchronized
    fun createChildren(generator: RandomWithTracker) {

        if (!generator.isEmpty()) {
            generator.clear()
            throw Exception("Generator is dirty")
        }

        if (nodeType == NodeType.STOCHASTIC_NODE)
            throw Exception("Creating children of a stochastic node should be done via sampling")

        if (nodeType == NodeType.TERMINAL_NODE)
            return

        if (children != null)
            return

        if (gameState!!.isQueueEmpty())
            return

        val (unqueuedGameState, decision) = gameState.dequeDecision() // Non-stochastic nodes always have a game state

        children = HashMap()

        decision.options.forEachIndexed { index, action ->
            val updatedGameState =  unqueuedGameState.applyAction(action, generator)

            val (childType, childPlayer) = if (updatedGameState.isGameOver())
                Pair(NodeType.TERMINAL_NODE, null)
            else
                Pair(NodeType.PLAYER_NODE, updatedGameState.dequeDecision().second.player)

            // If new game state was generated deterministically
            if (generator.isEmpty()) {
                val child = TreeNode(this, childType, childPlayer, action, updatedGameState, manager)
                children!![listOf(index)] = child
            }
            // Otherwise, add a stochastic node to model non-determinism
            else {
                val child = TreeNode(this, NodeType.STOCHASTIC_NODE, null, action, null, manager)
                children!![listOf(index)] = child
                child.children = HashMap()
                val grandchildId = generator.popAll() // The random integers identify the child
                // Save the outcome as the first child of the stochastic node
                val grandChild = TreeNode(child, childType, childPlayer, null, updatedGameState, manager)
                child.children!![grandchildId] = grandChild
            }
        }
    }

    /**
     * Samples a child from a stochastic node. This is done by taking the parent's state and re-applying the node
     * action. This produces a next state drawing from the distribution dictated by the game mechanics.
     *
     * @param generator a random generator
     *
     * @return a pair containing: 1) all the numbers that were drawn in the new state generation process (which are used
     * as a unique identifier for the child) and 2) a node containing the new state, or null if that state has been
     * already generated in some previous call to this method on this node
     *
     * @throws Exception if the node is not a stochastic node
     */
    @Synchronized
    fun sampleChild(generator: RandomWithTracker): Pair<List<Int>,TreeNode<T>?> {

        if (nodeType != NodeType.STOCHASTIC_NODE)
            throw Exception("Sampling of children can only be done on stochastic nodes")

        // Re-apply parent action to parent game state to sample another game state for the child
        val parent = parent!! // Root is never stochastic, so all stochastic nodes have a parent
        val unqueuedParentGameState = parent.gameState!!.dequeDecision().first // Stochastic nodes have non-stochastic parents, which always have a game state
        val updatedGameState = unqueuedParentGameState.applyAction(selectedAction!!, generator)

        // The random integers identify the child
        val childId = generator.popAll()

        // If a child with that id does not exist, create it
        return if (children!!.containsKey(childId)) {
            Pair(childId, null)
        }
        else {

            val (childType, childPlayer) = if (updatedGameState.isGameOver())
                Pair(NodeType.TERMINAL_NODE, null)
            else
                Pair(NodeType.PLAYER_NODE, updatedGameState.dequeDecision().second.player)

            Pair(childId, TreeNode(this, childType, childPlayer, null, updatedGameState, manager))
        }
    }

    companion object {

        /** The value of the constant C of UCB 1  */
        private const val UCB_C = 3.0

        /**
         * Exports the data of a node in JSON format.
         *
         * @param node the node to export
         * @param generator the JSON generator to use to output the data
         */
        @Synchronized
        private fun <T: AbstractGameState<T>> exportNode(node: TreeNode<T>, generator: JsonGenerator) {
            generator.writeStartObject()

            generator.writeStartObject("attributes")
            generator.write("games", node.games)
            generator.writeStartObject("scores")
            node.playersScore.forEach {
                generator.write(it.key.toString(), it.value)
            }
            generator.writeEnd()
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

    /**
     * Creates a human-readable string representation of the node.
     *
     * @return a string representation of the node
     */
    override fun toString(): String {
        val builder = StringBuilder()
        children!!.values
                .sortedBy { -it.playersScore[player]!! / it.games }
                .forEach {
                    if (it.games > 0) {
                        val score = (100 * it.playersScore[player]!! / it.games).roundToInt()
                        builder.append("Victory chance $score%: ${it.selectedAction!!}\n")
                    }
                    else
                        builder.append("Victory chance NaN: ${it.selectedAction!!}\n")
                }
        return builder.toString()
    }
}
