package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashMap
import io.vavr.collection.Vector


class ChooseWonderToBuild(playerTurn: PlayerTurn, val card: Card) : Action(playerTurn) {

    override fun process(gameState: GameState): GameState {

        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val cost = playerCity.canBuild(card, opponentCity) ?: throw Exception("Wonder not affordable")
        val playerCoins = playerCity.coins

        val newUnbuiltWonders = playerCity.unbuiltWonders.remove(card)
        val newWonders = playerCity.wonders.add(card)

        val newPlayerCity = playerCity.update(wonders_ = newWonders, unbuiltWonders_ = newUnbuiltWonders, coins_ = playerCoins - cost)
        val newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)


        val hasExtraTurn = gameState.getPlayerCity(playerTurn).hasProgressToken(Enhancement.THEOLOGY)
        val newGameState = if (hasExtraTurn)
            gameState.update(decisionQueue_ = gameState.decisionQueue.insert(0, addExtraTurn(gameState)), playerCities_ = newPlayerCities)
        else
            gameState.update(playerCities_ = newPlayerCities)

        return processWonders(newGameState, hasExtraTurn)
    }

    private fun processWonders(gameState: GameState, hasExtraTurn: Boolean): GameState {
        when (card.wonders) {
            Wonders.THE_GREAT_LIBRARY -> {
                val newDecisionQueue = gameState.decisionQueue.
                        insert(0, addScienceTokenSelectionAction(gameState))
                return gameState.update(decisionQueue_ = newDecisionQueue)
            }
            Wonders.THE_HANGING_GARDENS -> {
                val newPlayerCities = addCoinToCity(gameState)

                // Add an extra turn if it hasn't got one already from the science token
                return if (!hasExtraTurn) {
                    gameState.update(decisionQueue_ = gameState.decisionQueue.insert(0, addExtraTurn(gameState)),
                            playerCities_ = newPlayerCities)
                } else {
                    gameState.update(playerCities_ = newPlayerCities)
                }

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
                val newDecisionQueue = updatedMilitaryGameState.decisionQueue
                        .insert(0, addBuildingToBurnActions(updatedMilitaryGameState, CardColor.GRAY))
                return updatedMilitaryGameState.update(decisionQueue_ = newDecisionQueue)

            }
            Wonders.THE_STATUE_OF_ZEUS -> {
                val updatedMilitaryGameState = addMilitaryProgress(1, gameState)
                val newDecisionQueue = updatedMilitaryGameState.decisionQueue
                        .insert(0, addBuildingToBurnActions(updatedMilitaryGameState, CardColor.BROWN))
                return updatedMilitaryGameState.update(decisionQueue_ = newDecisionQueue)
            }
            Wonders.THE_TEMPLE_OF_ARTEMIS -> {
                val newPlayerCities = addCoinToCity(gameState)
                val updatedPlayerCities = removeCoinsFromCity(newPlayerCities, 3)

                return if (!hasExtraTurn)
                    gameState.update(decisionQueue_ = gameState.decisionQueue.
                            insert(0, addExtraTurn(gameState)), playerCities_ = updatedPlayerCities)
                else
                    gameState.update(playerCities_ = updatedPlayerCities)
            }
            Wonders.THE_APPIAN_WAY -> {
                val newPlayerCities = addCoinToCity(gameState)

                return if (!hasExtraTurn)
                    gameState.update(decisionQueue_ = gameState.decisionQueue.
                            insert(0, addExtraTurn(gameState)), playerCities_ = newPlayerCities)
                else
                    gameState.update(playerCities_ = newPlayerCities)
            }
            Wonders.THE_SPHINX -> {
                return if (!hasExtraTurn)
                    gameState.update(decisionQueue_ = gameState.decisionQueue.
                            insert(0, addExtraTurn(gameState)))
                else
                    gameState
            }
            Wonders.THE_PYRAMIDS -> {
                return gameState
            }
            Wonders.PIRAEUS -> {
                return if (!hasExtraTurn)
                    gameState.update(decisionQueue_ = gameState.decisionQueue.
                            insert(0, addExtraTurn(gameState)))
                else
                    gameState
            }
            else -> {
                throw Exception()
            }
        }
    }

    private fun buildBurned(gameState: GameState): Decision {
        val actions = gameState.burnedDeck.cards
                .map { c -> BuildBurned(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)

    }

    private fun addScienceTokenSelectionAction(gameState: GameState): Decision {
        val actions = gameState.unusedScienceDeck.cards
                .map { c -> ChooseUnusedScienceToken(playerTurn, c) }
        return Decision(playerTurn, Vector.ofAll(actions), false)
    }

    private fun removeCoinsFromCity(playerCities: HashMap<PlayerTurn, PlayerCity>, coinsToBeRemoved: Int)
            : HashMap<PlayerTurn, PlayerCity> {
        val opponentCity = playerCities.get(playerTurn.opponent()).getOrElseThrow { Exception("No opponent city") }
        val newOpponentCoins = opponentCity.coins - coinsToBeRemoved
        val updatedOpponentCity = opponentCity.update(coins_ = newOpponentCoins)
        return playerCities.put(
                playerTurn.opponent(),
                updatedOpponentCity
        )
    }

    private fun addCoinToCity(gameState: GameState): HashMap<PlayerTurn, PlayerCity> {
        val playerCity = gameState.getPlayerCity(playerTurn)
        val newPlayerCoins = playerCity.coins + card.coinsProduced
        val newPlayerCity = playerCity.update(coins_ = newPlayerCoins)
        return gameState.playerCities.put(playerTurn, newPlayerCity)
    }

    private fun addExtraTurn(gameState: GameState): Decision {
        return DecisionFactory.makeTurnDecision(playerTurn, gameState, false)
    }

    private fun addMilitaryProgress(strength: Int, gameState: GameState): GameState {

        // Move military tokens
        val militaryOutcome = gameState.militaryBoard.addMilitaryPointsTo(strength, playerTurn)

        // Deal with any burning any coins
        return if (militaryOutcome.first == 0) {
            gameState.update(militaryBoard_ = militaryOutcome.second)
        } else {
            val updatedCityOutcome = removeCoinsFromCity(gameState.playerCities, militaryOutcome.first)
            gameState.update(militaryBoard_ = militaryOutcome.second, playerCities_ = updatedCityOutcome)
        }
    }

    private fun addBuildingToBurnActions(gameState: GameState, colourToBurn: CardColor): Decision {

        val playerCity = gameState.getPlayerCity(playerTurn.opponent())
        val colouredCards = playerCity.buildings.filter { c -> c.color == colourToBurn }
        val actions = colouredCards.map { c -> BurnOpponentCard(playerTurn, c) }

        return Decision(playerTurn, Vector.ofAll(actions), false)
    }
}
