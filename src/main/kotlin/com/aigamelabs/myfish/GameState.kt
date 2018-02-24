package com.aigamelabs.myfish

import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.actions.ChoosePenguin
import com.aigamelabs.myfish.actions.MovePenguin
import com.aigamelabs.myfish.actions.PlacePenguin
import com.aigamelabs.myfish.enums.BoardTile
import com.aigamelabs.myfish.enums.GamePhase
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.Tuple5
import io.vavr.collection.*
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import io.vavr.collection.List
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.json.stream.JsonGenerator


data class GameState(
        val board: HashMap<Triple<Int, Int, Int>, BoardTile>,
        val penguins: HashMap<PlayerTurn,HashMap<Int,Triple<Int,Int,Int>>>,
        val score: HashMap<PlayerTurn,Int>,
        val decisionQueue: Queue<Decision<GameState>>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
): AbstractGameState<GameState>() {
    private val directions = Vector.of(
            Triple(+1,-1,0),
            Triple(+1,0,-1),
            Triple(-1,+1,0),
            Triple(-1,0,+1),
            Triple(0,+1,-1),
            Triple(0,-1,+1)
    )

    val numPlayers: Int
            get() = score.size()

    fun update(
            board_ : HashMap<Triple<Int, Int, Int>, BoardTile>? = null,
            penguins_ : HashMap<PlayerTurn,HashMap<Int,Triple<Int,Int,Int>>>? = null,
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
    override fun dequeDecision() : Pair<GameState, Decision<GameState>> {
        val dequeueOutcome = decisionQueue.dequeue()
        val thisDecision = dequeueOutcome._1
        val updatedDecisionsQueue = dequeueOutcome._2
        val returnGameState = update(decisionQueue_ = updatedDecisionsQueue)
        return Pair(returnGameState, thisDecision)
    }

    /**
     * Enqueues a decision and returns the updated game state (with the decision in the queue).
     */
    private fun enqueueDecision(decision: Decision<GameState>): GameState {
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

    private fun getFishesOnTile(location: Triple<Int,Int,Int>):Int {
        val boardTile = board[location].getOrElseThrow({Exception("Location $location does not exist")})!!
        return when (boardTile) {
            BoardTile.TILE_WITH_1_FISH -> 1
            BoardTile.TILE_WITH_2_FISHES -> 2
            BoardTile.TILE_WITH_3_FISHES -> 3
            BoardTile.EATEN_TILE -> throw Exception("The tile $location has been previously eaten")
        }
    }

    fun placePenguin(player: PlayerTurn, penguinId: Int, location: Triple<Int,Int,Int>): GameState {
        val updatedPlayerPenguins = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .put(penguinId, location)
        val updatedPenguins = penguins.put(player, updatedPlayerPenguins)
        return update(penguins_ = updatedPenguins)
    }

    fun movePenguin(player: PlayerTurn, penguinId: Int, location: Triple<Int,Int,Int>): GameState {
        val oldPenguinLocation = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .get(penguinId)
                .getOrElseThrow { Exception("There is no such penguin: $penguinId") }
        val eatenFishes = getFishesOnTile(oldPenguinLocation)
        val oldPlayerScore = score.getOrElse(player, 0)
        val updatedScore = score.put(player, oldPlayerScore + eatenFishes)
        val updatedPlayerPenguins = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .put(penguinId, location)
        val updatedPenguins = penguins.put(player, updatedPlayerPenguins)
        val updatedBoard = board.put(oldPenguinLocation, BoardTile.EATEN_TILE)
        return update(board_ = updatedBoard, penguins_ = updatedPenguins, score_ = updatedScore)
    }

    private fun getPlayerPenguinsId(player: PlayerTurn): List<Int> {
        return penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .keySet()
                .toList()
    }

    private fun getPlayerPenguins(player: PlayerTurn): List<Triple<Int,Int,Int>> {
        return penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .values()
                .toList()
    }

    private fun canAnyPlayerPenguinMove(player: PlayerTurn): Boolean {
        return getPlayerPenguins(player)
                .any { canPenguinMove(it) }
    }

    private fun canPenguinMove(location: Triple<Int, Int, Int>): Boolean {
        return directions
                .toStream()
                .map { board[location + it].getOrElse(BoardTile.EATEN_TILE) }
                .any { it != BoardTile.EATEN_TILE}
    }

    private fun getAvailableDestinations(location: Triple<Int, Int, Int>): List<Triple<Int,Int,Int>> {
        return Stream.concat(
                directions.map { dir ->
                    val firstMissing =
                            (0 until 8)
                                    .find {
                                        val loc =  location + dir * it
                                        val onTheBoard = board.containsKey(loc)
                                        if (onTheBoard)
                                            false
                                        else {
                                            val tile = board[loc].getOrElse { throw Exception("No such tile") }
                                            tile == BoardTile.EATEN_TILE
                                        }
                                    }
                    (0 until firstMissing!! - 1)
                            .map { location + dir * it }
                }
        ).toList()
    }

    private fun getAvailableTiles(): List<Triple<Int,Int,Int>> {
        return board.keySet()
                .filter { location ->
                    !penguins.values().any { it.containsValue(location) }
                }
                .toList()
    }

    fun addPlacePenguinDecision(curPlayer: PlayerTurn): GameState {
        val player = curPlayer.next(numPlayers)
        val options = getAvailableTiles()
                .toStream()
                .map { PlacePenguin(player, it) }
                .toVector()
            val decision = Decision(player, options)
        return enqueueDecision(decision)
    }

    fun addChoosePenguinDecision(curPlayer: PlayerTurn): GameState {
        val player = calcNextPlayer(curPlayer)
        return if (player == null)
            update(gamePhase_ = GamePhase.GAME_OVER)
        else {
            val options = getPlayerPenguinsId(player)
                    .toStream()
                    .map { ChoosePenguin(player, it) }
                    .toVector()
            val decision = Decision(player, options)
            enqueueDecision(decision)
        }
    }

    fun addMovePenguinDecision(player: PlayerTurn, penguinId: Int): GameState {
        val penguinLocation = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .get(penguinId)
                .getOrElseThrow { Exception("There is no such penguin: $penguinId") }
        val options = getAvailableDestinations(penguinLocation)
                .toStream()
                .map { MovePenguin(player, penguinId, it)}
                .toVector()
        val decision = Decision(player, options)
        return enqueueDecision(decision)
    }

    private fun calcNextPlayer(player: PlayerTurn): PlayerTurn? {
        var tentativeNextPlayer = player.next(numPlayers)
        while (tentativeNextPlayer != player) {
            if (canAnyPlayerPenguinMove(tentativeNextPlayer))
                return tentativeNextPlayer
            tentativeNextPlayer = tentativeNextPlayer.next(numPlayers)
        }
        if (canAnyPlayerPenguinMove(player))
            return player
        return null
    }

    fun calcWinner(logger: Logger? = null): Tuple5<HashSet<PlayerTurn>, Int, Int, Int, Int> {
        val canAnyoneMove = calcNextPlayer(PlayerTurn.PLAYER_1) != null
        if (canAnyoneMove)
            throw Exception("Game is not over yet")
        else {
            val maxScore = PlayerTurn.values().map { score[it].getOrElse({0}) }.max()
            val winners = PlayerTurn.values().filter { score[it].getOrElse(0) == maxScore }
            val p1Score = score[PlayerTurn.PLAYER_1].getOrElse({0})
            logger?.log(Level.INFO, "Player 1 ate $p1Score fishes")
            val p2Score = score[PlayerTurn.PLAYER_2].getOrElse({0})
            logger?.log(Level.INFO, "Player 2 ate $p2Score fishes")
            val p3Score = score[PlayerTurn.PLAYER_3].getOrElse({0})
            logger?.log(Level.INFO, "Player 3 ate $p3Score fishes")
            val p4Score = score[PlayerTurn.PLAYER_4].getOrElse({0})
            logger?.log(Level.INFO, "Player 4 ate $p4Score fishes")
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

        fun generateBoard(generator: RandomWithTracker): HashMap<Triple<Int,Int,Int>,BoardTile> {
            val tokens = Stream.concat(
                    (0 until 30).map { BoardTile.TILE_WITH_1_FISH },
                    (0 until 20).map { BoardTile.TILE_WITH_2_FISHES },
                    (0 until 20).map { BoardTile.TILE_WITH_2_FISHES }
            ).toMutableList()

            val entries = LinkedList<Pair<Triple<Int,Int,Int>,BoardTile>>()
            (0 until 4).forEach { i ->
                (0 until 7).forEach { j ->
                    val x = -i + j
                    val y = -i - j
                    val z = -x - y
                    val iToken = generator.nextInt(tokens.size)
                    val token = tokens.removeAt(iToken)
                    entries.add(Pair(Triple(x,y,z), token))
                }
                (0 until 8).forEach { j ->
                    val x = -i + j - 1
                    val y = -i - j
                    val z = -x - y
                    val iToken = generator.nextInt(tokens.size)
                    val token = tokens.removeAt(iToken)
                    entries.add(Pair(Triple(x,y,z), token))
                }
            }
            return HashMap.ofAll(entries.toMap())
        }
    }

}

operator fun Triple<Int, Int, Int>.plus(other: Triple<Int,Int,Int>): Triple<Int,Int,Int> {
    return Triple(
            first + other.first,
            second + other.second,
            third + other.third
    )
}

operator fun Triple<Int, Int, Int>.times(other: Int): Triple<Int,Int,Int> {
    return Triple(
            first * other,
            second * other,
            third * other
    )
}

/**
 * Determines the player after the current one.
 */
fun PlayerTurn.next(numPlayers: Int) : PlayerTurn {
    return when (numPlayers) {
        2 -> when (this) {
            PlayerTurn.PLAYER_1 -> PlayerTurn.PLAYER_2
            PlayerTurn.PLAYER_2 -> PlayerTurn.PLAYER_1
            else -> throw Exception("$this is not supposed to be playing")
        }
        3 -> when (this) {
            PlayerTurn.PLAYER_1 -> PlayerTurn.PLAYER_2
            PlayerTurn.PLAYER_2 -> PlayerTurn.PLAYER_3
            PlayerTurn.PLAYER_3 -> PlayerTurn.PLAYER_1
            else -> throw Exception("$this is not supposed to be playing")
        }
        4 -> when (this) {
            PlayerTurn.PLAYER_1 -> PlayerTurn.PLAYER_2
            PlayerTurn.PLAYER_2 -> PlayerTurn.PLAYER_3
            PlayerTurn.PLAYER_3 -> PlayerTurn.PLAYER_4
            PlayerTurn.PLAYER_4 -> PlayerTurn.PLAYER_1
        }
        else -> throw Exception("Cannot have less than 2 or more than 4 players")
    }
}