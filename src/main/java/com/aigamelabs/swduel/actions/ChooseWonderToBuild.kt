package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashMap
import io.vavr.collection.Vector


class ChooseWonderToBuild(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {

    override fun process(gameState: GameState): GameState {

//        val playerCity = gameState.playerCities[playerTurn]

        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }
        val newWondersDeck = playerCity.wondersDeck.removeCard(card)
        val newWonders = playerCity.wonders
        newWonders.add(card)
        val newPlayerCity = playerCity.update(wonders_ = newWonders, wondersDeck_ = newWondersDeck)
        val newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)
        val newGameState = gameState.update(playerCities_ = newPlayerCities)

        //val hasExtraTurn = playerCity.hasProgressToken(Enhancement.THEOLOGY)

        //TODO check science token extra turn

        return processWonders(newGameState)
    }

    private fun processWonders(gameState: GameState): GameState {

        // check if all wonders have an extra turn (flag et)
//        val flag = gameState.getPlayerCity(playerTurn).hasProgressToken(Enhancement.THEOLOGY)
//
//        val newGameState = if (flag)
//            gameState.update(decisionQueue_ = gameState.decisionQueue.insert(0, addExtraTurn(gameState)))
//        else
//            gameState
//

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
                return addMilitaryProgress(2, gameState)
            }
            Wonders.THE_GREAT_LIGHTHOUSE -> {
                return gameState
            }
            Wonders.CIRCUS_MAXIMUS -> {
                val updatedMilitaryGameState = addMilitaryProgress(1, gameState)

                val newDecisionQueue  = updatedMilitaryGameState.decisionQueue
                        .insert(0,addBuildingToBurnActions(updatedMilitaryGameState, CardColor.GRAY))

                return updatedMilitaryGameState.update(decisionQueue_ = newDecisionQueue)

            }
            Wonders.THE_STATUE_OF_ZEUS -> {
                val updatedMilitaryGameState = addMilitaryProgress(1, gameState)
                val newDecisionQueue  = updatedMilitaryGameState.decisionQueue
                        .insert(0,addBuildingToBurnActions(updatedMilitaryGameState, CardColor.BROWN))
                return updatedMilitaryGameState.update(decisionQueue_ = newDecisionQueue)
            }
            Wonders.THE_TEMPLE_OF_ARTEMIS -> {
                val newPlayerCities = addCoinToCity(gameState)
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addExtraTurn(gameState))
                val updatedPlayerCities = removeCoinsFromCity(newPlayerCities, 3)
                return gameState.update(decisionQueue_ = newDecisionQueue, playerCities_ = updatedPlayerCities)
            }
            Wonders.THE_APPIAN_WAY -> {
                val newPlayerCities = addCoinToCity(gameState)
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addExtraTurn(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue, playerCities_ = newPlayerCities)
            }
            Wonders.THE_SPHINX -> {
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addExtraTurn(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue)

            }
            Wonders.THE_PYRAMIDS -> {
                return gameState
            }
            Wonders.PIRAEUS -> {
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addExtraTurn(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue)
            }
            else -> {
                throw Exception()
            }
        }
    }

    private fun buildBurned(gameState: GameState): Decision {
        val actions = gameState.decks.get(GameDeck.BURNED)
                .getOrElseThrow { -> Exception("No unused sicence deck") }
                .cards
                .map { c -> BuildBurned(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)

    }

    private fun addScienceTokenSelectionAction(gameState: GameState): Decision {
        val actions = gameState.decks.get(GameDeck.UNUSED_SCIENCE_TOKENS)
                .getOrElseThrow { -> Exception("No unused sicence deck") }
                .cards
                .map { c -> ChooseUnusedScienceToken(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)
    }

    private fun removeCoinsFromCity(playerCities: HashMap<PlayerTurn, PlayerCity>, coinsToBeRemoved: Int): HashMap<PlayerTurn, PlayerCity> {
        val opponentCity = playerCities.get(playerTurn.other()).getOrElseThrow { -> Exception("No oopponent city") }
        val newOpponentCoins = opponentCity.coins - coinsToBeRemoved
        val updatedOpponentCity = opponentCity.update(coins_ = newOpponentCoins)
        return playerCities.put(
                playerTurn.other(),
                updatedOpponentCity
        )
    }

    private fun addCoinToCity(gameState: GameState): HashMap<PlayerTurn, PlayerCity> {

        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }
        val newPlayerCoins = playerCity.coins + card.coinsProduced
        val newPlayerCity = playerCity.update(coins_ = newPlayerCoins)
        return gameState.playerCities.put(playerTurn, newPlayerCity)
    }

    private fun addExtraTurn(gameState: GameState): Decision {
        return DecisionFactory.makeTurnDecision(playerTurn, gameState, false)
    }

    private fun addMilitaryProgress(strength: Int, gameState: GameState): GameState {

        // Move military tokens
        val militaryOutcome = gameState.militaryBoard.advantagePlayer(strength, playerTurn)
        //deal with any burning any coins

        return if (militaryOutcome.first == 0) {
            gameState.update(militaryBoard_ = militaryOutcome.second)
        } else {
            val updatedCityOutcome = removeCoinsFromCity(gameState.playerCities, militaryOutcome.first)
            gameState.update(militaryBoard_ = militaryOutcome.second, playerCities_ = updatedCityOutcome)
        }
    }

    private fun addBuildingToBurnActions (gameState: GameState, colourToBurn: CardColor): Decision {

        val playerCity = gameState.playerCities.get(playerTurn)
                .getOrElseThrow { -> Exception("The player  does not have a city") }

        val colouredCards = playerCity.buildings.filter { c -> c.color == colourToBurn }

        val actions = colouredCards.map { c -> BurnOpponentCard(playerTurn, c) }

        return Decision(playerTurn, Vector.ofAll(actions), false)
    }
}
