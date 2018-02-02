package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import javax.json.stream.JsonGenerator

/**
 * Represents the military situation.
 */
data class MilitaryBoard(
        private val conflictPawnPosition: Int,
        val token1P1Present : Boolean,
        val token2P1Present : Boolean,
        val token1P2Present : Boolean,
        val token2P2Present : Boolean
) {
    /*
    Stores an integer representing the position of the conflict pawn, with positive values indicating an advantage for
    player 1 and negative values indicating an advantage for player 2.

    Also, stores four booleans, one for each military token.
     */
    constructor() : this(0, true, true, true, true)

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("conflict_pawn_position", conflictPawnPosition)
        generator.write("token1_player1", token1P1Present)
        generator.write("token2_player1", token2P1Present)
        generator.write("token1_player2", token1P2Present)
        generator.write("token2_player2", token2P2Present)

        generator.writeEnd()
    }

    override fun toString(): String {
        return "Conflict pawn position: $conflictPawnPosition\n" +
                "Tokens on Player 1 side:\n" +
                "  ${if (token1P1Present) "O" else "X"} 2 coins\n" +
                "  ${if (token2P1Present) "O" else "X"} 5 coins\n" +
                "Tokens on Player 2 side:\n" +
                "  ${if (token1P2Present) "O" else "X"} 2 coins\n" +
                "  ${if (token2P2Present) "O" else "X"} 5 coins"
    }

    /**
     * Creates a new instance, with every field updated as specified. Null values are ignored.
     *
     * @param conflictPawnPosition_ the new position of the conflict pawn
     * @param token1P1Present_ the new value for the flag indicating whether token 1 of player 1 is present
     * @param token1P2Present_ the new value for the flag indicating whether token 1 of player 2 is present
     * @param token2P1Present_ the new value for the flag indicating whether token 2 of player 1 is present
     * @param token2P2Present_ the new value for the flag indicating whether token 2 of player 2 is present
     */
    fun update(
            conflictPawnPosition_: Int? = null,
            token1P1Present_ : Boolean? = null,
            token2P1Present_ : Boolean? = null,
            token1P2Present_ : Boolean? = null,
            token2P2Present_ : Boolean? = null
    ) : MilitaryBoard {
        return MilitaryBoard(
                conflictPawnPosition_ ?: conflictPawnPosition,
                token1P1Present_ ?: token1P1Present,
                token2P1Present_ ?: token2P1Present,
                token1P2Present_ ?: token1P2Present,
                token2P2Present_ ?: token2P2Present
        )
    }

    fun getConflictPawnState(): Pair<PlayerTurn?, Int> {
        return Pair(getAdvantagedPlayer(), Math.abs(conflictPawnPosition))
    }

    /**
     * Adds military points to the given player.
     *
     * @param n the amount of military points
     * @param playerTurn the player of interest
     * @return the cost in coins to be paid and an updated instance of the military board
     */
    fun addMilitaryPointsTo(n : Int, playerTurn: PlayerTurn) : Pair<Int,MilitaryBoard> {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1 -> {
                addPointsToPlayer1(n)
            }
            PlayerTurn.PLAYER_2 -> {
                addPointsToPlayer2(n)
            }
        }
    }

    private fun addPointsToPlayer1(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = conflictPawnPosition + n
        var cost = 0
        cost += if (token1P1Present && newPosition >= +3) 2 else 0
        cost += if (token1P1Present && newPosition >= +6) 5 else 0
        val newToken1P1Present = token1P1Present && newPosition < +3
        val newToken2P1Present = token2P1Present && newPosition < +6
        val newBoard = update(
                conflictPawnPosition_ = newPosition,
                token1P1Present_ = newToken1P1Present,
                token2P1Present_ = newToken2P1Present
        )
        return Pair(cost, newBoard)
    }

    private fun addPointsToPlayer2(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = conflictPawnPosition - n
        var cost = 0
        cost += if (token1P2Present && newPosition <= -3) 2 else 0
        cost += if (token1P2Present && newPosition <= -6) 5 else 0
        val newToken1P2Present = token1P2Present && newPosition > -3
        val newToken2P2Present = token2P2Present && newPosition > -6
        val newBoard = update(
                conflictPawnPosition_ = newPosition,
                token1P2Present_ = newToken1P2Present,
                token2P2Present_ = newToken2P2Present
        )
        return Pair(cost, newBoard)
    }

    fun isMilitarySupremacy() : Boolean {
        return Math.abs(conflictPawnPosition) >= 9
    }

    fun getAdvantagedPlayer() : PlayerTurn? {
        return when {
            conflictPawnPosition > 0 -> PlayerTurn.PLAYER_1
            conflictPawnPosition < 0 -> PlayerTurn.PLAYER_2
            else -> null
        }
    }

    fun getDisadvantagedPlayer() : PlayerTurn? {
        return when {
            conflictPawnPosition > 0 -> PlayerTurn.PLAYER_2
            conflictPawnPosition < 0 -> PlayerTurn.PLAYER_1
            else -> null
        }
    }

    fun getVictoryPoints(player: PlayerTurn): Int {
        return when (conflictPawnPosition) {
            0 -> 0
            +1, +2 -> when (player) {
                PlayerTurn.PLAYER_1 -> 2
                PlayerTurn.PLAYER_2 -> 0
            }
            -1, -2 -> when (player) {
                PlayerTurn.PLAYER_1 -> 0
                PlayerTurn.PLAYER_2 -> 2
            }
            +3, +4, +5 -> when (player) {
                PlayerTurn.PLAYER_1 -> 5
                PlayerTurn.PLAYER_2 -> 0
            }
            -3, -4, -5 -> when (player) {
                PlayerTurn.PLAYER_1 -> 0
                PlayerTurn.PLAYER_2 -> 5
            }
            +6, +7, +8 -> when (player) {
                PlayerTurn.PLAYER_1 -> 10
                PlayerTurn.PLAYER_2 -> 0
            }
            -6, -7, -8 -> when (player) {
                PlayerTurn.PLAYER_1 -> 0
                PlayerTurn.PLAYER_2 -> 10
            }
            +9, -9 -> throw Exception("Conflict pawn is far enough to grant military supremacy; why is this function being called: : $conflictPawnPosition")
            else -> throw Exception("Conflict pawn in an invalid position: $conflictPawnPosition")
        }
    }
}