package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector
import java.util.logging.Logger

class ChooseStartingWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?) : GameState {
        // Remove wonder from for-pick deck
        var updatedWondersForPickDeck = gameState.wondersForPick.removeCard(card)
        // Give wonder to the player
        var updatedGameState = gameState
        val playerCity = updatedGameState.getPlayerCity(playerTurn)
        val updatedPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(card))
        var updatedPlayerCities = updatedGameState.playerCities.put(playerTurn, updatedPlayerCity)
        updatedGameState = updatedGameState.update(playerCities_ = updatedPlayerCities, wondersForPickDeck_ = updatedWondersForPickDeck)

        val numForPick = updatedGameState.wondersForPick.size()
        val numUnused = updatedGameState.discardedWonders.size()

        // First round of wonders choice (P1, P2, P2, P1)
        if (numUnused == 8) {
            if (numForPick == 3 || numForPick == 2) {
                val decision = createDecision(PlayerTurn.PLAYER_2, updatedGameState.wondersForPick)
                return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P1
                val lastWonder = updatedGameState.wondersForPick.drawCard(generator).first
                val player1City = updatedGameState.getPlayerCity(PlayerTurn.PLAYER_1)
                val updatedPlayer1City = player1City.update(unbuiltWonders_ = player1City.unbuiltWonders.add(lastWonder))
                updatedPlayerCities = updatedGameState.playerCities.put(PlayerTurn.PLAYER_1, updatedPlayer1City)
                // Draw another 4 wonders for pick
                val drawOutcome = updatedGameState.discardedWonders.drawCards(4, generator)
                val updatedUnusedWondersDeck = drawOutcome.second
                updatedWondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)

                val decision = createDecision(PlayerTurn.PLAYER_2, updatedWondersForPickDeck)
                return updatedGameState.update(unusedWondersDeck_ = updatedUnusedWondersDeck,
                        wondersForPickDeck_ = updatedWondersForPickDeck, playerCities_ = updatedPlayerCities,
                        decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
            }
            else {
                throw Exception("This should not happen")
            }

        // Second round of wonders choice (P2, P1, P1, P2)
        } else if (numUnused == 4) {
            if (numForPick == 3 || numForPick == 2) {
                val decision = createDecision(PlayerTurn.PLAYER_1, updatedGameState.wondersForPick)
                return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P2
                val lastWonder = updatedGameState.wondersForPick.drawCard(generator).first
                val player2City = updatedGameState.getPlayerCity(PlayerTurn.PLAYER_2)
                val updatedPlayer2City = player2City.update(unbuiltWonders_ = player2City.unbuiltWonders.add(lastWonder))
                updatedPlayerCities = updatedGameState.playerCities.put(PlayerTurn.PLAYER_2, updatedPlayer2City)
                // Update game phase
                updatedWondersForPickDeck = Deck("Wonders for pick")
                return updatedGameState.update(wondersForPickDeck_ = updatedWondersForPickDeck, playerCities_ = updatedPlayerCities)
                        .updateBoard(generator)
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
        val options : Vector<Action> = wondersForPickDeck.cards
                .map { ChooseStartingWonder(playerTurn, it) }
        return Decision(playerTurn, options, "ChooseStartingWonder.createDecision")
    }

    override fun toString(): String {
        return "Choose ${card.name} as starting wonder"
    }
}