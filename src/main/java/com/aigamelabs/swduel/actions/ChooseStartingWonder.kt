package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class ChooseStartingWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        // Remove wonder from for-pick deck
        var newWondersForPickDeck = gameState.wondersForPickDeck.removeCard(card)
        // Give wonder to the player
        var newGameState = gameState
        val playerCity = newGameState.getPlayerCity(playerTurn)
        val newPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(card))
        var newPlayerCities = newGameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayerCity)
        newGameState = newGameState.update(playerCities_ = newPlayerCities, wondersForPickDeck_ = newWondersForPickDeck)




        val numForPick = newGameState.wondersForPickDeck.size()
        val numUnused = newGameState.unusedWondersDeck.size()

        // First round of wonders choice (P1, P2, P2, P1)
        if (numUnused == 8) {
            if (numForPick == 3 || numForPick == 2) {
                val decision = createDecision(PlayerTurn.PLAYER_2, newGameState.wondersForPickDeck)
                return newGameState.update(decisionQueue_ = newGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P1
                val lastWonder = newGameState.wondersForPickDeck.drawCard().first
                val player1City = newGameState.getPlayerCity(PlayerTurn.PLAYER_1)
                val newPlayer1City = player1City.update(unbuiltWonders_ = player1City.unbuiltWonders.add(lastWonder))
                newPlayerCities = newGameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayer1City)
                // Draw another 4 wonders for pick
                val drawOutcome = newGameState.unusedWondersDeck.drawCards(4)
                val newUnusedWondersDeck = drawOutcome.second
                newWondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)

                val decision = createDecision(PlayerTurn.PLAYER_2, newGameState.wondersForPickDeck)
                return newGameState.update(unusedWondersDeck_ = newUnusedWondersDeck,
                        wondersForPickDeck_ = newWondersForPickDeck, playerCities_ = newPlayerCities,
                        decisionQueue_ = newGameState.decisionQueue.enqueue(decision))
            }
            else {
                throw Exception("This should not happen")
            }

        // Second round of wonders choice (P2, P1, P1, P2)
        } else if (numUnused == 4) {
            if (numForPick == 3 || numForPick == 2) {
                val decision = createDecision(PlayerTurn.PLAYER_1, newGameState.wondersForPickDeck)
                return newGameState.update(decisionQueue_ = newGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P2
                val lastWonder = newGameState.wondersForPickDeck.drawCard().first
                val player2City = newGameState.getPlayerCity(PlayerTurn.PLAYER_1)
                val newPlayer2City = player2City.update(unbuiltWonders_ = player2City.unbuiltWonders.add(lastWonder))
                newPlayerCities = newGameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayer2City)
                // Update game phase
                newWondersForPickDeck = Deck("Wonders for pick")
                return newGameState.update(wondersForPickDeck_ = newWondersForPickDeck, playerCities_ = newPlayerCities)
                        .switchToNextAge()
            }
            else {
                throw Exception("This should not happen")
            }
        }
        else {
            throw Exception("This should not happen")
        }


    }

    private fun createDecision(playerTurn: PlayerTurn, wondersForPickDeck : Deck) : Decision {
        // Create decision
        val actions : Vector<Action> = wondersForPickDeck.cards
                .map { card -> ChooseStartingWonder(PlayerTurn.PLAYER_1, card) }
        return Decision(playerTurn, actions, false)
    }
}