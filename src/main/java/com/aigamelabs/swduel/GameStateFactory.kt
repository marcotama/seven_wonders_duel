package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.actions.ChooseStartingWonder
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
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

        // Initialize wonder decks
        val drawOutcome = DeckFactory.createWondersDeck().drawCards(4)
        val wondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)
        val unusedWondersDeck = drawOutcome.second

        // Initialize burned cards deck
        val burnedDeck = Deck("Burned")

        // Create decision
        val actions : Vector<Action> = wondersForPickDeck.cards
                .map { card -> ChooseStartingWonder(PlayerTurn.PLAYER_1, card) }
        val decision = Decision(PlayerTurn.PLAYER_1, actions, false)


        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        Collections.shuffle(allTokens)
        val progressTokens = HashSet.ofAll(allTokens.subList(0, 5))

        // Set cities
        val playerCities = HashMap.of<PlayerTurn, PlayerCity>(
                PlayerTurn.PLAYER_1, PlayerCity(p1Name),
                PlayerTurn.PLAYER_2, PlayerCity(p2Name)
        )

        return GameState(activeScienceDeck, unusedScienceDeck, wondersForPickDeck, unusedWondersDeck,
                burnedDeck, null, MilitaryBoard(), playerCities, Queue.of(decision), progressTokens,
                GamePhase.WONDERS_SELECTION, PlayerTurn.PLAYER_1)
    }
}