package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.enums.GameOutcome
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.HashSet
import java.util.*

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
class Game(private val players : HashMap<PlayerTurn,Player>? = null) {
    fun mainLoop(startingGameState : GameState, generator : RandomWithTracker?) {

        // Play the game
        var gameState = startingGameState
        while (!gameState.decisionQueue.isEmpty) {
            gameState = iterate(gameState, generator)
        }

        // Determine winner
        val gameOutcome = calculateWinner(gameState)
        val outcome = gameOutcome.first
        val p1VictoryPoints = gameOutcome.second
        val p2VictoryPoints = gameOutcome.third
        when (outcome) {
            GameOutcome.PLAYER_1_VICTORY -> System.out.println("Player 1 wins with $p1VictoryPoints versus $p2VictoryPoints")
            GameOutcome.PLAYER_2_VICTORY -> System.out.println("Player 2 wins with $p1VictoryPoints versus $p2VictoryPoints")
            GameOutcome.TIE -> System.out.println("Players scored the same amount of points: $p1VictoryPoints")
        }
    }

    /**
     * Advances the game by one step by querying the appropriate player for the next decision in the queue and applying
     * the returned action.
     */
    private fun iterate(gameState: GameState, generator: RandomWithTracker?): GameState {

        // Dequeue decision and enqueue the next one
        var (gameState_, thisDecision) = gameState.dequeAction()

        // Query player for action
        val action = players!![thisDecision.player]!!.decide(gameState_, thisDecision.options)

        // Check for cheating
        if (!thisDecision.options.contains(action)) {
            throw Exception("Player cheated: selected action\n" + action +
                    "\nbut available actions are\n" + thisDecision.options)
        }

        // Process action
        gameState_ = action.process(gameState_, generator)

        // If the cards structure is empty, switch to next age
        if (gameState_.cardStructure!!.isEmpty()) {
            gameState_ = gameState_.switchToNextAge(generator)
        }

        return gameState_
    }


    companion object {
        /**
         * Calculates the winner and the victory points
         */
        fun calculateWinner(gameState: GameState): Triple<GameOutcome, Int, Int> {
            val endGamePhases = HashSet.of(GamePhase.MILITARY_SUPREMACY, GamePhase.SCIENCE_SUPREMACY, GamePhase.CIVILIAN_VICTORY)
            return if (endGamePhases.contains(gameState.gamePhase)) {
                val p1VictoryPoints = gameState.calculateVictoryPoints(PlayerTurn.PLAYER_1)
                val p2VictoryPoints = gameState.calculateVictoryPoints(PlayerTurn.PLAYER_2)
                when {
                    p1VictoryPoints > p2VictoryPoints -> Triple(GameOutcome.PLAYER_1_VICTORY, p1VictoryPoints, p2VictoryPoints)
                    p2VictoryPoints > p1VictoryPoints -> Triple(GameOutcome.PLAYER_2_VICTORY, p1VictoryPoints, p2VictoryPoints)
                    else -> Triple(GameOutcome.TIE, p1VictoryPoints, p2VictoryPoints)
                }
            } else {
                throw Exception("Something went wrong: we left the game loop but the game phase is not final:\n${gameState.gamePhase}")
            }
        }
    }

}