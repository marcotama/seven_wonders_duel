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
import com.aigamelabs.myfish.enums.PenguinId
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.Tuple5
import io.vavr.collection.*
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import io.vavr.collection.List
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.json.stream.JsonGenerator
import kotlin.collections.ArrayList


data class GameState(
        val board: HashMap<Triple<Int, Int, Int>, BoardTile>,
        val penguins: HashMap<PlayerTurn,HashMap<PenguinId,Triple<Int,Int,Int>>>,
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
            penguins_ : HashMap<PlayerTurn,HashMap<PenguinId,Triple<Int,Int,Int>>>? = null,
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

    private fun getFishesOnTile(location: Triple<Int,Int,Int>):Int {
        val boardTile = board[location].getOrElseThrow {Exception("Location $location does not exist")}!!
        return when (boardTile) {
            BoardTile.TILE_WITH_1_FISH -> 1
            BoardTile.TILE_WITH_2_FISH -> 2
            BoardTile.TILE_WITH_3_FISH -> 3
            BoardTile.EATEN_TILE -> throw Exception("The tile $location has been previously eaten")
        }
    }

    fun placePenguin(player: PlayerTurn, penguinId: PenguinId, location: Triple<Int,Int,Int>): GameState {
        val updatedPlayerPenguins = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .put(penguinId, location)
        val updatedPenguins = penguins.put(player, updatedPlayerPenguins)
        return update(penguins_ = updatedPenguins)
    }

    fun movePenguin(player: PlayerTurn, penguinId: PenguinId, location: Triple<Int,Int,Int>): GameState {
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

    private fun getPlayerPenguinsId(player: PlayerTurn): List<PenguinId> {
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
        val allPenguinLocations = Stream.concat(penguins.values().map { it.values() }).toSet()
        return directions
                .toStream()
                .any {
                    val loc = location + it
                    when {
                        !board.containsKey(loc) -> false // off the board
                        board[loc].getOrElse(BoardTile.EATEN_TILE) == BoardTile.EATEN_TILE -> false // previously eaten
                        allPenguinLocations.contains(loc) -> false // other penguin on the tile
                        else -> true
                    }
                }
    }

    fun getAvailableDestinations(location: Triple<Int, Int, Int>): List<Triple<Int,Int,Int>> {
        val allPenguinLocations = Stream.concat(penguins.values().map { it.values() }).toSet()

        return Stream.concat(
                directions.map { dir ->
                    val firstMissing =
                            (1 until 8)
                                    .find {
                                        val loc =  location + dir * it
                                        when {
                                            !board.containsKey(loc) -> true // off the board
                                            board[loc].getOrElse(BoardTile.EATEN_TILE) == BoardTile.EATEN_TILE -> true // previously eaten
                                            allPenguinLocations.contains(loc) -> true // other penguin on the tile
                                            else -> false
                                        }
                                    }
                    (1 until (firstMissing ?: 8))
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
        val allPenguinLocations = Stream.concat(penguins.values().map { it.values() }).toSet()
        val options = getAvailableTiles()
                .toStream()
                .filter { !allPenguinLocations.contains(it) }
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
            val playerPenguins = penguins[player].getOrElseThrow { Exception("No such player: $player") }
            val options = getPlayerPenguinsId(player)
                    .toStream()
                    .filter { canPenguinMove(playerPenguins[it].getOrElseThrow { Exception("No such penguin: $it")}) }
                    .map { ChoosePenguin(player, it) }
                    .toVector()
            val decision = Decision(player, options)
            enqueueDecision(decision)
        }
    }

    fun addMovePenguinDecision(player: PlayerTurn, penguinId: PenguinId): GameState {
        val penguinLocation = penguins
                .get(player)
                .getOrElseThrow { Exception("There is no such player: $player") }
                .get(penguinId)
                .getOrElseThrow { Exception("There is no such penguin: $penguinId") }
        val options = getAvailableDestinations(penguinLocation).toStream().map { MovePenguin(player, penguinId, it)}.toVector()
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
            val maxScore = PlayerTurn.values().map { score[it].getOrElse {0} }.max()
            val winners = PlayerTurn.values().filter { score[it].getOrElse(0) == maxScore }
            val p1Score = score[PlayerTurn.PLAYER_1].getOrElse {0}
            logger?.log(Level.INFO, "Player 1 ate $p1Score fishes")
            val p2Score = score[PlayerTurn.PLAYER_2].getOrElse {0}
            logger?.log(Level.INFO, "Player 2 ate $p2Score fishes")
            val p3Score = score[PlayerTurn.PLAYER_3].getOrElse {0}
            logger?.log(Level.INFO, "Player 3 ate $p3Score fishes")
            val p4Score = score[PlayerTurn.PLAYER_4].getOrElse {0}
            logger?.log(Level.INFO, "Player 4 ate $p4Score fishes")
            return Tuple5(HashSet.ofAll(winners), p1Score, p2Score, p3Score, p4Score)
        }
    }


    fun isAnyPenguinOnTile(location: Triple<Int, Int, Int>): Boolean {
        val allPenguinLocations = Stream.concat(penguins.values().map { it.values() }).toSet()
        return allPenguinLocations.contains(location)
    }

    fun findPenguinOnTile(location: Triple<Int, Int, Int>):Pair<PlayerTurn, PenguinId>? {
        penguins.forEach { playerPenguins ->
            playerPenguins._2.forEach { penguinLocation ->
                if (penguinLocation._2 == location)
                    return Pair(playerPenguins._1, penguinLocation._1)
            }
        }
        return null
    }


    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toJson(generator: JsonGenerator, name: String?) {
        /*
        val board: HashMap<Triple<Int, Int, Int>, BoardTile>,
        */
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
            generator.write(coords.third)
            generator.writeEnd()
            generator.write("tile", tile.toString())
            generator.writeEnd()
        }
        generator.writeEnd()

        generator.writeStartObject("penguins")
        penguins.forEach { playerPenguins ->
            generator.writeStartArray(playerPenguins._1.toString())
            playerPenguins._2.forEach {
                val penguinCoords = it._2
                generator.writeStartArray()
                generator.write(penguinCoords.first)
                generator.write(penguinCoords.second)
                generator.write(penguinCoords.third)
                generator.writeEnd()
            }
            generator.writeEnd()
        }
        generator.writeEnd()

        generator.writeStartObject("score")
        score.forEach { generator.write(it._1.toString(), it._2) }
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
                board.fold("") { acc, it ->
                    val coords = it._1
                    val tile = it._2
                    "$acc\n  $coords: $tile"
                },
                "Penguins:\n",
                penguins.fold("") { acc, playerPenguins ->
                    val player = playerPenguins._1
                    val coords = playerPenguins._2
                    val s = coords.fold("  $player>:\n") { acc2, it ->
                        val penguinId = it._1
                        val penguinCoords = it._2
                        "$acc2    $penguinId: $penguinCoords"
                    }
                    "$acc$s"
                },
                "Score:\n",
                score.fold("") { acc, it ->
                    val player = it._1
                    val score = it._2
                    "$acc\n  $player: $score"
                },
                "Decision queue:\n",
                decisionQueue.mapIndexed { index, decision -> "  #$index (${decision.player}) options:\n" +
                        decision.options.fold("") { acc, s -> "$acc    $s\n"} + "\n"
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
                                val z = coords[2] as Int
                                val tile = when (it.getString("tile")) {
                                    "TILE_WITH_1_FISH" -> BoardTile.TILE_WITH_1_FISH
                                    "TILE_WITH_2_FISH" -> BoardTile.TILE_WITH_2_FISH
                                    "TILE_WITH_3_FISH" -> BoardTile.TILE_WITH_3_FISH
                                    "EATEN_TILE" -> BoardTile.EATEN_TILE
                                    else -> throw Exception("Unknown tile")
                                }
                                Pair(Triple(x, y, z), tile)
                            }.toMap())

            val penguinsObj = obj.getJSONObject("penguins")
            val penguins = HashMap.ofAll(penguinsObj.toMap()
                    .map { playerPenguins ->
                        val player = getPlayerFromString(playerPenguins.key)
                        val penguinsCoords = HashMap.ofAll((playerPenguins.value as ArrayList<*>)
                                .mapIndexed { i, it ->
                                    it as ArrayList<*>
                                    val penguinId = when (i) {
                                        0 -> PenguinId.A
                                        1 -> PenguinId.B
                                        2 -> PenguinId.C
                                        3 -> PenguinId.D
                                        else -> throw Exception("No such penguin: $i")
                                    }
                                    val x = it[0] as Int
                                    val y = it[1] as Int
                                    val z = it[2] as Int
                                    Pair(penguinId, Triple(x, y, z))
                                }.toMap())

                        Pair(player, penguinsCoords)
                    }
                    .toMap()
            )

            val scoreObj = obj.getJSONObject("score")
            val score = HashMap.ofAll(scoreObj.toMap()
                    .map { Pair(getPlayerFromString(it.key), (it.value as Int)) }
                    .toMap()
            )

            val placePenguinPattern = Regex("Place penguin to location \\(([-0-9]+), ([-0-9]+), ([-0-9]+)\\)")
            val choosePenguinPattern = Regex("Choose penguin (\\d+) for next move")
            val movePenguinPattern = Regex("Move penguin (\\d+) to location \\(([-0-9]+), ([-0-9]+), ([-0-9]+)\\)")

            val decisionQueue = Queue.ofAll<Decision<GameState>>(obj.getJSONArray("decision_queue").map { decisionObj ->
                decisionObj as JSONObject
                val player = getPlayerFromString(decisionObj.getString("player"))
                val options = Vector.ofAll(decisionObj.getJSONArray("options").map { option ->
                    option as String
                    when (option) {
                        in placePenguinPattern -> {
                            val x = placePenguinPattern.matchEntire(option)!!.groupValues[1].toInt()
                            val y = placePenguinPattern.matchEntire(option)!!.groupValues[2].toInt()
                            val z = placePenguinPattern.matchEntire(option)!!.groupValues[3].toInt()
                            PlacePenguin(player, Triple(x, y, z))
                        }
                        in choosePenguinPattern -> {
                            val penguinStr = choosePenguinPattern.matchEntire(option)!!.groupValues[1]
                            val penguinId = when (penguinStr) {
                                "A" -> PenguinId.A
                                "B" -> PenguinId.B
                                "C" -> PenguinId.C
                                "D" -> PenguinId.D
                                else -> throw Exception("No such penguin: $penguinStr")
                            }
                            ChoosePenguin(player, penguinId)
                        }
                        in movePenguinPattern -> {
                            val penguinStr = choosePenguinPattern.matchEntire(option)!!.groupValues[1]
                            val penguinId = when (penguinStr) {
                                "A" -> PenguinId.A
                                "B" -> PenguinId.B
                                "C" -> PenguinId.C
                                "D" -> PenguinId.D
                                else -> throw Exception("No such penguin: $penguinStr")
                            }
                            val x = movePenguinPattern.matchEntire(option)!!.groupValues[2].toInt()
                            val y = movePenguinPattern.matchEntire(option)!!.groupValues[3].toInt()
                            val z = movePenguinPattern.matchEntire(option)!!.groupValues[4].toInt()
                            MovePenguin(player, penguinId, Triple(x, y, z))
                        }
                        else -> throw Exception("Action $option not found")
                    }
                })
                Decision(player, options)
            })

            val nextPlayer = getPlayerFromString(obj.getString("next_player"))

            val gamePhase = when (obj.getString("game_phase")) {
                "PENGUINS_PLACEMENT" -> GamePhase.PENGUINS_PLACEMENT
                "FISHES_EATING" -> GamePhase.FISHES_EATING
                "GAME_OVER" -> GamePhase.GAME_OVER
                else -> throw Exception("Game phase unknown ${obj.getString("game_phase")}")
            }

            return GameState(
                    board = board,
                    penguins = penguins,
                    score = score,
                    decisionQueue = decisionQueue,
                    gamePhase = gamePhase,
                    nextPlayer = nextPlayer
            )
        }


        fun generateBoard(generator: RandomWithTracker): HashMap<Triple<Int,Int,Int>,BoardTile> {
            val tokens = Stream.concat(
                    (0 until 30).map { BoardTile.TILE_WITH_1_FISH },
                    (0 until 20).map { BoardTile.TILE_WITH_2_FISH },
                    (0 until 10).map { BoardTile.TILE_WITH_3_FISH }
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
            else -> throw Exception("$this is not supposed to be playing")
        }
        else -> throw Exception("Cannot have less than 2 or more than 4 players")
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