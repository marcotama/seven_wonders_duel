package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.ProgressToken

class ChooseProgressToken(val token : ProgressToken) : Action() {
    override fun process(gameState: GameState) : GameState {
        return GameStateFactory.createNewGameState() // TODO
    }
}