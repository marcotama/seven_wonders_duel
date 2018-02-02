package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.Vector
import java.util.concurrent.ThreadLocalRandom
import javax.json.stream.JsonGenerator

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

    fun getByName(name: String): Card {
        val matches = cards.filter { it.name == name }
        if (matches.size() == 1)
            return matches[0]
        else
            throw Exception("Card $name not found in ${toString()}")
    }

    fun removeCard(card : Card) : Deck {
        val cardIdx = cards.indexOf(card)
        if (cardIdx == -1)
            throw Exception("Card \"${card.name}\" not present in deck \"$name\" (contents: ${toString()})")
        else
            return update(cards_ = cards.removeAt(cardIdx))
    }

    fun drawCard(generator : RandomWithTracker? = null) : Pair<Card, Deck> {
        return if (cards.size() > 0) {
            val cardIdx = generator?.nextInt(cards.size())
                    ?: ThreadLocalRandom.current().nextInt(0, cards.size())
            val drawnCard = cards[cardIdx]
            val newDeck = update(cards_ = cards.removeAt(cardIdx))
            Pair(drawnCard, newDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun drawCards(n : Int = 1, generator : RandomWithTracker? = null) : Pair<Vector<Card>, Deck> {
        return if (cards.size() >= n) {
            val indices = (0 until n).toMutableList()

            if (generator == null)
                indices.shuffle()
            else
                generator.shuffle(indices)

            val drawnCards = indices.subList(0, n).map { cards[indices[it]] }
            val newDeck = update(cards_ = cards.filter { !drawnCards.contains(it) } )
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

    /**
     * Dumps the object content in JSON.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartArray()
        else generator.writeStartArray(name)

        cards.forEach { generator.write(it.name) }

        generator.writeEnd()
    }

    override fun toString(): String {
        return if (cards.isEmpty)
            "<Empty>"
        else {
            val tmp = cards.map { it.name }.foldLeft("", { acc, el -> "$acc$el, " })
            tmp.substring(0, tmp.length - 2)
        }
    }
}