package com.aigamelabs.swduel.enums

enum class PlayerTurn {
    PLAYER_1,
    PLAYER_2;

    fun opponent() : PlayerTurn {
        return if (this == PLAYER_1) PLAYER_2 else PLAYER_1
    }
}