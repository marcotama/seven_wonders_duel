package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.MilitaryBoard
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn

class Build(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        // Draw card from appropriate deck
        val newActiveDeck = gameState.getActiveDeck().removeCard(card)
        val newDecks = gameState.decks.put(gameState.activeDeck, newActiveDeck)

        // Add card to appropriate player city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val newPlayer1City = playerCity.update(buildings_ = playerCity.buildings.add(card))
        var newPlayerCities = gameState.playerCities.put(playerTurn, newPlayer1City)

        // Handle military cards
        val newMilitaryBoard : MilitaryBoard
        val newGamePhase : GamePhase
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
            // If a player achieved military supremacy, change game phase
            newGamePhase = if (newMilitaryBoard.isMilitarySupremacy()) GamePhase.MILITARY_SUPREMACY else GamePhase.IN_GAME
        }
        else {
            // Unaltered
            newMilitaryBoard = gameState.militaryBoard
            newGamePhase = GamePhase.IN_GAME
        }

        // TODO handle science

        return gameState.update(decks_ = newDecks, playerCities_ = newPlayerCities,
                militaryBoard_ = newMilitaryBoard, gamePhase_ = newGamePhase)
    }
}