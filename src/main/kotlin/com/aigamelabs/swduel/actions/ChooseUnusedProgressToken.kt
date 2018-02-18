package com.aigamelabs.swduel.actions

import com.aigamelabs.game.Action
import com.aigamelabs.swduel.Card
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.swduel.GameState
import java.util.logging.Logger

class ChooseUnusedProgressToken(playerTurn: PlayerTurn, val card : Card) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        val playerCity =  gameState.getPlayerCity(player)
        val opponentCity =  gameState.getPlayerCity(player.opponent())
        val updatedProgressTokens = playerCity.progressTokens.add(card)
        val updatedPlayerCity = playerCity.update(progressTokens_ = updatedProgressTokens)
        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else opponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else opponentCity
        return gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City)
                .addMainTurnDecision(generator, logger)
                .checkScienceSupremacy(player)
    }

    override fun toString(): String {
        return "Choose unused progress token ${card.name}"
    }
}