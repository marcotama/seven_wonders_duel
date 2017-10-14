package com.aigamelabs.swduel

data class MilitaryBoard(
        val militaryTokenPosition : Int,
        val token1P1Present : Boolean,
        val token2P1Present : Boolean,
        val token1P2Present : Boolean,
        val token2P2Present : Boolean
) {
    constructor() : this(0, true, true, true, true)

    fun moveTowardsBy(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = militaryTokenPosition - n
        var cost = 0
        cost += if (token1P1Present && newPosition <= -3) 2 else 0
        cost += if (token1P1Present && newPosition <= -6) 5 else 0
        val newToken1P1Present = token1P1Present && newPosition > -3
        val newToken2P1Present = token2P1Present && newPosition > -6
        val newBoard = MilitaryBoard(newPosition, newToken1P1Present, newToken2P1Present, token1P2Present, token2P2Present)
        return Pair(cost, newBoard)
    }
    fun moveRightBy(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = militaryTokenPosition + n
        var cost = 0
        cost += if (token1P2Present && newPosition >= +3) 2 else 0
        cost += if (token1P2Present && newPosition >= +6) 5 else 0
        val newToken1P2Present = token1P2Present && newPosition > +3
        val newToken2P2Present = token2P2Present && newPosition > +6
        val newBoard = MilitaryBoard(newPosition, token1P1Present, token2P1Present, newToken1P2Present, newToken2P2Present)
        return Pair(cost, newBoard)
    }
}