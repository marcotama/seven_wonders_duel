package com.aigamelabs.game

enum class PlayerTurn {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    companion object {
        fun getPlayers(numPlayers: Int): List<PlayerTurn> {
            return when (numPlayers) {
                2 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2)
                3 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3)
                4 -> listOf(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3, PlayerTurn.PLAYER_4)
                else -> throw Exception("Cannot have $numPlayers players")
            }
        }
    }
}