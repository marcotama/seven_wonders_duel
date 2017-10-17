package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Decision
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.PlayerCity
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.Wonders
import io.vavr.collection.HashMap
import io.vavr.collection.Vector
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ChooseWonderToBuild(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {

    override fun process(gameState: GameState) : GameState {

//        val playerCity = gameState.playerCities[playerTurn]

        val playerCity =  gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }
        val newWondersDeck = playerCity.wondersDeck.removeCard(card)
        val newWonders = playerCity.wonders
        newWonders.add(card)
        val newPlayerCity = playerCity.update(wonders_ = newWonders, wondersDeck_ = newWondersDeck)
        val newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)
        val newGameState = gameState.update(playerCities_ = newPlayerCities)

        return processWonders(newGameState)
    }

    fun processWonders(gameState: GameState): GameState {
        when (card.wonders) {
            Wonders.THE_GREAT_LIBRARY -> {
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addScienceTokenSelectionAction(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue)
            }
            Wonders.THE_HANGING_GARDENS -> {
                val newPlayerCities = addCoinToCity(gameState)
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addExtraTurn(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue, playerCities_ = newPlayerCities)

            }
            Wonders.THE_MAUSOLEUM -> {
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, buildBurned(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue)

            }
            Wonders.THE_COLOSSUS -> {

            }
            Wonders.THE_GREAT_LIGHTHOUSE -> {

            }
            Wonders.CIRCUS_MAXIMUS -> {

            }
            Wonders.THE_STATUE_OF_ZEUS -> {

            }
            Wonders.THE_TEMPLE_OF_ARTEMIS -> {

            }
            Wonders.THE_APPIAN_WAY -> {

            }
            Wonders.THE_SPHINX -> {

            }
            Wonders.THE_PYRAMIDS -> {

            }
            Wonders.PIRAEUS-> {

            }
        }
        return gameState
    }

    fun buildBurned (gameState: GameState): Decision {
        val actions = gameState.decks.get(GameDeck.BURNED)
                .getOrElseThrow { -> Exception("No unused sicence deck") }
                .cards
                .map { c -> BuildBurned(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)

    }
    fun addScienceTokenSelectionAction (gameState: GameState) : Decision {
         val actions = gameState.decks.get(GameDeck.UNUSED_SCIENCE_TOKENS)
                .getOrElseThrow { -> Exception("No unused sicence deck") }
                .cards
                .map { c -> ChooseUnusedScienceToken(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)
    }

    fun addCoinToCity (gameState: GameState) : HashMap<PlayerTurn, PlayerCity> {

        val playerCity =  gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }
        val newPlayerCoins = playerCity.coins + card.coinsProduced
        val newPlayerCity = playerCity.update(coins_ = newPlayerCoins)
        return gameState.playerCities.put(playerTurn, newPlayerCity)
    }

    fun addExtraTurn (gameState: GameState) : Decision {
        val actions = gameState.allAvailableAction()
        return Decision(playerTurn,actions,false)
    }

}