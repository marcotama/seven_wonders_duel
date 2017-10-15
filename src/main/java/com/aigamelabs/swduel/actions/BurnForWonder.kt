package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory

class BurnForWonder(val card : Card) : Action() {
    override fun process(gameState: GameState) : GameState {

        //Draw card from appropriate deck/remove from graph

        //Add Choose wonder to build action

        return GameStateFactory.createNewGameState() // TODO
    }
}