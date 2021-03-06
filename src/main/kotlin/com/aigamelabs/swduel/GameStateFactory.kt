package com.aigamelabs.swduel

import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.swduel.actions.*
import com.aigamelabs.swduel.enums.ProgressToken
import com.aigamelabs.utils.Deck
import io.vavr.collection.Queue
import io.vavr.collection.Vector

object GameStateFactory {

    fun createNewGameState(generator : RandomWithTracker) : GameState {
        return createNewGameState("P1", "P2", generator)
    }

    fun createNewGameState(p1Name : String, p2Name : String, generator : RandomWithTracker) : GameState {

        // Initialise the 2 Science token decks
        val allProgressTokensDeck = DeckFactory.createProgressTokensDeck()
        val (unusedProgressTokens, activeProgressTokensDeck) = allProgressTokensDeck.drawCards(5, generator)
        val unusedProgressTokensDeck = Deck("Unused Progress Tokens", Vector.of(Pair(unusedProgressTokens, 0)))

        // Initialize wonder decks
        val allWondersDeck = DeckFactory.createWondersDeck()
        val (wondersForPick, unusedWondersDeck) = allWondersDeck.drawCards(4, generator)
        val wondersForPickDeck = Deck("Wonders For Pick", Vector.of(Pair(wondersForPick, 0)))

        // Initialize burned cards deck
        val burnedDeck = Deck<Card>("Burned")

        // Create decision
        val actions : Vector<Action<GameState>> = wondersForPickDeck.cards
                .map { ChooseStartingWonder(PlayerTurn.PLAYER_1, it) }
        val decision = Decision(PlayerTurn.PLAYER_1, actions)


        // Setup progress tokens
        val allTokens = ProgressToken.values().toMutableList()
        allTokens.shuffle()

        return GameState(activeProgressTokensDeck, unusedProgressTokensDeck, wondersForPickDeck, unusedWondersDeck,
                burnedDeck, null, MilitaryBoard(), PlayerCity(p1Name), PlayerCity(p2Name),
                Queue.of(decision), GamePhase.WONDERS_SELECTION, PlayerTurn.PLAYER_1)
    }
}