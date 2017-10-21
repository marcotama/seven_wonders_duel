package com.aigamelabs.swduel

import io.vavr.collection.Vector
import java.util.*

data class Deck(val name: String, val cards: Vector<Card>) {

    constructor(name: String) : this(name, Vector.empty())

    fun update(
            name_ : String? = null,
            cards_ : Vector<Card>? = null
    ) : Deck {
        return Deck(
                name_ ?: name,
                cards_ ?: cards
        )
    }

    fun removeCard(card : Card) : Deck {
        return update(cards_ = cards.remove(card))
    }

    fun drawCard(generator : Random) : Pair<Card, Deck> {
        return if (cards.size() > 0) {
            val cardIdx = generator.nextInt(cards.size() + 1)
            val drawnCard = cards[cardIdx]
            val newDeck = update(cards_ = cards.removeAt(cardIdx))
            Pair(drawnCard, newDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun drawCards(n : Int = 1, generator : Random) : Pair<Vector<Card>, Deck> {
        return if (cards.size() >= n) {
            val indices = (0..n).toMutableList()
            Collections.shuffle(indices, generator)
            val drawnCards = indices.subList(0, n).map { i -> cards[indices[i]] }
            val newDeck = update(cards_ = cards.filter { card -> drawnCards.contains(card) } )
            Pair(Vector.ofAll(drawnCards), newDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun add(first: Card) : Deck {
        return Deck(name, cards.append(first))
    }

    fun addAll(deck : Deck) : Deck {
        return Deck(name, cards.appendAll(deck.cards))
    }

    fun size() : Int {
        return cards.size()
    }
}