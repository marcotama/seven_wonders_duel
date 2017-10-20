package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn

class BurnOpponentCard(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        //remove card from opponents city
        val opponentCity = gameState.playerCities.get(playerTurn.opponent())
                .getOrElseThrow { -> Exception("The players opponent does not have a city") }
        val updatedOpponentCity = opponentCity.update(buildings_ = opponentCity.buildings.remove(card))

        val updatedPlayerCities = gameState.playerCities.put(playerTurn.opponent(), updatedOpponentCity)

        // Add burned card to discard deck
        val newDiscardDeck = gameState.decks.get(GameDeck.BURNED)
                .getOrElseThrow { -> Exception("The players opponent does not have a city")}.add(card)

        val newGameDecks = gameState.decks.put(GameDeck.BURNED, newDiscardDeck)

        return gameState.update(playerCities_ = updatedPlayerCities, decks_ = newGameDecks)
    }
}