package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Decision
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class BurnForWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        //TODO remove card from graph/deck

        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player does not have a city") }

        val wondersToBuild = playerCity.wondersDeck

        val affordableWonders = wondersToBuild.cards.filter { w -> playerCity.canBuild(w) != null }

        val chooseWondertoBuildActions = affordableWonders.map { c -> ChooseWonderToBuild(playerTurn, c) }

        val decisionQueue = gameState.decisionQueue
                .insert(0, Decision(playerTurn, Vector.ofAll(chooseWondertoBuildActions), false))

        return gameState.update(decisionQueue_ = decisionQueue)
    }
}