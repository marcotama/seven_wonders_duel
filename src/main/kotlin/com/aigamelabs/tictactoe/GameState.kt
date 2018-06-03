package com.aigamelabs.tictactoe

import com.aigamelabs.game.AbstractGameState
import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.tictactoe.actions.PlaceTile
import com.aigamelabs.tictactoe.enums.BoardTile
import com.aigamelabs.tictactoe.enums.GamePhase
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import io.vavr.collection.List
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import javax.json.stream.JsonGenerator


data class GameState(
        val board: HashMap<Pair<Int, Int>, BoardTile?>,
        val decisionQueue: Queue<Decision<GameState>>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
): AbstractGameState<GameState>() {
    private val playersToTiles = hashMapOf(
            PlayerTurn.PLAYER_1 to BoardTile.O,
            PlayerTurn.PLAYER_2 to BoardTile.X
    )

    fun update(
            board_ : HashMap<Pair<Int, Int>, BoardTile?>? = null,
            decisionQueue_ : Queue<Decision<GameState>>? = null,
            gamePhase_: GamePhase? = null,
            nextPlayer_: PlayerTurn? = null
    ) : GameState {
        return GameState(
                board_ ?: board,
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
    fun enqueueDecision(decision: Decision<GameState>): GameState {
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

    fun place(player: PlayerTurn, location: Pair<Int,Int>): GameState {
        val newTile = playersToTiles[player]
        val updatedBoard = board.put(location, newTile)
        return update(board_ = updatedBoard)
    }

    private fun getEmptyLocations(): List<Pair<Int,Int>> {
        return board.filter { _, v -> v == null }.keySet().toList()
    }

    private fun isBoardFull(): Boolean {
        return board.values().all { it != null }
    }

    fun addDecision(curPlayer: PlayerTurn): GameState {
        return if (isBoardFull() || calcWinner().size() == 1)
            update(gamePhase_ = GamePhase.GAME_OVER)
        else {
            val player = curPlayer.next()
            val options = getEmptyLocations().toStream().map { PlaceTile(player, it) }.toVector()
            val decision = Decision(player, options)
            enqueueDecision(decision)
        }
    }

    fun calcWinner(): HashSet<PlayerTurn> {
        val p1Tile = playersToTiles[PlayerTurn.PLAYER_1]
        val tileToPlayer = { tile: BoardTile -> if (tile == p1Tile) PlayerTurn.PLAYER_1 else PlayerTurn.PLAYER_2}
        for (tile in BoardTile.values()) {
            var diag1Count = 0
            var diag2Count = 0
            for (i in 0..2) {
                if (board[Pair(i, i)].orNull == tile)
                    diag1Count++
                if (board[Pair(i, 2-i)].orNull == tile)
                    diag2Count++
                var rowCount = 0
                var colCount = 0
                for (j in 0..2) {
                    if (board[Pair(i, j)].orNull == tile)
                        rowCount++
                    if (board[Pair(j, i)].orNull == tile)
                        colCount++
                }
                if (rowCount == 3 || colCount == 3)
                    return HashSet.of(tileToPlayer(tile))
            }
            if (diag1Count == 3 || diag2Count == 3)
                return HashSet.of(tileToPlayer(tile))
        }
        return HashSet.of(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2)
    }


    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.writeStartArray("board")
        board.forEach {
            val coords = it._1
            val tile = it._2
            generator.writeStartObject()
            generator.writeStartArray("coords")
            generator.write(coords.first)
            generator.write(coords.second)
            generator.writeEnd()
            generator.write("tile", tile.toString())
            generator.writeEnd()

        }
        generator.writeEnd()

        generator.writeStartArray("decision_queue")
        decisionQueue.forEach { it.toJson(generator, null) }
        generator.writeEnd()

        generator.write("game_phase", gamePhase.toString())
        generator.write("next_player", nextPlayer.toString())

        generator.writeEnd()
    }

    override fun toString(): String {

        val ret = StringBuilder()
        ret.append(
                "Board:\n",
                board.fold("", { acc, it ->
                    val coords = it._1
                    val tile = it._2
                    "$acc\n  $coords: $tile"
                }),
                "Decision queue:\n",
                decisionQueue.mapIndexed { index, decision -> "  #$index (${decision.player}) options:\n" +
                        decision.options.fold("", { acc, s -> "$acc    $s\n"}) + "\n"
                },
                "Next player: $nextPlayer\n",
                "Game phase: $gamePhase\n\n"
        )
        return ret.toString()
    }

    companion object {
        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)


        fun loadFromJson(obj: JSONObject): GameState {

            val boardObj = obj.getJSONArray("board")
            val board = HashMap.ofAll((boardObj as JSONArray)
                            .map {
                                (it as JSONObject)
                                val coords = it.getJSONArray("coords")
                                val x = coords[0] as Int
                                val y = coords[1] as Int
                                val tile = when (it.getString("tile")) {
                                    "X" -> BoardTile.X
                                    "O" -> BoardTile.O
                                    else -> throw Exception("Unknown tile")
                                }
                                Pair(Pair(x, y), tile)
                            }.toMap())

            val placePenguinPattern = Regex("Place tile to location \\(([-0-9]+), ([-0-9]+), ([-0-9]+)\\)")

            val decisionQueue = Queue.ofAll<Decision<GameState>>(obj.getJSONArray("decision_queue").map { decisionObj ->
                decisionObj as JSONObject
                val player = getPlayerFromString(decisionObj.getString("player"))
                val options = Vector.ofAll(decisionObj.getJSONArray("options").map { option ->
                    option as String
                    when (option) {
                        in placePenguinPattern -> {
                            val x = placePenguinPattern.matchEntire(option)!!.groupValues[1].toInt()
                            val y = placePenguinPattern.matchEntire(option)!!.groupValues[2].toInt()
                            PlaceTile(player, Pair(x, y))
                        }
                        else -> throw Exception("Action $option not found")
                    }
                })
                Decision(player, options)
            })

            val nextPlayer = getPlayerFromString(obj.getString("next_player"))

            val gamePhase = when (obj.getString("game_phase")) {
                "PLAYING" -> GamePhase.PLAYING
                "GAME_OVER" -> GamePhase.GAME_OVER
                else -> throw Exception("Game phase unknown ${obj.getString("game_phase")}")
            }

            return GameState(
                    board = board,
                    decisionQueue = decisionQueue,
                    gamePhase = gamePhase,
                    nextPlayer = nextPlayer
            )
        }


        fun generateBoard(): HashMap<Pair<Int,Int>,BoardTile?> {
            val entries = LinkedList<Pair<Pair<Int,Int>,BoardTile?>>()
            for (i in 0..2) {
                for (j in 0..2) {
                    entries.add(Pair(Pair(i, j), null))
                }
            }
            return HashMap.ofAll(entries.toMap())
        }
    }

}

/**
 * Determines the player after the current one.
 */
fun PlayerTurn.next() : PlayerTurn {
    return when (this) {
            PlayerTurn.PLAYER_1 -> PlayerTurn.PLAYER_2
            PlayerTurn.PLAYER_2 -> PlayerTurn.PLAYER_1
            else -> throw Exception("$this is not supposed to be playing")
    }
}


fun getPlayerFromString(s: String): PlayerTurn {
    return when (s) {
        "PLAYER_1" -> PlayerTurn.PLAYER_1
        "PLAYER_2" -> PlayerTurn.PLAYER_2
        "PLAYER_3" -> PlayerTurn.PLAYER_3
        "PLAYER_4" -> PlayerTurn.PLAYER_4
        else -> throw Exception("Player unknown $s")
    }
}