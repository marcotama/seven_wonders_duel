package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {

        // Add building to city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val updatedPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card))

        // Remove building from burned deck
        val newBurnedDeck = gameState.burnedDeck.removeCard(card)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)

        val newGameState = gameState.update(playerCities_ = updatedPlayerCities, burnedDeck_ = newBurnedDeck,
                nextPlayer_ = gameState.nextPlayer.opponent()).updateBoard(generator)

        return when {
            card.color == CardColor.GREEN -> newGameState.checkScienceSupremacy(playerTurn)
            card.color == CardColor.RED -> newGameState.checkMilitarySupremacy()
            else -> newGameState
        }

    }

    override fun toString(): String {
        return "Build burned card ${card.name}"
    }
}