package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class ChooseStartingWonder(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {
        // TODO
        // add next starting wonder decision

        val numForPick = gameState.getDeck(GameDeck.WONDERS_FOR_PICK).size()
        val numUnused = gameState.getDeck(GameDeck.UNUSED_WONDERS).size()

        if (numForPick == 3 && numUnused == 8) {
            val decision = createDecision(PlayerTurn.PLAYER_2, gameState.getDeck(GameDeck.WONDERS_FOR_PICK))
            return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision))
        } else if (numForPick == 2 && numUnused == 8) {
            val decision = createDecision(PlayerTurn.PLAYER_2, gameState.getDeck(GameDeck.WONDERS_FOR_PICK))
            return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision))
        } else if (numForPick == 1 && numUnused == 8) {
            // Give remaining card to P1
            val lastWonder = gameState.getDeck(GameDeck.WONDERS_FOR_PICK).drawCard().first
            val playerCity = gameState.getPlayerCity(PlayerTurn.PLAYER_1)
            val newPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(lastWonder))
            val newPlayerCities = gameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayerCity)
            // Draw another 4 wonders for pick
            val drawOutcome = gameState.getDeck(GameDeck.UNUSED_WONDERS).drawCards(4)
            val newUnusedWondersDeck = drawOutcome.second
            val newWondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)
            val newDecks = gameState.decks
                    .put(GameDeck.UNUSED_WONDERS, newUnusedWondersDeck)
                    .put(GameDeck.WONDERS_FOR_PICK, newWondersForPickDeck)

            val decision = createDecision(PlayerTurn.PLAYER_2, gameState.getDeck(GameDeck.WONDERS_FOR_PICK))
            return gameState.update(decks_ = newDecks, playerCities_ = newPlayerCities, decisionQueue_ = gameState.decisionQueue.enqueue(decision))

        } else if (numForPick == 3 && numUnused == 4) {
            val decision = createDecision(PlayerTurn.PLAYER_1, gameState.getDeck(GameDeck.WONDERS_FOR_PICK))
            return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision))
        } else if (numForPick == 2 && numUnused == 4) {
            val decision = createDecision(PlayerTurn.PLAYER_1, gameState.getDeck(GameDeck.WONDERS_FOR_PICK))
            return gameState.update(decisionQueue_ = gameState.decisionQueue.enqueue(decision))
        } else if (numForPick == 1 && numUnused == 4) {
            // Give remaining card to P2
            val lastWonder = gameState.getDeck(GameDeck.WONDERS_FOR_PICK).drawCard().first
            val playerCity = gameState.getPlayerCity(PlayerTurn.PLAYER_1)
            val newPlayerCity = playerCity.update(unbuiltWonders_ = playerCity.unbuiltWonders.add(lastWonder))
            val newPlayerCities = gameState.playerCities.put(PlayerTurn.PLAYER_1, newPlayerCity)
            // Update game phase
            val newDecks = gameState.decks
                    .put(GameDeck.WONDERS_FOR_PICK, Deck("Wonders for pick"))
            return gameState.update(decks_ = newDecks, playerCities_ = newPlayerCities, gamePhase_ = GamePhase.FIRST_AGE)
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