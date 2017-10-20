package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.*

class ChooseProgressToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        val playerCity =  gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }

        val newScienceTokens = playerCity.scienceTokens.add(card)


        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)

        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)

        //remove card from the science deck

        val gameDecks = gameState.decks
                .put(GameDeck.ACTIVE_SCIENCE_TOKENS,
                        gameState.decks.get(GameDeck.ACTIVE_SCIENCE_TOKENS).
                                getOrElseThrow { -> Exception("The player  does not have a city") }.removeCard(card))


        val distinctScienceSymbols = if (card.enhancement == Enhancement.LAW)
            playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size() + 1
        else
            playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size()

        return when {
            distinctScienceSymbols >= 6 -> gameState.update(playerCities_ = newPlayerCities, decks_ = gameDecks, gamePhase_ = GamePhase.SCIENCE_SUPREMACY)
            else -> gameState.update(playerCities_ = newPlayerCities, decks_ = gameDecks)
        }
    }
}