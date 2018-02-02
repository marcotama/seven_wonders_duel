package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.Vector
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class CardStructureTest : Spek ({
    given("a fresh card structure") {
        val firstAgeStructure = CardStructureFactory.makeFirstAgeCardStructure(RandomWithTracker(0))
        /*
Vertices:
    Altar: 0
    Baths: 1
    Stone pit: 2
    Theater: 3
    Press: 4
    Scriptorium: 5
    Face down card: 6
    Face down card: 7
    Face down card: 8
    Face down card: 9
    Face down card: 10
    Clay pit: 11
    Workshop: 12
    Apothecary: 13
    Pharmacist: 14
    Face down card: 15
    Face down card: 16
    Face down card: 17
    Glassworks: 18
    Logging camp: 19

Edges:
    0 -> 6
    1 -> 6
    1 -> 7
    2 -> 7
    2 -> 8
    3 -> 8
    3 -> 9
    4 -> 9
    4 -> 10
    5 -> 10
    6 -> 11
    7 -> 11
    7 -> 12
    8 -> 12
    8 -> 13
    9 -> 13
    9 -> 14
    10 -> 14
    11 -> 15
    12 -> 15
    12 -> 16
    13 -> 16
    13 -> 17
    14 -> 17
    15 -> 18
    16 -> 18
    16 -> 19
    17 -> 19

Face-down cards pool:
    Palisade
    Stable
    Garrison
    Guard tower
    Wood reserve
    Clay reserve
    Stone reserve
    Tavern
        * */


        on("checking available cards") {
            val availableCardsTrue = Vector.of(0, 1, 2, 3, 4, 5)
                    .map { (firstAgeStructure.graph.vertices[it] as Card).name }
                    .sorted()

            it("should return a list of available cards") {
                val availableCardsReturned = firstAgeStructure.availableCards().map { it.name }.sorted()
                assertEquals(availableCardsReturned, availableCardsTrue)
            }
        }

        on("picking up cards") {
            val cardsToPickUp = Vector.of(2, 3, 4).map { firstAgeStructure.graph.vertices[it] as Card }
            var newStructure = firstAgeStructure
            cardsToPickUp.forEach {
                newStructure = newStructure.pickUpCard(it, RandomWithTracker(0))
            }
            val availableCardsTrue = listOf(8, 9).map { newStructure.graph.vertices[it] as Card }

            it("should replace picked up cards with nulls") {
                assertTrue(newStructure.graph.vertices[2] == null)
                assertTrue(newStructure.graph.vertices[3] == null)
                assertTrue(newStructure.graph.vertices[4] == null)
            }

            it("should remove the edges from the graph") {
                assertFalse(newStructure.graph.isEdge(2, 8))
                assertFalse(newStructure.graph.isEdge(2, 9))
            }

            it("should flip the newly available previously facing down cards") {
                assertTrue(newStructure.availableCards().containsAll(availableCardsTrue))
            }

            it("should remove the flipped cards from the pool") {
                availableCardsTrue
                        .map { newStructure.faceDownPool.cards.contains(it) }
                        .forEach { assertFalse(it) }
            }
        }

        on("picking up the first available card for 20 times") {
            var newStructure = firstAgeStructure
            (0 until 20).forEach {
                newStructure = newStructure.pickUpCard(newStructure.availableCards()[0], RandomWithTracker(0))
            }

            it("should only have nulls left") {
                newStructure.graph.vertices.forEach { assertTrue(it == null) }
            }

            it("should have no edges left") {
                newStructure.graph.adjMatrix.forEach { assertFalse(it) }
            }

            it("should have no available cards") {
                assertEquals(0, newStructure.availableCards().size())
            }

            it("should have no cards in the pool") {
                assertEquals(0, newStructure.faceDownPool.size())
            }
        }
    }
})