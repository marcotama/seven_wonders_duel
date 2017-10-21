package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.Random

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : Random?) : GameState {

        // Add building to city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val updatedPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card))

        // Remove building from burned deck
        val newBurnedDeck = gameState.burnedDeck.removeCard(card)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)

        return gameState.update(playerCities_ = updatedPlayerCities, burnedDeck_ = newBurnedDeck)

    }
}