package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseUnusedScienceToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        val playerCity =  gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }

        val newScienceTokens = playerCity.scienceTokens.add(card)

        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)

        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)

        return gameState.update(playerCities_ = newPlayerCities)
    }
}