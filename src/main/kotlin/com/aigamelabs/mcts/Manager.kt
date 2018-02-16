package com.aigamelabs.mcts

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.utils.MinimalFormatter
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

abstract class Manager(
        val player: PlayerTurn,
        val actionSelector: ActionSelector,
        val playerNodeEvaluator: NodeEvaluator,
        val opponentNodeEvaluator: NodeEvaluator,
        val outPath: String?,
        id: String?
) {

    var rootNode: TreeNode? = null
    var rootGameState: GameState? = null

    /** Number of games to be played on a node before it is expanded  */
    val uctNodeCreateThreshold = 10

    /** Depth of the search tree  */
    var maxUctTreeDepth: Int = 100

    /** UCT execution time budget (in frames)  */
    var uctBudgetInNanoseconds: Long = 30_000_000_000

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

    abstract fun run(gameState: GameState) : Action
    abstract fun shutdown()
}
