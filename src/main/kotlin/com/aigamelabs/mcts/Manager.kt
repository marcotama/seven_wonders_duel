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

abstract class Manager<T: AbstractGameState<T>>(
        val player: PlayerTurn,
        val actionSelector: (Array<TreeNode<T>>) -> Int,
        val playerNodeEvaluator: (Double) -> Double,
        val opponentNodeEvaluator: (Double) -> Double,
        val playerStateEvaluator: (T) -> Double,
        val opponentStateEvaluator: (T) -> Double,
        val outPath: String?,
        id: String?
) {

    var rootNode: TreeNode<T>? = null
    var rootGameState: T? = null

    /** Number of games to be played on a node before it is expanded  */
    val uctNodeCreateThreshold = 10

    /** Depth of the search tree  */
    var maxUctTreeDepth: Int = 100

    /** UCT execution time budget (in frames)  */
    var uctBudgetInNanoseconds: Long = 3_000_000_000

    internal val logger = Logger.getLogger("SevenWondersDuel_$id")
    init {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH.mm.ss")
        val gameId = dateFormat.format(Calendar.getInstance().time)
        val level = Level.INFO

        val fileHandler = FileHandler(Paths.get(outPath, "${gameId}_player_$id.log").toAbsolutePath().toString())
        fileHandler.formatter = MinimalFormatter()
        fileHandler.level = level
        logger.addHandler(fileHandler)
        logger.level = level
        logger.useParentHandlers = false
    }


    // --- Settings ---

    /** Whether to output additional information  */
    protected var verbose = true

    abstract fun run(gameState: T) : Action<T>
    abstract fun shutdown()
}
