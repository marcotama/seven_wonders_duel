package com.aigamelabs.swduel.actions

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        val newBurnedDeck = gameState.burnedCards.removeCard(card)
        val newGameState = gameState.update(burnedDeck_ = newBurnedDeck)

        return newGameState.buildBuilding(playerTurn, card, generator)
    }

    override fun toString(): String {
        return "Build burned card ${card.name}"
    }
}