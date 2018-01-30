package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.logging.Logger

class ChooseUnusedProgressToken(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {
        val playerCity =  gameState.getPlayerCity(playerTurn)
        val newScienceTokens = playerCity.progressTokens.add(card)
        val newPlayerCity = playerCity.update(scienceTokens_ = newScienceTokens)
        val newPlayerCities = gameState.playerCities.put(playerTurn,newPlayerCity)
        return gameState.update(playerCities_ = newPlayerCities)
                .checkScienceSupremacy(playerTurn, logger)
    }

    override fun toString(): String {
        return "Choose unused progress token ${card.name}"
    }
}