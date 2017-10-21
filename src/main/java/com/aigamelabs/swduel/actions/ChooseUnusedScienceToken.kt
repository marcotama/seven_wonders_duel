package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.Enhancement
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseUnusedScienceToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        val playerCity =  gameState.getPlayerCity(playerTurn)
        val newScienceTokens = playerCity.scienceTokens.add(card)
        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)
        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)
        val distinctScienceSymbols = if (card.enhancement == Enhancement.LAW)
                playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size() + 1
            else
                playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size()

        return when {
            distinctScienceSymbols >= 6 -> gameState.update(playerCities_ = newPlayerCities, gamePhase_ = GamePhase.SCIENCE_SUPREMACY)
            else -> gameState.update(playerCities_ = newPlayerCities)
        }
    }
}