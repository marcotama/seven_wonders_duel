package com.aigamelabs.myfish

import com.aigamelabs.game.Player
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.utils.MinimalFormatter
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.logging.*
import javax.json.Json
import javax.json.stream.JsonGenerator


/**
 * Workflow:
 *
 * The Game class has a game loop and a queue of decisions.
 * At every iteration, the following steps are executed:
 * - Check if there are more decisions to make
 * - If not, terminate the game
 * - If yes:
 *  - Fetch the top one
 *  - Query the AI about the decision (ie pass it the game state and its options).
 *    The query function takes a GameState and a list of Action instances and returns an Action (@see Player.decide).
 *  - Check that the returned Action is actually from the list that was passed (i.e. no cheating).
 *  - Call the `process` method of the decided Action, which takes a GameState and returns a GameState.
 *    The `process` method also takes care of adding new decisions to the queue, if any.
 *  - Repeat
 */
class Game(gameId: String, private val players : Map<PlayerTurn, Player<GameState>>, logPath: String) {

    private val logger = Logger.getLogger("ThatsMyFish_Messages")

    init {

        val level = Level.INFO
        while (!logger.handlers.isEmpty())
            logger.removeHandler(logger.handlers[0])
        logger.level = level
        logger.useParentHandlers = false

        val fileHandler = FileHandler(Paths.get(logPath, "${gameId}_game.log").toAbsolutePath().toString())
        fileHandler.formatter = MinimalFormatter()
        fileHandler.level = level
        logger.addHandler(fileHandler)

        /*
        val consoleHandler = ConsoleHandler()
        consoleHandler.formatter = MinimalFormatter()
        consoleHandler.level = level
        logger.addHandler(consoleHandler)
        */
    }

    private val file = File(Paths.get(logPath, "${gameId}_game.json").toAbsolutePath().toString())
    private val fos = FileOutputStream(file, false)
    private val properties = mapOf(Pair(JsonGenerator.PRETTY_PRINTING, true))
    private val jgf = Json.createGeneratorFactory(properties)
    private val jsonGen = jgf.createGenerator(fos)

    fun mainLoop(startingGameState : GameState, generator : RandomWithTracker) {

        try {
            jsonGen.writeStartArray()
            startingGameState.toJson(jsonGen)

            players.forEach { it.value.init() }

            // Play the game
            var gameState = startingGameState
            while (!gameState.isGameOver()) {
                gameState = iterate(gameState, generator)
                //logger.log(Level.INFO, gameState.toString())
            }

            players.forEach { it.value.close() }

            jsonGen.writeEnd()

            // Determine winner
            val gameOutcome = gameState.calcWinner(logger)
            val winners = gameOutcome._1
            val p1Score = gameOutcome._2
            val p2Score = gameOutcome._3
            val p3Score = gameOutcome._4
            val p4Score = gameOutcome._5
            logger?.info(
                    "Winners: $winners\n" +
                    "Scores:\n" +
                    "P1: $p1Score\n" +
                    "P2: $p2Score\n" +
                    "P3: $p3Score\n" +
                    "P4: $p4Score"
            )
        }
        catch (e: Exception) {
            logger?.log(Level.SEVERE, e.message, e)

            throw e
        }
    }

    /**
     * Advances the game by one step by querying the appropriate player for the next decision in the queue and applying
     * the returned action.
     */
    private fun iterate(gameState: GameState, generator: RandomWithTracker): GameState {

        // Dequeue decision and enqueue the next one
        var (gameState_, thisDecision) = gameState.dequeDecision()

        val action = if (thisDecision.options.size() > 1) {
            // Query player for action
            logger?.info("Querying ${thisDecision.player}; options:\n" +
                    thisDecision.options
                            .map { "  $it\n" }
                            .fold("") { a, b -> a + b } + "\n"
            )
            players[thisDecision.player]!!.decide(gameState)
        }
        else {
            logger?.info("Skipping query for ${thisDecision.player} (only one option)\n" +
                    thisDecision.options
                            .map { "  $it\n" }
                            .fold("") { a, b -> a + b } + "\n"
            )
            thisDecision.options[0]
        }
        logger?.info("${thisDecision.player} chose: $action\n\n")


        // Check for cheating
        if (!thisDecision.options.contains(action)) {
            throw Exception("Player cheated: selected action\n" + action +
                    "\nbut available actions are\n" + thisDecision.options)
        }

        // Process action
        gameState_= action.process(gameState_, generator, logger)
        logger.handlers.forEach { it.flush() }

        jsonGen.write(action.toString())
        gameState_.toJson(jsonGen)
        jsonGen.flush()

        return gameState_
    }

}