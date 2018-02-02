package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BurnOpponentCard(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        // Remove card from opponents city
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val updatedOpponentCity = opponentCity.update(buildings_ = opponentCity.buildings.remove(card))
        val updatedPlayerCities = gameState.playerCities.put(playerTurn.opponent(), updatedOpponentCity)

        // Add burned card to discard deck
        val updatedBurnedDeck = gameState.burnedCards.add(card)

        return gameState.update(playerCities_ = updatedPlayerCities, burnedDeck_ = updatedBurnedDeck)
    }

    override fun toString(): String {
        return "Burn opponent card ${card.name}"
    }
}