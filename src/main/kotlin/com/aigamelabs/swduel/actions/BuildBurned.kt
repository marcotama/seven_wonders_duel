package com.aigamelabs.swduel.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.game.PlayerTurn
import java.util.logging.Logger

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {

        val updatedBurnedDeck = gameState.burnedCards.removeCard(card)
        val updatedGameState = gameState.update(burnedDeck_ = updatedBurnedDeck)

        return updatedGameState.buildBuilding(player, card, generator, logger, true)
    }

    override fun toString(): String {
        return "Build burned card ${card.name}"
    }
}