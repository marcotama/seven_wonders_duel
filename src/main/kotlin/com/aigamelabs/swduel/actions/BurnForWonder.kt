package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class BurnForWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {

        // Remove card from appropriate deck
        val newCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val wondersToBuild = playerCity.unbuiltWonders
        val affordableWonders = wondersToBuild.filter { w -> playerCity.canBuild(w, opponentCity) != null }
        val chooseWonderToBuildActions = affordableWonders.map { c -> ChooseWonderToBuild(playerTurn, c) }
        val newDecisionQueue = gameState.decisionQueue
                .enqueue(Decision(playerTurn, Vector.ofAll(chooseWonderToBuildActions), false))
                .enqueue(DecisionFactory.makeTurnDecision(playerTurn.opponent(), gameState, true))

        return gameState.update(cardStructure_ = newCardStructure, decisionQueue_ = newDecisionQueue)
    }
}