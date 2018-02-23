package com.aigamelabs.myfish

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.*
import com.aigamelabs.myfish.enums.BoardTile
import com.aigamelabs.myfish.enums.GamePhase
import io.vavr.Tuple5
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import org.json.JSONObject
import java.util.logging.Level
import java.util.logging.Logger
import javax.json.stream.JsonGenerator



data class GameState(
        val board: HashMap<Triple<Int, Int, Int>, BoardTile>,
        val penguins: HashMap<Pair<PlayerTurn, Int>, Triple<Int, Int, Int>>,
        val score: HashMap<PlayerTurn,Int>,
        val decisionQueue: Queue<Decision<GameState>>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
): AbstractGameState<GameState>() {

    fun update(
            board_ : HashMap<Triple<Int, Int, Int>, BoardTile>? = null,
            penguins_ : HashMap<Pair<PlayerTurn, Int>, Triple<Int, Int, Int>>? = null,
            score_ : HashMap<PlayerTurn,Int>? = null,
            decisionQueue_ : Queue<Decision<GameState>>? = null,
            gamePhase_: GamePhase? = null,
            nextPlayer_: PlayerTurn? = null
    ) : GameState {
        return GameState(
                board_ ?: board,
                penguins_ ?: penguins,
                score_ ?: score,
                decisionQueue_ ?: decisionQueue,
                gamePhase_ ?: gamePhase,
                nextPlayer_ ?: nextPlayer
        )
    }

    /**
     * Deques a decision and returns the updated game state (without the decision in the queue) and the extracted
     * decision.
     */
    override fun dequeAction() : Pair<GameState, Decision<GameState>> {
        val dequeueOutcome = decisionQueue.dequeue()
        val thisDecision = dequeueOutcome._1
        val updatedDecisionsQueue = dequeueOutcome._2
        val returnGameState = update(decisionQueue_ = updatedDecisionsQueue)
        return Pair(returnGameState, thisDecision)
    }

    /**
     * Enqueues a decision and returns the updated game state (with the decision in the queue).
     */
    fun enqueue(decision: Decision<GameState>): GameState {
        val updatedDecisionQueue = decisionQueue.insert(0, decision)
        return update(decisionQueue_ = updatedDecisionQueue)
    }

    /**
     * Advances the game by one step by applying the given action to the next decision in the queue. Does not detect
     * cheating.
     */
    override fun applyAction(action: Action<GameState>, generator: RandomWithTracker): GameState {
        return action.process(this, generator)
    }

    override fun isQueueEmpty(): Boolean {
        return decisionQueue.isEmpty
    }

    override fun isGameOver(): Boolean {
        return gamePhase == GamePhase.GAME_OVER
    }

    fun getFishesOn(location: Triple<Int,Int,Int>):Int {
        val boardTile = board[location].getOrElseThrow({Exception("Location $location does not exist")})!!
        return when (boardTile) {
            BoardTile.TILE_WITH_1_FISH -> 1
            BoardTile.TILE_WITH_2_FISHES -> 2
            BoardTile.TILE_WITH_3_FISHES -> 3
            BoardTile.EATEN_TILE -> throw Exception("The tile $location has been previously eaten")
        }
    }

    fun movePenguin(playerTurn: PlayerTurn, penguinId: Int, location: Triple<Int,Int,Int>): GameState {
        val oldPenguinLocation = penguins[Pair(playerTurn,penguinId)].getOrElseThrow { Exception("There is no such penguin") }
        val eatenFishes = getFishesOn(oldPenguinLocation)
        val oldPlayerScore = score.getOrElse(playerTurn, 0)
        val updatedScore = score.put(playerTurn, oldPlayerScore + eatenFishes)
        val updatedPenguins = penguins.put(Pair(playerTurn, penguinId), location)
        val updatedBoard = board.put(oldPenguinLocation, BoardTile.EATEN_TILE)
        return update(board_ = updatedBoard, penguins_ = updatedPenguins, score_ = updatedScore)
    }

    fun canPenguinMove(playerTurn: PlayerTurn, penguinId: Int): Boolean {
        val penguinLocation = penguins[Pair(playerTurn,penguinId)].getOrElseThrow { Exception("There is no such penguin") }
        return canPenguinMove(penguinLocation)
    }

    fun canPenguinMove(location: Triple<Int, Int, Int>): Boolean {
        // TODO
        return true
    }

    fun calculateWinner(logger: Logger? = null): Tuple5<HashSet<PlayerTurn>, Int, Int, Int, Int> {
        val canAnyoneMove = penguins.values()
                .map { location -> canPenguinMove(location) }
                .fold(false, { acc, v -> acc || v })
        if (canAnyoneMove)
            throw Exception("Game is not over yet")
        else {
            val maxScore = PlayerTurn.values().map { score[it].getOrElse({0}) }.max()
            val winners = PlayerTurn.values().filter { score[it].getOrElse(0) == maxScore }
            val p1Score = score[PlayerTurn.PLAYER_1].getOrElse({0})
            val p2Score = score[PlayerTurn.PLAYER_2].getOrElse({0})
            val p3Score = score[PlayerTurn.PLAYER_3].getOrElse({0})
            val p4Score = score[PlayerTurn.PLAYER_4].getOrElse({0})
            return Tuple5(HashSet.ofAll(winners), p1Score, p2Score, p3Score, p4Score)
        }
    }


    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toJson(generator: JsonGenerator, name: String?) {
        // TODO
    }

    override fun toString(): String {
        // TODO
        return ""
    }

    companion object {

        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

        /*
        fun loadFromJson(obj: JSONObject): GameState {
            // TODO
        }
        */
    }

}

/**
 * Determines the opponent of the given player.
 */
// This function is not in the PlayerTurn class because it assumes a 2-players game
fun PlayerTurn.opponent() : PlayerTurn {
    return if (this == PlayerTurn.PLAYER_1) PlayerTurn.PLAYER_2 else PlayerTurn.PLAYER_1
}