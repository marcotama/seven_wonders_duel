package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn

class Build(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        // Draw card from appropriate deck
        val newActiveDeck = gameState.getActiveDeck().removeCard(card)
        val newDecks = gameState.decks.put(gameState.activeDeck, newActiveDeck)

        // Add card to appropriate player city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val newPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card))
        val newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)

        // Add decision for next player
        val newDecision = DecisionFactory.makeMainTurnDecision(playerTurn.other())
        val newDecisionsQueue = gameState.decisionQueue.enqueue(newDecision)

        return gameState.update(decks_ = newDecks, playerCities_ = newPlayerCities, decisionQueue_ = newDecisionsQueue)
    }
}