package com.aigamelabs.swduel.actions

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        val updatedBurnedDeck = gameState.burnedCards.removeCard(card)
        val updatedGameState = gameState.update(burnedDeck_ = updatedBurnedDeck)

        return updatedGameState.buildBuilding(player, card, generator)
    }

    override fun toString(): String {
        return "Build burned card ${card.name}"
    }
}