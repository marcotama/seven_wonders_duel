package com.aigamelabs.terraforming

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.*
import com.aigamelabs.myfish.next
import com.aigamelabs.terraforming.enums.*
import com.aigamelabs.utils.Deck
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.logging.Logger
import javax.json.stream.JsonGenerator



data class GameState(
        val playerStates : HashMap<PlayerTurn,PlayerState>,
        val oxygen: Int,
        val temperature: Int,
        val board: HashMap<Triple<Int, Int, Int>, TileType>,
        val decisionQueue: Queue<Decision<GameState>>,
        val gamePhase: GamePhase,
        private val nextPlayer: PlayerTurn
): AbstractGameState<GameState>() {


    fun update(
            playerStates_ : HashMap<PlayerTurn,PlayerState>? = null,
            oxygen_ : Int? = null,
            temperature_ : Int? = null,
            board_ : HashMap<Triple<Int, Int, Int>, TileType>? = null,
            decisionQueue_ : Queue<Decision<GameState>>? = null,
            gamePhase_: GamePhase? = null,
            nextPlayer_: PlayerTurn? = null
    ) : GameState {
        return GameState(
                playerStates_ ?: playerStates,
                oxygen_ ?: oxygen,
                temperature_ ?: temperature,
                board_ ?: board,
                decisionQueue_ ?: decisionQueue,
                gamePhase_ ?: gamePhase,
                nextPlayer_ ?: nextPlayer
        )
    }


    private val directions = Vector.of(
            Triple(+1,-1,0),
            Triple(+1,0,-1),
            Triple(-1,+1,0),
            Triple(-1,0,+1),
            Triple(0,+1,-1),
            Triple(0,-1,+1)
    )


    fun generateBoard(generator: RandomWithTracker): HashMap<Triple<Int, Int, Int>, TileType> {
        val entries = LinkedList<Pair<Triple<Int,Int,Int>, TileType>>()
        Vector.of(5, 6, 7, 8, 9)
                .forEachIndexed { i, l ->
                    (0 until l).forEach { j ->
                        val x = -i + j
                        val y = l - 1 - j
                        val z = -x - y
                        entries.add(Pair(Triple(x,y,z), TileType.EMPTY))
                    }
                }
        Vector.of(8, 7, 6, 5)
                .forEachIndexed { i, l ->
                    (0 until l).forEach { j ->
                        val x = l - 1 + j
                        val y = -i - j
                        val z = -x - y
                        entries.add(Pair(Triple(x,y,z), TileType.EMPTY))
                    }
                }
        return HashMap.ofAll(entries.toMap())
    }

    override fun isGameOver(): Boolean {
        return gamePhase == GamePhase.GAME_OVER
    }

    fun getPlayerStatus(playerTurn : PlayerTurn) : PlayerState {
        return playerStates[playerTurn]
                .getOrElseThrow { throw Exception("The player specified is not playing: $playerTurn.") }
    }

    private fun updateBoard(generator: RandomWithTracker, logger: Logger? = null) : GameState {
        return this
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

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("oxygen", oxygen)
        generator.write("temperature", temperature)

        generator.writeStartObject("player_states")
        playerStates.forEach {
            it._2.toJson(generator, it._1.toString())
        }
        generator.writeEnd()

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

        generator.writeStartArray("decision_queue")
        decisionQueue.forEach { it.toJson(generator, null) }
        generator.writeEnd()

        generator.write("game_phase", gamePhase.toString())
        generator.write("next_player", nextPlayer.toString())

        generator.writeEnd()
    }

    override fun toString(): String {
        val ret = StringBuilder()
        ret.append("Game phase: $gamePhase\n\n")
        ret.append("Oxygen: $oxygen\n\n")
        ret.append("Temperature: $temperature\n\n")
        playerStates.forEach {
            ret.append("Player ${it._1} state: \n\n ${it._2}")
        }
        ret.append(
                "Next player: $nextPlayer\n",
                "Decision queue:\n" +
                        decisionQueue.mapIndexed { index, decision -> "  #$index (${decision.player}) options:\n" +
                                decision.options.fold("") { acc, s -> "$acc    $s\n"} + "\n"
                        }
        )
        return ret.toString()
    }

    companion object {

        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

        fun loadFromJson(obj: JSONObject): GameState {
            val oxygen = obj.getInt("oxygen")
            val temperature = obj.getInt("temperature")

            val playerStatesObj = obj.getJSONObject("player_states")
            val playerStates = HashMap.ofAll(playerStatesObj.toMap()
                    .map { Pair(getPlayerFromString(it.key), PlayerState.loadFromJson(it.value as JSONObject)) }
                    .toMap()
            )

            val boardObj = obj.getJSONArray("board")
            val board = HashMap.ofAll((boardObj as JSONArray)
                    .map {
                        (it as JSONObject)
                        val coords = it.getJSONArray("coords")
                        val x = coords[0] as Int
                        val y = coords[1] as Int
                        val z = coords[2] as Int
                        val tile = getTileTypeFromString(it.getString("tile"))
                        Pair(Triple(x, y, z), tile)
                    }.toMap())

            val nextPlayer = getPlayerFromString(obj.getString("next_player")).next(playerStates.size())

            val decisionQueue = Queue.ofAll<Decision<GameState>>(obj.getJSONArray("decision_queue").map { decisionObj ->
                decisionObj as JSONObject
                val player = when (decisionObj.getString("player")) {
                    "PLAYER_1" -> PlayerTurn.PLAYER_1
                    "PLAYER_2" -> PlayerTurn.PLAYER_2
                    else -> throw Exception("Player unknown ${obj.getString("next_player")}")
                }
                val options = Vector.ofAll(decisionObj.getJSONArray("options").map { option ->
                    option as String
                    when (option) {
                        else -> throw Exception("Action $option not found")
                    }
                })
                Decision(player, options)
            })

            val gamePhase = when (obj.getString("game_phase")) {
                "CHOOSE_CORPORATION" -> GamePhase.CHOOSE_CORPORATION
                "MAIN_GAME" -> GamePhase.MAIN_GAME
                "GAME_OVER" -> GamePhase.GAME_OVER
                else -> throw Exception("Game phase unknown ${obj.getString("game_phase")}")
            }
            return GameState(
                    oxygen = oxygen,
                    temperature = temperature,
                    playerStates = playerStates,
                    board = board,
                    gamePhase = gamePhase,
                    nextPlayer = nextPlayer,
                    decisionQueue = decisionQueue
            )
        }
    }

}

fun loadDeckFromJson(obj: JSONObject): Deck<Card> {
    val name = obj.getString("name")
    val groupsObj = obj.getJSONArray("groups")
    val groups = Vector.ofAll(groupsObj.map { groupObj ->
        groupObj as JSONObject
        val discarded = groupObj.getInt("discarded")
        val cardsObj = groupObj.getJSONArray("cards")
        val cards = Vector.ofAll(cardsObj.map {
            CardFactory.getByName(it as String)
        })
        Pair(cards, discarded)
    })
    return Deck(name, groups)
}

/**
 * Determines the opponent of the given player.
 */
// This function is not in the PlayerTurn class because it assumes a 2-players game
fun PlayerTurn.opponent() : PlayerTurn {
    return if (this == PlayerTurn.PLAYER_1) PlayerTurn.PLAYER_2 else PlayerTurn.PLAYER_1
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


fun getTileTypeFromString(s: String): TileType {
    return when (s) {
        "EMPTY" -> TileType.EMPTY
        "CITY" -> TileType.CITY
        "GREENERY" -> TileType.GREENERY
        "OCEAN" -> TileType.OCEAN
        else -> throw Exception("Unknown tile")
    }
}