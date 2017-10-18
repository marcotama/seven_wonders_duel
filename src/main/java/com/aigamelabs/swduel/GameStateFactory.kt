package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import java.util.Collections

object GameStateFactory {

    fun createNewGameState() : GameState {
        return createNewGameState("P1", "P2")
    }

    fun createNewGameState(p1Name : String, p2Name : String) : GameState {

        // Initialize 2 groups of 4 wonders each
        val draw4outcome1 = DeckFactory.createWondersDeck().drawCards(4)!!
        val draw4outcome2 = draw4outcome1.second.drawCards(4)!!


        // Initialise the 2 Science token decks
        val scienceTokensDraw = DeckFactory.createScienceTokenDeck().drawCards(5)!!
        val activeScienceDeck = Deck("Active Science Tokens", scienceTokensDraw.first)
        val unusedScienceDeck = scienceTokensDraw.second.update("Unused Science Tokens")

        // Setup graph for first age
        val graphCreationOutcome = GraphFactory.makeFirstAgeGraph(DeckFactory.createFirstAgeDeck())
        val currentGraph = graphCreationOutcome.first
        val firstAgeDeck = graphCreationOutcome.second

        // Initialize decks
        val decks : HashMap<GameDeck, Deck> = HashMap.of(
                GameDeck.FIRST_AGE, firstAgeDeck,
                GameDeck.SECOND_AGE, DeckFactory.createSecondAgeDeck(),
                GameDeck.THIRD_AGE, DeckFactory.createThirdAgeDeck(),
                GameDeck.WONDERS_1, Deck("Wonders of P1", draw4outcome1.first),
                GameDeck.WONDERS_2, Deck("Wonders of P2", draw4outcome2.first),
                GameDeck.BURNED, Deck("Burned"),
                GameDeck.ACTIVE_SCIENCE_TOKENS, activeScienceDeck,
                GameDeck.UNUSED_SCIENCE_TOKENS, unusedScienceDeck
        )

        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        Collections.shuffle(allTokens)
        val progressTokens = HashSet.ofAll(allTokens.subList(0, 5))

        // Set cities
        var player1City = PlayerCity(p1Name)
        var player2City = PlayerCity(p2Name)
        player1City = player1City.setOpponentCity(player2City)
        player2City = player2City.setOpponentCity(player1City)
        val playerCities = HashMap.of<PlayerTurn, PlayerCity>(
                PlayerTurn.PLAYER_1, player1City,
                PlayerTurn.PLAYER_2, player2City
        )

        val decisionQueue = Queue.empty<Decision>().enqueue(DecisionFactory.makeTurnDecision(PlayerTurn.PLAYER_1))

        return GameState(GameDeck.FIRST_AGE, decks, currentGraph,
                progressTokens, MilitaryBoard(), playerCities, decisionQueue)
    }
}