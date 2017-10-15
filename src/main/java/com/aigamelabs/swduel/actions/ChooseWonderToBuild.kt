package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory

class ChooseWonderToBuild(val card : Card) : Action() {
    override fun process(gameState: GameState) : GameState {
        return GameStateFactory.createNewGameState() // TODO
    }
}