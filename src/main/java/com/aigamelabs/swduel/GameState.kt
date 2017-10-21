package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.actions.ChooseStartingWonder
import com.aigamelabs.swduel.enums.GamePhase

data class GameState (
        val decks : HashMap<GameDeck, Deck>,
        val cardStructure : CardStructure?,
        val progressTokens : HashSet<ProgressToken>,
        val militaryBoard: MilitaryBoard,
        val playerCities : HashMap<PlayerTurn,PlayerCity>,
        val decisionQueue: Queue<Decision>,
        val gamePhase: GamePhase
) {

    fun update(
            decks_ : HashMap<GameDeck, Deck>? = null,
            cardStructure_ : CardStructure? = null,
            progressTokens_ : HashSet<ProgressToken>? = null,
            militaryBoard_ : MilitaryBoard? = null,
            playerCities_ : HashMap<PlayerTurn,PlayerCity>? = null,
            decisionQueue_ : Queue<Decision>? = null,
            gamePhase_: GamePhase? = null
    ) : GameState {
        return GameState(
                decks_ ?: decks,
                cardStructure_ ?: cardStructure,
                progressTokens_ ?: progressTokens,
                militaryBoard_ ?: militaryBoard,
                playerCities_ ?: playerCities,
                decisionQueue_ ?: decisionQueue,
                gamePhase_ ?: gamePhase
        )
    }

    fun getPlayerCity(playerTurn : PlayerTurn) : PlayerCity {
        return playerCities.get(playerTurn).getOrElseThrow { Exception("Player city not found") }
    }

    fun getDeck(gameDeck: GameDeck) : Deck {
        return decks.get(gameDeck).getOrElseThrow { Exception("Deck not found") }
    }

    fun enqueueDecision (decision: Decision) : GameState {
        return update(decisionQueue_ = decisionQueue.enqueue(decision))
    }


    fun dequeueDecision() : Pair<Decision,GameState> {
        val dequeueOutcome = decisionQueue.dequeue()
        val decision = dequeueOutcome._1
        val newQueue = dequeueOutcome._2
        val newGameState = update(decisionQueue_ = newQueue)
        return Pair(decision, newGameState)
    }

    fun initGamePhase() : GameState {
        when (gamePhase) {
            GamePhase.WONDERS_SELECTION -> {
                // Draw 4 wonders
                val drawOutcome = getDeck(GameDeck.UNUSED_WONDERS).drawCards(4)
                val newUnusedWondersDeck = drawOutcome.second
                val newWondersForPickDeck = Deck("Wonders for pick", drawOutcome.first)
                val newDecks = decks
                        .put(GameDeck.UNUSED_WONDERS, newUnusedWondersDeck)
                        .put(GameDeck.WONDERS_FOR_PICK, newWondersForPickDeck)
                // Create decision
                val actions : Vector<Action> = newWondersForPickDeck.cards
                        .map { card -> ChooseStartingWonder(PlayerTurn.PLAYER_1, card) }
                val decision = Decision(PlayerTurn.PLAYER_1, actions, false)

                return update(decks_ = newDecks, decisionQueue_ = decisionQueue.enqueue(decision))
            }
            GamePhase.FIRST_AGE -> {
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
                // TODO
                return update(cardStructure_ = newCardStructure)
            }
            GamePhase.SECOND_AGE -> {
                val newCardStructure = CardStructureFactory.makeFirstAgeCardStructure()
                // TODO
                // add decision for who starts this age
                return update(cardStructure_ = newCardStructure)
            }
            GamePhase.THIRD_AGE -> {
                val newCardStructure = CardStructureFactory.makeThirdAgeCardStructure()
                // TODO
                // add decision for who starts this age
                return update(cardStructure_ = newCardStructure)
            }
            GamePhase.MILITARY_SUPREMACY -> {
                // TODO
                return this
            }
            GamePhase.SCIENCE_SUPREMACY -> {
                // TODO
                return this
            }
            GamePhase.CIVILIAN_VICTORY -> {
                // TODO
                return this
            }
        }
    }
}