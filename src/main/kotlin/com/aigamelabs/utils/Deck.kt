package com.aigamelabs.utils

import io.vavr.collection.Vector
import javax.json.stream.JsonGenerator

/**
 * Represents a set of cards. The set is defined by subgroups of cards. For each subgroup, a number of "discarded"
 * cards can be specified. This models game decks where some cards are discarded, without actually storing such
 * information. This prevents players from extracting such information from the object.
 */
data class Deck<T>(val name: String, private val groups: Vector<Pair<Vector<T>,Int>>) {

    val cards: Vector<T>
    get() {
        return groups.fold(Vector.empty<T>(), { acc, d -> acc.appendAll(d.first) })
    }

    private val numCards = groups.map { it.first.size() - it.second }.sum().toInt()

    constructor(name: String) : this(name, Vector.of(Pair(Vector.empty(),0)))

    fun update(
            name_ : String? = null,
            groups_: Vector<Pair<Vector<T>,Int>>? = null
    ) : Deck<T> {
        return Deck(
                name_ ?: name,
                groups_ ?: groups
        )
    }

    fun removeCard(card : T) : Deck<T> {
        groups.forEachIndexed { groupIdx, group ->
            val cardIdx = group.first.indexOf(card)
            if (cardIdx != -1) {
                return update(groups_ = groups.update(groupIdx, Pair(group.first.removeAt(cardIdx), group.second)))
            }
        }
        throw Exception("Card \"$card\" not present in deck \"$name\" (contents: ${toString()})")
    }

    fun drawCard(generator : RandomWithTracker) : Pair<T, Deck<T>> {
        return if (numCards > 0) {
            val groupIdx = generator.nextInt(groups.map { it.first.size() - it.second } )
            val group = groups[groupIdx]
            val cards = group.first
            val cardIdx = generator.nextInt(cards.size())
            val drawnCard = cards[cardIdx]
            val updatedDeck = update(groups_ = groups.update(groupIdx, Pair(group.first.removeAt(cardIdx), group.second)))
            Pair(drawnCard, updatedDeck)
        }
        else {
            throw Exception("The deck is empty")
        }
    }

    fun drawCards(n : Int = 1, generator : RandomWithTracker) : Pair<Vector<T>, Deck<T>> {
        var deck = this
        val drawnCards = (0 until n).map {
            val (card, updatedDeck) = deck.drawCard(generator)
            deck = updatedDeck
            card
        }
        return Pair(Vector.ofAll(drawnCards), deck)
    }

    fun add(card: T) : Deck<T> {
        return Deck(name, groups.update(0, Pair(groups[0].first.append(card), groups[0].second)))
    }

    fun addAll(cards: Vector<T>) : Deck<T> {
        return Deck(name, groups.update(0, Pair(groups[0].first.appendAll(cards), groups[0].second)))
    }

    fun size() : Int {
        return numCards
    }

    /**
     * Dumps the object content in JSON.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("name", name)
        generator.writeStartArray("groups") // array of groups
        groups.forEach { group ->
            generator.writeStartObject() // group
            generator.write("discarded", group.second)
            generator.writeStartArray("cards") // array of cards
            group.first.forEach {
                generator.write(it.toString())
            }
            generator.writeEnd() // array of cards
            generator.writeEnd() // group
        }
        generator.writeEnd() // array of groups

        generator.writeEnd()
    }

    override fun toString(): String {
        return if (cards.isEmpty)
            "<Empty>"
        else {
            val tmp = cards.map { it }.foldLeft("", { acc, el -> "$acc$el, " })
            tmp.substring(0, tmp.length - 2)
        }
    }
}