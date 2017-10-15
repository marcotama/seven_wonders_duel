package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import java.util.Collections

object GameStateFactory {

    fun createNewGameState() : GameState {
        return createNewGameState("P1", "P2")
    }

    fun createNewGameState(p1Name : String, p2Name : String) : GameState {

        // Initialize decks
        var firstAgeDeck = DeckFactory.createFirstAgeDeck()
        val secondAgeDeck = DeckFactory.createSecondAgeDeck()
        val thirdAgeDeck = DeckFactory.createThirdAgeDeck()
        val wondersDeck = DeckFactory.createWondersDeck()
        val burnedCardsDeck = Deck("Burned")

        // Setup graph for first age
        val graphCreationOutcome = GraphFactory.makeFirstAgeGraph(firstAgeDeck)
        val currentGraph = graphCreationOutcome.first
        firstAgeDeck = graphCreationOutcome.second

        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        Collections.shuffle(allTokens)
        val progressTokens = HashSet.ofAll(allTokens.subList(0, 5))

        // Set cities
        var player1City = PlayerCity(p1Name)
        var player2City = PlayerCity(p2Name)
        player1City = player1City.setOpponentCity(player2City)
        player2City = player2City.setOpponentCity(player1City)

        return GameState(firstAgeDeck, secondAgeDeck, thirdAgeDeck, wondersDeck, burnedCardsDeck, currentGraph,
                progressTokens, MilitaryBoard(), player1City, player2City)
    }
}