package com.aigamelabs.swduel.actions

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BurnForWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {

        // Remove card from appropriate deck
        val updatedCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)
        val updatedGameState = gameState.update(cardStructure_ = updatedCardStructure)
        return updatedGameState.addSelectWonderToBuildDecision(player)
    }

    override fun toString(): String {
        return "Burn ${card.name} to build wonder"
    }
}