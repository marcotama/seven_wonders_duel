package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class ChooseProgressToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {
        val playerCity =  gameState.getPlayerCity(playerTurn)
        val updatedProgressTokens = playerCity.progressTokens.add(card)
        val updatedPlayerCity = playerCity.update(progressTokens_ = updatedProgressTokens)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn,updatedPlayerCity)

        // Remove card from the science deck
        val updatedActiveScienceDeck = gameState.availableProgressTokens.removeCard(card)

        return gameState.update(playerCities_ = updatedPlayerCities, activeScienceDeck_ = updatedActiveScienceDeck)
                .checkScienceSupremacy(playerTurn)
    }

    override fun toString(): String {
        return "Choose progress token ${card.name}"
    }
}