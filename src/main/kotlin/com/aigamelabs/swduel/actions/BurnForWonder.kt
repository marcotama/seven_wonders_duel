package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
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
        val affordableWonders = wondersToBuild.filter { playerCity.canBuild(it, opponentCity) != null }
        val chooseWonderToBuildActions = affordableWonders.map { BuildWonder(playerTurn, it) }

        val newGameState = gameState.update(cardStructure_ = newCardStructure)
        val newDecisionQueue = gameState.decisionQueue
                .enqueue(Decision(playerTurn, Vector.ofAll(chooseWonderToBuildActions), "BurnForWonder.process"))

        return newGameState.update(decisionQueue_ = newDecisionQueue)
    }

    override fun toString(): String {
        return "Burn ${card.name} to build wonder"
    }
}