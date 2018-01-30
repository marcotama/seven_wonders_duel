package com.aigamelabs.swduel.actions

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BuildBuilding(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        val newCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)
        val newGameState = gameState.update(cardStructure_ = newCardStructure)

        return newGameState.buildBuilding(playerTurn, card, generator)
    }

    override fun toString(): String {
        return "Build ${card.name}"
    }
}