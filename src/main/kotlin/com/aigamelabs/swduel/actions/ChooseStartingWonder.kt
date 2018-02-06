package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector
import java.util.logging.Logger

class ChooseStartingWonder(player: PlayerTurn, val card : Card) : Action(player) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        // Remove wonder from for-pick deck
        var updatedWondersForPickDeck = gameState.wondersForPick.removeCard(card)
        // Give wonder to the player
        var updatedGameState = gameState
        val playerCity = updatedGameState.getPlayerCity(player)
        val opponentCity = updatedGameState.getPlayerCity(player.opponent())
        val updatedPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(card))
        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else opponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else opponentCity
        updatedGameState = updatedGameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City,
                wondersForPickDeck_ = updatedWondersForPickDeck)

        val numForPick = updatedGameState.wondersForPick.size()
        val numUnused = updatedGameState.discardedWonders.size()

        // First round of wonders choice (P1, P2, P2, P1)
        when (numUnused) {
            8 -> when (numForPick) {
                3, 2 -> {
                    val decision = createDecision(PlayerTurn.PLAYER_2, updatedGameState.wondersForPick)
                    return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
                }
                1 -> {
                    val decision = createDecision(PlayerTurn.PLAYER_1, updatedGameState.wondersForPick)
                    return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
                }
                0 -> {
                    // Draw another 4 wonders for pick
                    val (wondersForPick, updatedDiscardedWondersDeck) = updatedGameState.discardedWonders.drawCards(4, generator)
                    updatedWondersForPickDeck = Deck("Unused Wonders", Vector.of(Pair(wondersForPick, 0)))

                    val decision = createDecision(PlayerTurn.PLAYER_2, updatedWondersForPickDeck)
                    return updatedGameState.update(
                            unusedWondersDeck_ = updatedDiscardedWondersDeck,
                            wondersForPickDeck_ = updatedWondersForPickDeck,
                            decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision)
                    )
                }
                else -> throw Exception("This should not happen")
            }

        // Second round of wonders choice (P2, P1, P1, P2)
            4 -> when (numForPick) {
                3, 2 -> {
                    val decision = createDecision(PlayerTurn.PLAYER_1, updatedGameState.wondersForPick)
                    return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
                }
                1 -> {
                    val decision = createDecision(PlayerTurn.PLAYER_2, updatedGameState.wondersForPick)
                    return updatedGameState.update(decisionQueue_ = updatedGameState.decisionQueue.enqueue(decision))
                }
                0 -> {
                    // Update game phase
                    updatedWondersForPickDeck = Deck("Wonders for pick")
                    return updatedGameState.update(wondersForPickDeck_ = updatedWondersForPickDeck)
                            .addMainTurnDecision(generator, logger)
                }
                else -> throw Exception("This should not happen")
            }
            else -> throw Exception("This should not happen")
        }


    }

    private fun createDecision(playerTurn: PlayerTurn, wondersForPickDeck : Deck) : Decision {
        // Create decision
        val options : Vector<Action> = wondersForPickDeck.cards
                .map { ChooseStartingWonder(playerTurn, it) }
        return Decision(playerTurn, options)
    }

    override fun toString(): String {
        return "Choose ${card.name} as starting wonder"
    }
}