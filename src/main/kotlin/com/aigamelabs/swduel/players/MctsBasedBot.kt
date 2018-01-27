package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.Player
import com.aigamelabs.mcts.actionselection.ActionSelector
import com.aigamelabs.mcts.actionselection.actionevaluation.AverageScore
import com.aigamelabs.mcts.nodeevaluation.NodeEvaluator
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.actions.Action
import io.vavr.collection.Vector
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
        name: String,
        gameData: GameData,
        actionSelector: ActionSelector,
        playerNodeEvaluator: NodeEvaluator,
        opponentNodeEvaluator: NodeEvaluator
) : Player(name, gameData) {

    /** Generator for JSON output  */
    private var generator: JsonGenerator? = null

    /** Flag indicating whether the JSON log currently has a round object and a corresponding frames array open  */
    private var isGameOpenInLog = false

    /** Flag indicating whether the JSON generator is open  */
    private var isJsonGeneratorOpen = false

    private var lastAction : Action? = null

    /** Uct threads manager  */
    internal var manager = UctParallelizationManager(actionSelector, playerNodeEvaluator, opponentNodeEvaluator)

    /**
     * Opens the JSON log. It does not open the round; this should be handled externally.
     * @param jsonName Name of the output file.
     */
    private fun openLog(jsonName: String) {
        val file = File(jsonName)

        try {
            val fos = FileOutputStream(file, false)
            generator = Json.createGenerator(fos)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Open root object
        generator!!.writeStartObject()

        generator!!.write("player_1_controller", gameData.player1Controller)
        generator!!.write("player_2_controller", gameData.player2Controller)

        // Open rounds array
        generator!!.writeStartArray("games")

        isJsonGeneratorOpen = true
    }

    private fun logMctsDecision() {
        if (!isJsonGeneratorOpen)
            return

        if (!isGameOpenInLog)
            logGameStart()

        /*
        //		Double[] childrenValues = lastRoot.getChildrenValues(Manager.AVERAGE_SCORE);
        val childrenValues = manager.rootNode!!.children!!.values
                .map( { AverageScore().getValue(it) } )
                .toDoubleArray()
        generator!!.writeStartObject()
        */

        /*
        generator!!.writeStartObject("values")
        IntStream.range(0, myActions.size()).forEach { i ->
            val actionName = myActions.get(i).name()
            if (java.lang.Double.isInfinite(childrenValues[i]))
                if (childrenValues[i] > 0)
                    generator!!.write(actionName, "+Infinity")
                else
                    generator!!.write(actionName, "-Infinity")
            else if (java.lang.Double.isNaN(childrenValues[i]))
                generator!!.write(actionName, "NaN")
            else
                generator!!.write(actionName, childrenValues[i])
        }
        generator!!.writeEnd()
        */

        /*
        generator!!.write("selection", lastAction!!.name())
        generator!!.write("player_hp", frameData.getMyCharacter(playerNumber).getHp())
        generator!!.write("opponent_hp", frameData.getOpponentCharacter(playerNumber).getHp())
        */

        generator!!.writeEnd()
    }

    /**
     * Closes the JSON log. It assumes that the round has already been closed.
     */
    private fun closeLog() {
        if (!isJsonGeneratorOpen)
            return

        if (isGameOpenInLog) {
            println("Unexpected emergency closure of round object in JSON.")
            logGameEnd()
        }

        // Close rounds array
        generator!!.writeEnd()

        // Close root object
        generator!!.writeEnd()

        // Close the resources
        generator!!.flush()
        generator!!.close()

        isJsonGeneratorOpen = false

    }

    /**
     * Writes round outcome to the log, closes the round and starts the next one.
     */
    private fun logGameEnd() {

        if (!isGameOpenInLog)
            return

        /*
        val cd1 = frameData.getP1()
        val cd2 = frameData.getP2()
        */

        // Close frames array
        generator!!.writeEnd()

        // Write outcome information
        /*
        val myCharacter = frameData.getMyCharacter(playerNumber)
        if (myCharacter == null) {
            generator!!.write("final_player_hp", java.lang.Float.NaN.toDouble())
        } else {
            generator!!.write("final_player_hp", myCharacter!!.getHp())
        }
        val opponentCharacter = frameData.getOpponentCharacter(playerNumber)
        if (opponentCharacter == null) {
            generator!!.write("final_opponent_hp", java.lang.Float.NaN.toDouble())
        } else {
            generator!!.write("final_opponent_hp", opponentCharacter!!.getHp())
        }
        generator!!.write("winner", if (cd1.getHp() > cd2.getHp()) "P1" else if (cd1.getHp() < cd2.getHp()) "P2" else "DRAW")
        */

        // Close round object
        generator!!.writeEnd()

        isGameOpenInLog = false
    }

    private fun logGameStart() {
        if (isGameOpenInLog)
            return

        // Open round object
        generator!!.writeStartObject()

        // Open frames array
        generator!!.writeStartArray("frames")

        isGameOpenInLog = true
    }

    private fun clear() {
        logGameEnd()
    }

    override fun close() {
        manager.shutdown()
        clear()
        if (LOGGING) closeLog()
    }

    /**
	 * This method processes the data from AI. It is executed in each frame.
	 */
    override fun decide(gameState: GameState, options: Vector<Action>): Action {
        lastAction = manager.run(gameState, options)
        return lastAction!!
    }

    companion object {
        /** If true, the values of all children of root, after MCTS, are logged  */
        private const val LOGGING = true
    }
}
