package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.MilitaryBoard
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.Enhancement
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn

class Build(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        // Draw card from appropriate deck
        val newCardStructure = gameState.getActiveCardStructure().pickUpCard(card)
        val newCardStructures = gameState.cardStructures.put(gameState.gamePhase, newCardStructure)

        // Add card to appropriate player city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val newPlayer1City = playerCity.update(buildings_ = playerCity.buildings.add(card))
        var newPlayerCities = gameState.playerCities.put(playerTurn, newPlayer1City)

        // Handle military cards
        val newMilitaryBoard : MilitaryBoard
        if (card.color == CardColor.RED) {
            val additionOutcome = gameState.militaryBoard.addMilitaryPointsTo(card.militaryPoints, playerTurn)
            newMilitaryBoard = additionOutcome.second
            // Apply penalty to opponent city, if any
            val opponentPenalty = additionOutcome.first
            if (opponentPenalty > 0) {
                val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
                val newPlayer2City = opponentCity.update(coins_ = opponentCity.coins - opponentPenalty)
                newPlayerCities = gameState.playerCities.put(playerTurn.opponent(), newPlayer2City)
            }
        }
        else {
            // Unchanged
            newMilitaryBoard = gameState.militaryBoard
        }

        // Count science symbols

        val distinctScienceSymbols = if (card.color == CardColor.GREEN)
            if (!newPlayer1City.scienceTokens.filter{ c-> c.enhancement == Enhancement.LAW}.isEmpty)
                playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size() + 1
            else
                playerCity.buildings.map { c -> c.scienceSymbol }.distinct().size()
        else 0


        val newGamePhase = when {
            newMilitaryBoard.isMilitarySupremacy() -> GamePhase.MILITARY_SUPREMACY
            distinctScienceSymbols >= 6 -> GamePhase.SCIENCE_SUPREMACY
            else -> gameState.gamePhase
        }

        return gameState.update(cardStructures_ = newCardStructures, playerCities_ = newPlayerCities,
                militaryBoard_ = newMilitaryBoard, gamePhase_ = newGamePhase)
    }
}