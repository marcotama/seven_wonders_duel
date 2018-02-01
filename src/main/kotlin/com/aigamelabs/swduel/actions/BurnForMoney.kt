package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger


class BurnForMoney(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        // Remove card from appropriate deck
        val updatedCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        //Add coins to player
        val playerCity = gameState.getPlayerCity(playerTurn)
        val numberOfCoinsToAdd = playerCity.buildings.filter { it.color == CardColor.GOLD }.length() + 2
        val updatedPlayerCity = playerCity.update(coins_ = playerCity.coins + numberOfCoinsToAdd)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)


        //Add card to discard deck
        val newBurnedDeck = gameState.burnedCards.add(card)

        return gameState.update(cardStructure_ = updatedCardStructure, playerCities_ = updatedPlayerCities,
                burnedDeck_ = newBurnedDeck, nextPlayer_ = playerTurn.opponent()).updateBoard(generator, logger)
    }

    override fun toString(): String {
        return "Burn ${card.name} for coins"
    }
}