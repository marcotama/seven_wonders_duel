package com.aigamelabs.swduel

import io.vavr.collection.Vector
import java.util.Collections
import java.util.Random
import java.util.concurrent.ThreadLocalRandom

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

    fun drawCard(generator : Random? = null) : Pair<Card, Deck> {
        return if (cards.size() > 0) {
            val cardIdx = generator?.nextInt(cards.size() + 1)
                    ?: ThreadLocalRandom.current().nextInt(0, cards.size() + 1)
            val drawnCard = cards[cardIdx]
            val newDeck = update(cards_ = cards.removeAt(cardIdx))
            Pair(drawnCard, newDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun drawCards(n : Int = 1, generator : Random? = null) : Pair<Vector<Card>, Deck> {
        return if (cards.size() >= n) {
            val indices = (0..n).toMutableList()

            if (generator == null)
                Collections.shuffle(indices)
            else
                Collections.shuffle(indices, generator)

            val drawnCards = indices.subList(0, n).map { i -> cards[indices[i]] }
            val newDeck = update(cards_ = cards.filter { card -> !drawnCards.contains(card) } )
            Pair(Vector.ofAll(drawnCards), newDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun add(first: Card) : Deck {
        return Deck(name, cards.append(first))
    }

    fun addAll(newCards : Vector<Card>) : Deck {
        return Deck(name, cards.appendAll(newCards))
    }

    fun merge(deck : Deck) : Deck {
        return addAll(deck.cards)
    }

    fun size() : Int {
        return cards.size()
    }
}