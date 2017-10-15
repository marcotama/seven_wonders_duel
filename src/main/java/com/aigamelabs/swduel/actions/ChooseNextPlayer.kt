package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseNextPlayer(playerTurn: PlayerTurn, val nextTurn : PlayerTurn) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        return GameStateFactory.createNewGameState() // TODO
    }
}