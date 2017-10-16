package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Decision
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.GameDeck
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.Wonders
import io.vavr.collection.Vector

class ChooseWonderToBuild(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {

    override fun process(gameState: GameState) : GameState {

//        val playerCity = gameState.playerCities[playerTurn]

        val playerCity =  gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }
        val newWondersDeck = playerCity.wondersDeck.removeCard(card)
        val newWonders = playerCity.wonders.add(card)
        val newPlayerCity = playerCity.update(wonders_ = newWonders, wondersDeck_ = newWondersDeck)
        val newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)

        val newGameState = gameState.update(playerCities_ = newPlayerCities)
        return processWonders(newGameState)
    }

    fun processWonders(gameState: GameState): GameState {
        when (card.wonders) {
            Wonders.THE_GREAT_LIBRARY -> {
                val newdecisionQueue = gameState.decisionQueue.insert(0, addScienceTokenSelectionAction(gameState))
                return gameState.update(decisionQueue_ = newdecisionQueue)
            }
            Wonders.THE_HANGING_GARDENS -> {

            }
            Wonders.THE_MAUSOLEUM -> {

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

    fun addScienceTokenSelectionAction (gameState: GameState) : Decision {
        //build the action list
        var scienceSelectionActions: Vector<Action>

        val actions = gameState.decks.get(GameDeck.UNUSED_SCIENCE)
                .getOrElseThrow { -> Exception("No unused sicence deck") }
                .cards
                .map { c -> ChooseUnusedProgressToken(playerTurn, c) }
        val decision = Decision(playerTurn, Vector.ofAll(actions), false)

        //TODO we need a deck of the tokens that are not the buyable tokens
    return decision
    }
}