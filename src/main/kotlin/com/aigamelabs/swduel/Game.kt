package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
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
class Game(gameId: String, private val players : Map<PlayerTurn, Player>, logPath: String) {

    private val logger = Logger.getLogger("SevenWondersDuel_Messages")

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

        val consoleHandler = ConsoleHandler()
        consoleHandler.formatter = MinimalFormatter()
        consoleHandler.level = level
        logger.addHandler(consoleHandler)

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
            while (!gameState.decisionQueue.isEmpty) {
                gameState = iterate(gameState, generator)
                //logger.log(Level.INFO, gameState.toString())
            }

            players.forEach { it.value.close() }

            jsonGen.writeEnd()

            // Determine winner
            val gameOutcome = gameState.calculateWinner(logger)
            val outcome = gameOutcome.first
            val p1VictoryPoints = gameOutcome.second
            val p2VictoryPoints = gameOutcome.third
            when (gameState.gamePhase) {
                GamePhase.CIVILIAN_VICTORY -> when (outcome) {
                    GameOutcome.PLAYER_1_VICTORY -> logger?.info("Player 1 wins with $p1VictoryPoints versus $p2VictoryPoints")
                    GameOutcome.PLAYER_2_VICTORY -> logger?.info("Player 2 wins with $p1VictoryPoints versus $p2VictoryPoints")
                    GameOutcome.TIE -> logger?.info("Players scored the same amount of points: $p1VictoryPoints")
                }
                GamePhase.SCIENCE_SUPREMACY -> when (outcome) {
                    GameOutcome.PLAYER_1_VICTORY -> logger?.info("Player 1 wins with Science Supremacy")
                    GameOutcome.PLAYER_2_VICTORY -> logger?.info("Player 2 wins with Science Supremacy")
                    GameOutcome.TIE -> throw Exception("You cannot have Science Supremacy and a tie")
                }
                GamePhase.MILITARY_SUPREMACY -> when (outcome) {
                    GameOutcome.PLAYER_1_VICTORY -> logger?.info("Player 1 wins with Military Supremacy")
                    GameOutcome.PLAYER_2_VICTORY -> logger?.info("Player 2 wins with Military Supremacy")
                    GameOutcome.TIE -> throw Exception("You cannot have Military Supremacy and a tie")
                }
                else -> throw Exception("The game is not over yet; current phase is ${gameState.gamePhase}")
            }
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
        var (gameState_, thisDecision) = gameState.dequeAction()

        // Query player for action
        logger?.info("Querying ${thisDecision.player}; options:\n" +
                thisDecision.options
                        .map { "  $it\n" }
                        .fold("", { a, b -> a + b } ) +
                "Decision prompted by ${thisDecision.addedBy}\n"
        )
        val action = players[thisDecision.player]!!.decide(gameState)

        logger?.info("${thisDecision.player} chose: $action\n\n")


        // Check for cheating
        if (!thisDecision.options.contains(action)) {
            throw Exception("Player cheated: selected action\n" + action +
                    "\nbut available actions are\n" + thisDecision.options)
        }

        // Process action
        gameState_= action.process(gameState_, generator, logger)

        jsonGen.write(action.toString())
        gameState_.toJson(jsonGen)

        return gameState_
    }

}