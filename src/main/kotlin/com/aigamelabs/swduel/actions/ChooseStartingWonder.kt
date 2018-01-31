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
        var newWondersForPickDeck = gameState.wondersForPick.removeCard(card)
        // Give wonder to the player
        var newGameState = gameState
        val playerCity = newGameState.getPlayerCity(playerTurn)
        val newPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(card))
        var newPlayerCities = newGameState.playerCities.put(playerTurn, newPlayerCity)
        newGameState = newGameState.update(playerCities_ = newPlayerCities, wondersForPickDeck_ = newWondersForPickDeck)

        val numForPick = newGameState.wondersForPick.size()
        val numUnused = newGameState.discardedWonders.size()

        // First round of wonders choice (P1, P2, P2, P1)
        if (numUnused == 8) {
            if (numForPick == 3 || numForPick == 2) {
                val decision = createDecision(PlayerTurn.PLAYER_2, newGameState.wondersForPick)
                return newGameState.update(decisionQueue_ = newGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P1
                val lastWonder = newGameState.wondersForPick.drawCard(generator).first
                val player1City = newGameState.getPlayerCity(PlayerTurn.PLAYER_1)
                val newPlayer1City = player1City.update(unbuiltWonders_ = player1City.unbuiltWonders.add(lastWonder))
                newPlayerCities = newGameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayer1City)
                // Draw another 4 wonders for pick
                val drawOutcome = newGameState.discardedWonders.drawCards(4, generator)
                val newUnusedWondersDeck = drawOutcome.second
                newWondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)

                val decision = createDecision(PlayerTurn.PLAYER_2, newWondersForPickDeck)
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
                val decision = createDecision(PlayerTurn.PLAYER_1, newGameState.wondersForPick)
                return newGameState.update(decisionQueue_ = newGameState.decisionQueue.enqueue(decision))
            } else if (numForPick == 1) {
                // Give remaining card to P2
                val lastWonder = newGameState.wondersForPick.drawCard(generator).first
                val player2City = newGameState.getPlayerCity(PlayerTurn.PLAYER_2)
                val newPlayer2City = player2City.update(unbuiltWonders_ = player2City.unbuiltWonders.add(lastWonder))
                newPlayerCities = newGameState.playerCities.put(PlayerTurn.PLAYER_2, newPlayer2City)
                // Update game phase
                newWondersForPickDeck = Deck("Wonders for pick")
                return newGameState.update(wondersForPickDeck_ = newWondersForPickDeck, playerCities_ = newPlayerCities)
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