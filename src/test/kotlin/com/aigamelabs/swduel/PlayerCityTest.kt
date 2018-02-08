package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.BuildBuilding
import com.aigamelabs.swduel.actions.BurnForMoney
import com.aigamelabs.swduel.actions.ChooseProgressToken
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.HashSet
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*
import java.util.*

class PlayerCityTest : Spek ({
    given("these two cities") {
        val p1city = PlayerCity(
                name = "P1",
                coins = 7,
                buildings = HashSet.ofAll(setOf("Altar", "Theater", "Apothecary").map { CardFactory.getByName(it) }),
                unbuiltWonders = HashSet.ofAll(setOf("The Appian Way", "The Pyramids", "The Colossus", "The Statue of Zeus").map { CardFactory.getByName(it) }),
                wonders = HashSet.empty<Card>(),
                progressTokens = HashSet.empty<Card>()
        )
        val p2city = PlayerCity(
                name = "P1",
                coins = 7,
                buildings = HashSet.ofAll(setOf("Stone pit", "Clay pit", "Quarry").map { CardFactory.getByName(it) }),
                unbuiltWonders = HashSet.ofAll(setOf("The Great Lighthouse", "The Sphinx", "The Mausoleum", "The Great Library").map { CardFactory.getByName(it) }),
                wonders = HashSet.empty<Card>(),
                progressTokens = HashSet.empty<Card>()
        )


        on("checking if player 1 can build the pyramids") {
            it("should return false") {
                val cost = p1city.canBuild(CardFactory.getByName("The Pyramids"), p2city)
                assertEquals(null, cost)
            }
        }


        on("checking if player 1 can build the baths") {
            it("should return false") {
                val cost = p1city.canBuild(CardFactory.getByName("Baths"), p2city)
                assertEquals(4, cost)
            }
        }


        on("checking if player 1 can build the shelf quarry") {
            it("should return false") {
                val cost = p1city.canBuild(CardFactory.getByName("Shelf quarry"), p2city)
                assertEquals(2, cost)
            }
        }

        on("p1 burning the logging camp") {
            it("should decrease her coins by 2") {
                val card = CardFactory.getByName("Logging camp")
                val generator = RandomWithTracker(Random().nextLong())
                var cardStructure = CardStructureFactory.makeFirstAgeCardStructure(generator)
                cardStructure = CardStructure(cardStructure.graph.setVertex(0, card), cardStructure.faceDownPool)
                val gameState = GameStateFactory.createNewGameState(generator).update(
                        player1City_ = p1city,
                        player2City_ = p2city,
                        cardStructure_ = cardStructure
                )
                val updatedState = BurnForMoney(PlayerTurn.PLAYER_1, card).process(gameState, generator)
                val gain = updatedState.player1City.coins - p1city.coins
                assertEquals(2, gain)
            }
        }

        on("p2 building a science card of the same symbol of an existing one") {
            it("should prompt a progress token selection") {
                val upP2City = p1city.update(buildings_ = p1city.buildings.add(CardFactory.getByName("Pharmacist")))
                val card = CardFactory.getByName("Dispensary")
                val generator = RandomWithTracker(Random().nextLong())
                var cardStructure = CardStructureFactory.makeSecondCardStructure(generator)
                cardStructure = CardStructure(cardStructure.graph.setVertex(0, card), cardStructure.faceDownPool)
                val gameState = GameStateFactory.createNewGameState(generator).update(
                        player1City_ = p1city,
                        player2City_ = upP2City,
                        cardStructure_ = cardStructure
                )
                val updatedState = BuildBuilding(PlayerTurn.PLAYER_2, card).process(gameState, generator)
                assertTrue(updatedState.decisionQueue.first().options.first() is ChooseProgressToken)
            }
        }
/*
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
        }*/
    }
})