package com.aigamelabs.mcts

import com.aigamelabs.game.Action
import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.utils.MinimalFormatter
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * An MCTS executor.
 *
 * @param player the player that is running the tree
 * @param actionSelector a function that chooses an action from a set
 * @param nodeEvaluators functions that map a score to a value, one per player
 * @param stateEvaluators function that map a node to a score, one per player
 * @param outPath the path of a directory where logs will be stored
 * @param playerId an identifier for the player (used in the name of log files)
 */
abstract class Manager<T: AbstractGameState<T>>(
        val player: PlayerTurn,
        val actionSelector: (Array<TreeNode<T>>) -> Int,
        val nodeEvaluators: Map<PlayerTurn, (Double) -> Double>,
        val stateEvaluators: Map<PlayerTurn, (T) -> Double>,
        val outPath: String?,
        playerId: String?
) {
    /** The root of the tree */
    var rootNode: TreeNode<T>? = null

    /** The game state of the root of the tree */
    var rootGameState: T? = null

    /** Number of games to be played on a node before it is expanded  */
    val uctNodeCreateThreshold = 10

    /** Depth of the search tree  */
    var maxUctTreeDepth: Int = 100

    /** UCT execution time budget (in frames)  */
    var uctBudgetInNanoseconds: Long = 1_000_000_000

    internal val logger = Logger.getLogger("SevenWondersDuel_$playerId")
    init {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH.mm.ss")
        val gameId = dateFormat.format(Calendar.getInstance().time)
        val level = Level.INFO

        val fileHandler = FileHandler(Paths.get(outPath, "${gameId}_player_$playerId.log").toAbsolutePath().toString())
        fileHandler.formatter = MinimalFormatter()
        fileHandler.level = level
        logger.addHandler(fileHandler)
        logger.level = level
        logger.useParentHandlers = false
    }


    // --- Settings ---

    /** Whether to output additional information  */
    protected var verbose = true

    /**
     * Performs MCTS on this node.
     *
     * @return Action with the highest number of visits
     */
    abstract fun run(gameState: T) : Action<T>

    /**
     * Shuts down this manager.
     */
    abstract fun shutdown()
}
