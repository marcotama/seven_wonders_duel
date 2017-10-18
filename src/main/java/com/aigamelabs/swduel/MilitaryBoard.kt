package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn

data class MilitaryBoard(
        val militaryTokenPosition : Int,
        val token1P1Present : Boolean,
        val token2P1Present : Boolean,
        val token1P2Present : Boolean,
        val token2P2Present : Boolean
) {
    constructor() : this(0, true, true, true, true)

    fun update(
            militaryTokenPosition_ : Int? = null,
            token1P1Present_ : Boolean? = null,
            token2P1Present_ : Boolean? = null,
            token1P2Present_ : Boolean? = null,
            token2P2Present_ : Boolean? = null
    ) : MilitaryBoard {
        return MilitaryBoard(
                militaryTokenPosition_ ?: militaryTokenPosition,
                token1P1Present_ ?: token1P1Present,
                token2P1Present_ ?: token2P1Present,
                token1P2Present_ ?: token1P2Present,
                token2P2Present_ ?: token2P2Present
        )
    }

    fun advantagePlayer(n : Int, playerTurn: PlayerTurn) : Pair<Int,MilitaryBoard> {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1 -> {
                advantagePlayer1 (n)
            }
            PlayerTurn.PLAYER_2 -> {
                advantagePlayer2(n)
            }
        }
    }

    fun advantagePlayer1(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = militaryTokenPosition - n
        var cost = 0
        cost += if (token1P1Present && newPosition <= -3) 2 else 0
        cost += if (token1P1Present && newPosition <= -6) 5 else 0
        val newToken1P1Present = token1P1Present && newPosition > -3
        val newToken2P1Present = token2P1Present && newPosition > -6
        val newBoard = update(
                militaryTokenPosition_ = newPosition,
                token1P1Present_ = newToken1P1Present,
                token2P1Present_ = newToken2P1Present
        )
        return Pair(cost, newBoard)
    }
    fun advantagePlayer2(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = militaryTokenPosition + n
        var cost = 0
        cost += if (token1P2Present && newPosition >= +3) 2 else 0
        cost += if (token1P2Present && newPosition >= +6) 5 else 0
        val newToken1P2Present = token1P2Present && newPosition > +3
        val newToken2P2Present = token2P2Present && newPosition > +6
        val newBoard = update(
                militaryTokenPosition_ = newPosition,
                token1P1Present_ = newToken1P2Present,
                token2P1Present_ = newToken2P2Present
        )
        return Pair(cost, newBoard)
    }
}