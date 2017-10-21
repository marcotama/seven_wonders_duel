package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GamePhase
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

        // Initialise the 2 Science token decks
        val scienceTokensDraw = DeckFactory.createScienceTokenDeck().drawCards(5)
        val activeScienceDeck = Deck("Active Science Tokens", scienceTokensDraw.first)
        val unusedScienceDeck = scienceTokensDraw.second.update("Unused Science Tokens")

        // Initialize other decks
        val wondersForPickDeck = Deck("Wonders for pick")
        val unusedWondersDeck = DeckFactory.createWondersDeck()
        val burnedDeck = Deck("Burned")


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

        val gameState = GameState(activeScienceDeck, unusedScienceDeck, wondersForPickDeck, unusedWondersDeck,
                burnedDeck, null, progressTokens, MilitaryBoard(), playerCities, Queue.empty(),
                GamePhase.WONDERS_SELECTION)

        val newDecisionQueue = gameState.decisionQueue
                .enqueue(DecisionFactory.makeTurnDecision(PlayerTurn.PLAYER_1, gameState, true))

        return gameState.update(decisionQueue_ = newDecisionQueue)
    }
}