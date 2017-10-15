package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

/**
 * Workflow:
 *
 * The Game class has a game loop and a queue of decisions.
 * At every iteration, checks if there are more decisions to make: if not, the game finishes.
 * If there are, pick one and enter the loop.
 * In the loop, query the AI about the decision (ie pass it the game state and its options).
 * The query is a function call that takes a GameState and returns a GameState (@see Player.decide).
 * Check that the decided action is actually from the set of options (ie no cheating).
 * Call the `process` method of the decided Action, which takes a GameState and returns a GameState.
 * The `process` method also takes care of adding new decisions to the queue, if any.
 * Loop back and repeat.
 */
data class Decision(val player: PlayerTurn, val options: Vector<Action>)