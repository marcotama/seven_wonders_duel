package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseProgressToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {
        val playerCity =  gameState.getPlayerCity(playerTurn)
        val newScienceTokens = playerCity.progressTokens.add(card)
        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)
        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)

        // Remove card from the science deck
        val newActiveScienceDeck = gameState.activeScienceDeck.removeCard(card)

        return gameState.update(playerCities_ = newPlayerCities, activeScienceDeck_ = newActiveScienceDeck)
                .checkScienceSupremacy(playerTurn)
    }

    override fun toString(): String {
        return "Choose progress token ${card.name}"
    }
}