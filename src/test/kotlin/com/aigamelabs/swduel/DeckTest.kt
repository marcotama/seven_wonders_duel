package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.CardGroup
import com.aigamelabs.swduel.enums.Resource
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.Vector
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*
import java.util.*

class DeckTest : Spek ({
    given("a deck") {
        val deck = DeckFactory.createFirstAgeDeck()
        val firstAgeCards = CardFactory.createFromFirstAge()
        val generator = RandomWithTracker(Random().nextLong())

        on("changing name") {
            val newName = "New deck"
            val newDeck = deck.update(name_ = newName)

            it("should return a new deck with the new name") {
                assertEquals(newDeck.name, newName)
            }
        }

        on("removing a card") {
            val card = firstAgeCards[0]
            val newDeck = deck.removeCard(card)

            it("should return a new deck with the card removed") {
                assertTrue(deck.cards.contains(card))
                assertTrue(!newDeck.cards.contains(card))
            }
        }

        on("drawing a card") {
            val drawOutcome = deck.drawCard(generator)
            val drawnCard = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the card removed") {
                assertTrue(deck.cards.contains(drawnCard))
                assertTrue(!newDeck.cards.contains(drawnCard))
            }
        }

        on("drawing multiple cards") {
            val drawOutcome = deck.drawCards(3, generator)
            val drawnCards = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the cards removed") {
                assertTrue(deck.cards.containsAll(drawnCards))
                drawnCards.forEach { assertFalse(newDeck.cards.contains(it)) }
            }
        }

        on("adding a card") {
            val card = Card(cardGroup = CardGroup.SECOND_AGE, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2))
            val newDeck = deck.add(card)

            it("should return a new deck with the card added") {
                assertFalse(deck.cards.contains(card))
                assertTrue(newDeck.cards.contains(card))
            }
        }

        on("adding multiple cards") {
            val newCards = Vector.of(
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2)),
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Brickyard", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.CLAY to 2)),
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Shelf quarry", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.STONE to 2))
            )
            val newDeck = deck.addAll(newCards)

            it("should return a new deck with the cards added") {
                newCards.forEach { assertFalse(deck.cards.contains(it)) }
                assertTrue(newDeck.cards.containsAll(newCards))
            }
        }

        on("a querying for the size of a deck") {

            it("should return the number of cards in the deck") {
                assertEquals(deck.size(), 23)
            }
        }
    }
})