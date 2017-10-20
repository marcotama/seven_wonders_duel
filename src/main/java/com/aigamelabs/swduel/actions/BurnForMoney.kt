package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn

class BurnForMoney(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        //TODO remove from the graph

        //Add coins to player
        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player does not have a city") }

        val numberOfCoinsToAdd = playerCity.buildings.filter { c -> c.color == CardColor.GOLD }.length() + 2

        val updatedPlayerCity = playerCity.update(coins_ = numberOfCoinsToAdd)

        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)


        //Add card to discard deck

        val newDiscardDeck = gameState.decks.get(GameDeck.BURNED)
                .getOrElseThrow { -> Exception("The players opponent does not have a city")}.add(card)

        val newGameDecks = gameState.decks.put(GameDeck.BURNED, newDiscardDeck)


        return gameState.update(playerCities_ = updatedPlayerCities, decks_ = newGameDecks ) // TODO
    }
}