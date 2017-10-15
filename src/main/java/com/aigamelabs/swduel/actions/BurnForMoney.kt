package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory

class BurnForMoney(val card : Card) : Action() {
    override fun process(gameState: GameState) : GameState {

        //Draw card from appropriate deck/remove from graph

        //Add card to the burned deck

        //Add money to players account

        //Change the players turn

        //Add change player turn to process to query vector

        return GameStateFactory.createNewGameState() // TODO
    }
}