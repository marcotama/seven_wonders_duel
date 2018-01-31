package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.actions.ChooseStartingWonder
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector

object GameStateFactory {

    fun createNewGameState(generator : RandomWithTracker? = null) : GameState {
        return createNewGameState("P1", "P2", generator)
    }

    fun createNewGameState(p1Name : String, p2Name : String, generator : RandomWithTracker?) : GameState {

        // Initialise the 2 Science token decks
        val scienceTokensDraw = DeckFactory.createProgressTokensDeck().drawCards(5, generator)
        val activeScienceDeck = Deck("Active Science Tokens", scienceTokensDraw.first)
        val unusedScienceDeck = scienceTokensDraw.second.update("Unused Science Tokens")

        // Initialize wonder decks
        val drawOutcome = DeckFactory.createWondersDeck().drawCards(4, generator)
        val wondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)
        val unusedWondersDeck = drawOutcome.second

        // Initialize burned cards deck
        val burnedDeck = Deck("Burned")

        // Create decision
        val actions : Vector<Action> = wondersForPickDeck.cards
                .map { card -> ChooseStartingWonder(PlayerTurn.PLAYER_1, card) }
        val decision = Decision(PlayerTurn.PLAYER_1, actions, "GameStateFactory.createNewGameState")


        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        allTokens.shuffle()

        // Set cities
        val playerCities = HashMap.of<PlayerTurn, PlayerCity>(
                PlayerTurn.PLAYER_1, PlayerCity(p1Name),
                PlayerTurn.PLAYER_2, PlayerCity(p2Name)
        )

        return GameState(activeScienceDeck, unusedScienceDeck, wondersForPickDeck, unusedWondersDeck,
                burnedDeck, null, MilitaryBoard(), playerCities, Queue.of(decision),
                GamePhase.WONDERS_SELECTION, PlayerTurn.PLAYER_1)
    }
}