package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.PlayerTurn

class ChooseWonderToBuild(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {

    override fun process(gameState: GameState) : GameState {

        //remove wonderDeck add to wonders

        val playerCity = gameState.playerCities[playerTurn]


        val newGameState = processWonders()
//        val newGameState = gameState.buildWonder(playerTurn, card)





        return GameStateFactory.createNewGameState() // TODO
    }

    fun processWonders() {

        when (card.name) {
            "name " -> {

            }
            ":other name" -> {

            }
        }

    }

}