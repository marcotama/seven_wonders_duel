package com.aigamelabs.mcts

import com.aigamelabs.game.*
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import javax.json.Json
import javax.json.stream.JsonGenerator

/**
 * AI implementing MCTS (Monte Carlo tree search)
 *
 * @author Marco Tamassia
 */
abstract class MctsBasedBot<T: AbstractGameState<T>>(
        player: PlayerTurn,
        private val playerId: String,
        private val gameId: String,
        gameData: GameData,
        actionSelector: (Array<TreeNode<T>>) -> Int,
        nodeEvaluators: Map<PlayerTurn, (Double) -> Double>,
        stateEvaluators: Map<PlayerTurn, (T) -> Double>,
        private val outPath: String? = null
) : Player<T>(playerId, gameData) {

    /** JSON generator */
    private var generator: JsonGenerator? = null

    /** Flag indicating whether the JSON generator is open  */
    private var isJsonGeneratorOpen = false

    private var lastAction : Action<T>? = null

    private val exportTree = false

    /** Uct threads manager  */
    private var manager = UctParallelizationManager(
            player,
            actionSelector,
            nodeEvaluators,
            stateEvaluators,
            outPath,
            exportTree,
            gameId,
            name
    )

    /**
     * Opens the JSON log and leaves the generator inside the games array. It does not open the first game object.
     * @param jsonName Name of the output file.
     */
    private fun openLog(jsonName: String?) {
        if (jsonName == null)
            return

        val file = File(jsonName)

        try {
            val fos = FileOutputStream(file, false)
            val properties = mapOf(Pair(JsonGenerator.PRETTY_PRINTING, true))
            val jgf = Json.createGeneratorFactory(properties)
            generator = jgf.createGenerator(fos)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Open root object
        generator?.writeStartObject()

        generator?.writeStartArray("controllers")
        gameData.controllers.forEach { generator?.write(it) }
        generator?.writeEnd()

        // Open games array
        generator?.writeStartArray("games")

        isJsonGeneratorOpen = true
    }

    private fun logDecision() {
        if (!isJsonGeneratorOpen)
            return

        // Open game object
        generator?.writeStartObject()

        val scorer = ActionSelection.averageScore<T>()
        generator?.writeStartObject("children_values")
        manager.rootNode!!.children!!.entries
                .forEach {
                    val actionName = it.value.selectedAction.toString()
                    val value = scorer(it.value)
                    if (java.lang.Double.isInfinite(value))
                        if (value > 0)
                            generator?.write(actionName, "+Infinity")
                        else
                            generator?.write(actionName, "-Infinity")
                    else if (java.lang.Double.isNaN(value))
                        generator?.write(actionName, "NaN")
                    else
                        generator?.write(actionName, value)
                }
        generator?.writeEnd()



        generator!!.write("selection", lastAction?.toString())

        // Close game object
        generator?.writeEnd()
    }

    /**
     * Closes the JSON log assuming the generator is inside the game array. It assumes that the game has already been
     * closed.
     */
    private fun closeLog() {

        if (!isJsonGeneratorOpen)
            return

        // Close games array
        generator?.writeEnd()

        // Close root object
        generator?.writeEnd()

        // Release resources
        generator?.flush()
        generator?.close()

        isJsonGeneratorOpen = false

    }

    override fun init() {
        openLog(Paths.get(outPath, "${gameId}_player_${playerId}_mcts.json").toAbsolutePath().toString())
    }

    override fun finalize(gameState: T) {

        // Open new game object
        generator!!.writeStartArray("games")
    }

    override fun close() {
        manager.shutdown()
        closeLog()
    }

    /**
	 * This method processes the data from AI. It is executed in each frame.
	 */
    override fun decide(gameState: T): Action<T> {
        lastAction = manager.run(gameState)
        println(manager.rootNode!!)
        logDecision()
        return lastAction!!
    }
}
