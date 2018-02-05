package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.Player
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.actionselection.actionevaluation.AverageScore
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.enums.PlayerTurn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.json.Json
import javax.json.stream.JsonGenerator

/**
 * AI implementing MCTS (Monte Carlo tree search)
 *
 * @author Marco Tamassia
 */
abstract class MctsBasedBot(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        actionSelector: ActionSelector,
        playerNodeEvaluator: NodeEvaluator,
        opponentNodeEvaluator: NodeEvaluator,
        private val outPath: String? = null
) : Player(playerId, gameData) {

    /** JSON generator */
    private var generator: JsonGenerator? = null

    /** Flag indicating whether the JSON generator is open  */
    private var isJsonGeneratorOpen = false

    private var lastAction : Action? = null

    private val exportTree = false

    /** Uct threads manager  */
    private var manager = UctParallelizationManager(player, actionSelector, playerNodeEvaluator, opponentNodeEvaluator,
            if (exportTree) outPath else null, gameId, name)

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
            generator = Json.createGenerator(fos)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Open root object
        generator?.writeStartObject()

        generator?.write("player_1_controller", gameData.player1Controller)
        generator?.write("player_2_controller", gameData.player2Controller)

        // Open games array
        generator?.writeStartArray("games")

        isJsonGeneratorOpen = true
    }

    private fun logDecision() {
        if (!isJsonGeneratorOpen)
            return

        // Open game object
        generator?.writeStartObject()

        val scorer = AverageScore()
        generator?.writeStartObject("children_values")
        manager.rootNode!!.children!!.entries
                .forEach {
                    val actionName = it.key.toString()
                    val value = scorer.getValue(it.value)
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
        if (exportTree)
            openLog(outPath + "mcts.log")
    }

    override fun finalize(gameState: GameState) {

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
    override fun decide(gameState: GameState): Action {
        lastAction = manager.run(gameState)
        logDecision()
        return lastAction!!
    }
}
