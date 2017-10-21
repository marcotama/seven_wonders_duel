package com.aigamelabs.swduel.enums

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class PlayerTurnTest : Spek({
    given("the enumerator for players") {

        on("PLAYER_1.opponent()") {
            val player = PlayerTurn.PLAYER_1
            val opponent = player.opponent()

            it("should return PLAYER_2") {
                assertEquals(opponent, PlayerTurn.PLAYER_2)
            }
        }

    }
})