package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.Enhancement
import com.aigamelabs.swduel.enums.GamePhase

class ChooseProgressToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {
        val playerCity =  gameState.getPlayerCity(playerTurn)
        val newScienceTokens = playerCity.progressTokens.add(card)
        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)
        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)

        // Remove card from the science deck

        val newActiveScienceDeck = gameState.activeScienceDeck.removeCard(card)

        val distinctScienceSymbols = if (card.enhancement == Enhancement.LAW)
            playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size() + 1
        else
            playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size()

        return when {
            distinctScienceSymbols >= 6 -> gameState.update(playerCities_ = newPlayerCities, activeScienceDeck_ = newActiveScienceDeck, gamePhase_ = GamePhase.SCIENCE_SUPREMACY)
            else -> gameState.update(playerCities_ = newPlayerCities, activeScienceDeck_ = newActiveScienceDeck)
        }
    }
}