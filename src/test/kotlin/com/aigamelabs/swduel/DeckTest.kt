package com.aigamelabs.swduel

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class DeckTest : Spek ({
    given("a deck") {
        val firstAgeCards = CardFactory.createFromFirstAge()
        val deck = Deck("", firstAgeCards)

        on("changing name") {
            val newName = "New deck"
            val newDeck = deck.update(name_ = newName)

            it("should return a new deck with the new name") {
                assertEquals(newDeck.name, newName)
            }
        }

        on("changing cards") {
            val secondAgeCards = CardFactory.createFromSecondAge()
            val newDeck = deck.update(cards_ = secondAgeCards)

            it("should return a new deck with the new cards") {
                assertEquals(newDeck.cards, secondAgeCards)
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
            val drawOutcome = deck.drawCard()
            val drawnCard = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the card removed") {
                assertTrue(deck.cards.contains(drawnCard))
                assertTrue(!newDeck.cards.contains(drawnCard))
            }
        }

        on("drawing multiple cards") {
            val drawOutcome = deck.drawCards(3)
            val drawnCards = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the cards removed") {
                assertTrue(deck.cards.containsAll(drawnCards))
                drawnCards.forEach { c -> assertFalse(newDeck.cards.contains(c)) }
            }
        }
    }
})