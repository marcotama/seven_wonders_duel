package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class Build(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?) : GameState {

        // Remove card from appropriate deck
        val newCardStructure = gameState.cardStructure!!.pickUpCard(card, generator)

        // Add card to appropriate player city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val coins = if (card.coinsProduced > 0)
            card.coinsProduced * gameState.getMultiplier(card.coinsProducedFormula, card.referenceCity, playerCity, opponentCity)
        else
            0
        val newPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card), coins_ = playerCity.coins + coins)
        var newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)

        // Handle military cards
        val newMilitaryBoard: MilitaryBoard
        if (card.color == CardColor.RED) {
            val additionOutcome = gameState.militaryBoard.addMilitaryPointsTo(card.militaryPoints, playerTurn)
            newMilitaryBoard = additionOutcome.second
            // Apply penalty to opponent city, if any
            val opponentPenalty = additionOutcome.first
            if (opponentPenalty > 0) {
                val newOpponentCity = opponentCity.update(coins_ = opponentCity.coins - opponentPenalty)
                newPlayerCities = gameState.playerCities.put(playerTurn.opponent(), newOpponentCity)
            }
        } else {
            // Unchanged
            newMilitaryBoard = gameState.militaryBoard
        }

        var newDecisionQueue = gameState.decisionQueue
        if (card.color == CardColor.GREEN) {
            if (playerCity.buildings.filter { c -> c.scienceSymbol == card.scienceSymbol}.size() == 2) {
                val actions : Vector<Action> = gameState.activeScienceDeck.cards
                        .map { c -> ChooseProgressToken(playerTurn, c) }
                val decision = Decision(playerTurn, actions, false)
                newDecisionQueue = newDecisionQueue.enqueue(decision)
            }
        }

        newDecisionQueue = newDecisionQueue
                .enqueue(DecisionFactory.makeTurnDecision(playerTurn.opponent(), gameState, true))

        val newGameState = gameState.update(cardStructure_ = newCardStructure, playerCities_ = newPlayerCities,
                militaryBoard_ = newMilitaryBoard, decisionQueue_ = newDecisionQueue)

        return when {
            card.color == CardColor.GREEN -> newGameState.checkScienceSupremacy(playerTurn)
            card.color == CardColor.RED -> newGameState.checkMilitarySupremacy()
            else -> newGameState
        }
    }
}