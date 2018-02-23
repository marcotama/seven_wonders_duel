package com.aigamelabs.game

enum class PlayerTurn {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    /**
     * Determines the player after the current one.
     */
    fun next() : PlayerTurn {
        return when (this) {
            PLAYER_1 -> PLAYER_2
            PLAYER_2 -> PLAYER_3
            PLAYER_3 -> PLAYER_4
            PLAYER_4 -> PLAYER_1
        }
    }

    /**
     * Determines the player before the current one.
     */
    fun previous() : PlayerTurn {
        return when (this) {
            PLAYER_1 -> PLAYER_4
            PLAYER_2 -> PLAYER_1
            PLAYER_3 -> PLAYER_2
            PLAYER_4 -> PLAYER_3
        }
    }
}