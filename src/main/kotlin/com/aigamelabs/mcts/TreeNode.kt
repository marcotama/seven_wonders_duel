package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action

import javax.json.Json
import javax.json.stream.JsonGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.stream.IntStream

/**
 * Node used in MCTS
 *
 * @author Marco Tamassia
 */
class TreeNode(val parent: TreeNode?, val nodeType: NodeType, val selectedAction: Action?, val gameState: GameState, val manager: Manager) {

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

    val childrenType: NodeType = when (nodeType) {
        NodeType.PLAYER_NODE -> NodeType.OPPONENT_NODE
        NodeType.OPPONENT_NODE -> NodeType.PLAYER_NODE
        NodeType.STOCHASTIC_NODE -> when (parent!!.nodeType) {
            NodeType.PLAYER_NODE -> NodeType.OPPONENT_NODE
            NodeType.OPPONENT_NODE -> NodeType.PLAYER_NODE
            NodeType.STOCHASTIC_NODE -> throw Exception("Stochastic node child of a stochastic node")
        }
    }

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
    fun createChildren(actions: List<Action>) {

        actions.forEach { println(it) }

        if (children == null) {

            children = HashMap()
            IntStream.range(0, actions.size)
                    .forEach {
                        val action = actions[it]
                        val newGameState =  gameState.applyAction(action)
                        children!![listOf(it)] = TreeNode(this, childrenType, action, newGameState, manager)
                    }
        }
    }

    companion object {

        /** The value of the constant C of UCB 1  */
        private const val UCB_C = 0.1


        private fun exportNode(node: TreeNode, generator: JsonGenerator) {
            generator.writeStartObject()

            generator.writeStartObject("attributes")
            generator.write("games", node.games)
            generator.write("player_score", node.playerScore)
            generator.write("opponent_score", node.opponentScore)
            generator.write("selected_action", if (node.selectedAction == null) "" else node.selectedAction.toString())
            generator.write("node_type", node.nodeType.toString())
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
