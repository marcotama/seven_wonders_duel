package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        //add building to city

        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player does not have a city") }
        val updatedPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card))

        //remove building from burned deck
        val gameDecks = gameState.decks
                .put(GameDeck.BURNED,
                        gameState.decks.get(GameDeck.BURNED).
                                getOrElseThrow { -> Exception("The player  does not have a city") }.removeCard(card))


        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)





        return gameState.update(playerCities_ = updatedPlayerCities, decks_ = gameDecks)

    }
}