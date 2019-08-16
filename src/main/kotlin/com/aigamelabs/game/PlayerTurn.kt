package com.aigamelabs.game

enum class PlayerTurn {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4,
    PLAYER_5,
    PLAYER_6,
    PLAYER_7,
    PLAYER_8,
    PLAYER_9,
    PLAYER_10,
    PLAYER_11,
    PLAYER_12;

    companion object {
        fun getPlayers(numPlayers: Int): List<PlayerTurn> {
            return when (numPlayers) {
                2 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2)
                3 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3)
                4 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3, PlayerTurn.PLAYER_4)
                else -> throw Exception("Cannot have $numPlayers players")
            }
        }

        fun getPlayerNumber(playerTurn: PlayerTurn): Int {
            return when (playerTurn) {
                PLAYER_1 -> 1
                PLAYER_2 -> 2
                PLAYER_3 -> 3
                PLAYER_4 -> 4
                PLAYER_5 -> 5
                PLAYER_6 -> 6
                PLAYER_7 -> 7
                PLAYER_8 -> 8
                PLAYER_9 -> 9
                PLAYER_10 -> 10
                PLAYER_11 -> 11
                PLAYER_12 -> 12
            }
        }
    }
}