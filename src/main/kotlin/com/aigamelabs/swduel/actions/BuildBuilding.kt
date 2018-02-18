package com.aigamelabs.swduel.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.game.PlayerTurn
import java.util.logging.Logger

class BuildBuilding(playerTurn: PlayerTurn, val card : Card) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {

        val updatedCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)
        val updatedGameState = gameState.update(cardStructure_ = updatedCardStructure)

        return updatedGameState.swapNextPlayer().buildBuilding(player, card, generator, logger, false)
    }

    override fun toString(): String {
        return "Build ${card.name}"
    }
}