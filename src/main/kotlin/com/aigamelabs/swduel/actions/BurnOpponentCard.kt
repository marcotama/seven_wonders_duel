package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class BurnOpponentCard(player: PlayerTurn, val card : Card) : Action(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        // Remove card from opponents city
        val playerCity = gameState.getPlayerCity(player)
        val opponentCity = gameState.getPlayerCity(player.opponent())
        val updatedOpponentCity = opponentCity.update(buildings_ = opponentCity.buildings.remove(card))

        // Add burned card to discard deck
        val updatedBurnedDeck = gameState.burnedCards.add(card)

        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) playerCity else updatedOpponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) playerCity else updatedOpponentCity
        return gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City,
                burnedDeck_ = updatedBurnedDeck)
    }

    override fun toString(): String {
        return "Burn opponent card ${card.name}"
    }
}