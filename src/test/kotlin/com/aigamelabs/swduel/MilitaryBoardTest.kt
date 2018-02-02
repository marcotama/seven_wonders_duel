package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class MilitaryBoardTest : Spek ({
    given("a fresh military board") {
        val militaryBoard = MilitaryBoard()

        on("adding 2 military points to P1") {
            val (cost, newMilitaryBoard) = militaryBoard.addMilitaryPointsTo(2, PlayerTurn.PLAYER_1)

            it("should return 0 as the cost for the opponent") {
                assertEquals(0, cost)
            }

            it("should move the conflict pawn by two positions towards P1") {
                assertEquals(Pair(PlayerTurn.PLAYER_1, 2), newMilitaryBoard.getConflictPawnState())
            }

            it("should identify P1 as the advantaged player") {
                assertEquals(PlayerTurn.PLAYER_1, newMilitaryBoard.getAdvantagedPlayer())
            }

            it("should identify P2 as the disadvantaged player") {
                assertEquals(PlayerTurn.PLAYER_2, newMilitaryBoard.getDisadvantagedPlayer())
            }

            it("should still have all tokens on it") {
                assertTrue(newMilitaryBoard.token1P1Present)
                assertTrue(newMilitaryBoard.token2P1Present)
                assertTrue(newMilitaryBoard.token1P2Present)
                assertTrue(newMilitaryBoard.token2P2Present)
            }

            it("should return 2 as the victory points for P1") {
                assertEquals(2, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_1))
            }

            it("should return 0 as the victory points for P2") {
                assertEquals(0, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_2))
            }
        }

        on("adding 2 military points to P2") {
            val (cost, newMilitaryBoard) = militaryBoard.addMilitaryPointsTo(2, PlayerTurn.PLAYER_2)

            it("should return 0 as the cost for the opponent") {
                assertEquals(0, cost)
            }

            it("should move the conflict pawn by two positions towards P2") {
                assertEquals(Pair(PlayerTurn.PLAYER_2, 2), newMilitaryBoard.getConflictPawnState())
            }

            it("should identify P2 as the advantaged player") {
                assertEquals(PlayerTurn.PLAYER_2, newMilitaryBoard.getAdvantagedPlayer())
            }

            it("should identify P1 as the disadvantaged player") {
                assertEquals(PlayerTurn.PLAYER_1, newMilitaryBoard.getDisadvantagedPlayer())
            }

            it("should still have all tokens on it") {
                assertTrue(newMilitaryBoard.token1P1Present)
                assertTrue(newMilitaryBoard.token2P1Present)
                assertTrue(newMilitaryBoard.token1P2Present)
                assertTrue(newMilitaryBoard.token2P2Present)
            }

            it("should return 0 as the victory points for P1") {
                assertEquals(0, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_1))
            }

            it("should return 2 as the victory points for P2") {
                assertEquals(2, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_2))
            }
        }

        on("adding 4 military points to P1") {
            val newMilitaryBoard = militaryBoard.addMilitaryPointsTo(4, PlayerTurn.PLAYER_1).second

            it("should still have all tokens on it except for the one worth 2 coins on P1 side") {
                assertFalse(newMilitaryBoard.token1P1Present)
                assertTrue(newMilitaryBoard.token2P1Present)
                assertTrue(newMilitaryBoard.token1P2Present)
                assertTrue(newMilitaryBoard.token2P2Present)
            }

            it("should return 5 as the victory points for P1") {
                assertEquals(5, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_1))
            }

            it("should return 0 as the victory points for P2") {
                assertEquals(0, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_2))
            }
        }

        on("adding 7 military points to P1") {
            val newMilitaryBoard = militaryBoard.addMilitaryPointsTo(7, PlayerTurn.PLAYER_1).second

            it("should still have all P2 tokens and none of P1 tokens") {
                assertFalse(newMilitaryBoard.token1P1Present)
                assertFalse(newMilitaryBoard.token2P1Present)
                assertTrue(newMilitaryBoard.token1P2Present)
                assertTrue(newMilitaryBoard.token2P2Present)
            }

            it("should return 10 as the victory points for P1") {
                assertEquals(10, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_1))
            }

            it("should return 0 as the victory points for P2") {
                assertEquals(0, newMilitaryBoard.getVictoryPoints(PlayerTurn.PLAYER_2))
            }
        }

        on("adding 9 military points to P1") {
            val newMilitaryBoard = militaryBoard.addMilitaryPointsTo(9, PlayerTurn.PLAYER_1).second

            it("should test true for military supremacy") {
                assertTrue(newMilitaryBoard.isMilitarySupremacy())
            }
        }
    }
})