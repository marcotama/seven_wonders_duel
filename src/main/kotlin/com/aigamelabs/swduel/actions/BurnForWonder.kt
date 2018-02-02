package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector
import java.util.logging.Logger

class BurnForWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {

        // Remove card from appropriate deck
        val updatedCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val wondersToBuild = playerCity.unbuiltWonders
        val affordableWonders = wondersToBuild.filter { playerCity.canBuild(it, opponentCity) != null }
        val chooseWonderToBuildActions = affordableWonders.map { BuildWonder(playerTurn, it) }

        val updatedGameState = gameState.update(cardStructure_ = updatedCardStructure)
        val updatedDecisionQueue = gameState.decisionQueue
                .enqueue(Decision(playerTurn, Vector.ofAll(chooseWonderToBuildActions), "BurnForWonder.process"))

        return updatedGameState.update(decisionQueue_ = updatedDecisionQueue)
    }

    override fun toString(): String {
        return "Burn ${card.name} to build wonder"
    }
}