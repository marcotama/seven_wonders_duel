package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.DecisionFactory
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.RandomWithTracker
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.PlayerTurn


class BurnForMoney(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {

        // Remove card from appropriate deck
        val newCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        //Add coins to player
        val playerCity = gameState.getPlayerCity(playerTurn)
        val numberOfCoinsToAdd = playerCity.buildings.filter { c -> c.color == CardColor.GOLD }.length() + 2
        val updatedPlayerCity = playerCity.update(coins_ = numberOfCoinsToAdd)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)


        //Add card to discard deck
        val newBurnedDeck = gameState.burnedDeck.add(card)

        val newDecisionQueue = gameState.decisionQueue
                .enqueue(DecisionFactory.makeTurnDecision(playerTurn.opponent(), gameState, true))


        return gameState.update(cardStructure_ = newCardStructure, playerCities_ = updatedPlayerCities,
                burnedDeck_ = newBurnedDeck, decisionQueue_ = newDecisionQueue)
    }
}