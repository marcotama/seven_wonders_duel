package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.PlayerTurn

class BuildBurned(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        //Draw card from appropriate deck (burned deck)

        //Add card to appropriate player city

        //Change the players turn

        //Add change player turn to process to query vector

        return GameStateFactory.createNewGameState() // TODO
    }
}