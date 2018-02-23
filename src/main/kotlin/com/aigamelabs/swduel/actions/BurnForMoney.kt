package com.aigamelabs.swduel.actions

import com.aigamelabs.game.Action
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.opponent
import com.aigamelabs.utils.RandomWithTracker
import java.util.logging.Logger


class BurnForMoney(playerTurn: PlayerTurn, val card : Card) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {

        // Remove card from appropriate deck
        val updatedCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        //Add coins to player
        val playerCity = gameState.getPlayerCity(player)
        val opponentCity = gameState.getPlayerCity(player.opponent())
        val numberOfCoinsToAdd = playerCity.buildings.filter { it.color == CardColor.GOLD }.length() + 2
        val updatedPlayerCity = playerCity.update(coins_ = playerCity.coins + numberOfCoinsToAdd)


        //Add card to discard deck
        val updatedBurnedDeck = gameState.burnedCards.add(card)

        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else opponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else opponentCity
        return gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City,
                cardStructure_ = updatedCardStructure, burnedDeck_ = updatedBurnedDeck,
                nextPlayer_ = player.opponent())
                .addMainTurnDecision(generator, logger)
    }

    override fun toString(): String {
        return "Burn ${card.name} for coins"
    }
}