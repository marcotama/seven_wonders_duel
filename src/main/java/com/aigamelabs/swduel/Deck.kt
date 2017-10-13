package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.DeckName
import com.aigamelabs.swduel.Card
import io.vavr.collection.List

open class Deck(val deckName: DeckName) {
    var cards: List<Card> = List.empty()
}