package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import java.util.*

class GameState (
        var firstAgeDeck : Deck,
        var secondAgeDeck : Deck,
        var thirdAgeDeck : Deck,
        var wondersDeck : Deck,
        var burnedCardsDeck : Deck,
        var currentGraph : Graph<Card>?,
        var progressTokens : HashSet<ProgressToken>,
        var militaryBoard: MilitaryBoard
) {

    constructor() : this(
            DeckFactory.createFirstAgeDeck(),
            DeckFactory.createSecondAgeDeck(),
            DeckFactory.createThirdAgeDeck(),
            DeckFactory.createWondersDeck(),
            Deck("Burned"),
            null,
            HashSet.empty(),
            MilitaryBoard()
    ) {
        // Setup graph for first age
        val graphCreationOutcome = GraphFactory.makeFirstAgeGraph(firstAgeDeck)
        currentGraph = graphCreationOutcome.first
        firstAgeDeck = graphCreationOutcome.second
        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        Collections.shuffle(allTokens)
        progressTokens.addAll(allTokens.subList(0, 5))
    }
}