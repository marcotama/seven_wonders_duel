package com.aigamelabs.swduel

import io.vavr.collection.Stream

class GameState (
        var firstAgeDeck : Deck,
        var secondAgeDeck : Deck,
        var thirdAgeDeck : Deck,
        var wondersDeck : Deck,
        var burnedCardsDeck : Deck,
        var currentGraph : Graph<Card>?
) {

    constructor() : this(
            DeckFactory.createFirstAgeDeck(),
            DeckFactory.createSecondAgeDeck(),
            DeckFactory.createThirdAgeDeck(),
            DeckFactory.createWondersDeck(),
            Deck("Burned"),
            null
    ) {
        val graphCreationOutcome = GraphFactory.makeFirstAgeGraph(firstAgeDeck)
        currentGraph = graphCreationOutcome.first
        firstAgeDeck = graphCreationOutcome.second
    }
}