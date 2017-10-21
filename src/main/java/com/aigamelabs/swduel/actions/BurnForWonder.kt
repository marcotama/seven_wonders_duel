package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Decision
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class BurnForWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        // Remove card from appropriate deck
        val newCardStructure = gameState.cardStructure!!.pickUpCard(card)

        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val wondersToBuild = playerCity.unbuiltWonders
        val affordableWonders = wondersToBuild.filter { w -> playerCity.canBuild(w, opponentCity) != null }
        val chooseWonderToBuildActions = affordableWonders.map { c -> ChooseWonderToBuild(playerTurn, c) }
        val decisionQueue = gameState.decisionQueue
                .insert(0, Decision(playerTurn, Vector.ofAll(chooseWonderToBuildActions), false))

        return gameState.update(cardStructure_ = newCardStructure, decisionQueue_ = decisionQueue)
    }
}