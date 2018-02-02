package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Decision
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.Enhancement
import com.aigamelabs.swduel.enums.Wonders
import com.aigamelabs.swduel.enums.CardColor
import io.vavr.collection.HashSet
import io.vavr.collection.Vector
import java.util.logging.Logger


class BuildWonder(playerTurn: PlayerTurn, val card: Card) : Action(playerTurn) {

    override fun process(gameState: GameState, generator : RandomWithTracker?, logger: Logger?): GameState {

        // Gather data
        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val cost = playerCity.canBuild(card, opponentCity) ?: throw Exception("Wonder not affordable")
        val playerCoins = playerCity.coins

        // Move card
        val updatedUnbuiltWonders = playerCity.unbuiltWonders.remove(card)
        val updatedWonders = playerCity.wonders.add(card)
        val updatedPlayerCity = playerCity.update(wonders_ = updatedWonders, unbuiltWonders_ = updatedUnbuiltWonders, coins_ = playerCoins - cost)
        val updatedPlayerCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)


        val hasExtraTurn = gameState.getPlayerCity(playerTurn).hasProgressToken(Enhancement.THEOLOGY) ||
                setOf(
                        Wonders.PIRAEUS,
                        Wonders.THE_SPHINX,
                        Wonders.THE_APPIAN_WAY,
                        Wonders.THE_HANGING_GARDENS,
                        Wonders.THE_TEMPLE_OF_ARTEMIS
                ).contains(card.wonders)

        val updatedGameState = if (hasExtraTurn)
            gameState.update(playerCities_ = updatedPlayerCities)
        else
            gameState.update(playerCities_ = updatedPlayerCities, nextPlayer_ = playerTurn.opponent())

        return processWonders(updatedGameState).updateBoard(generator, logger)
    }

    private fun processWonders(gameState: GameState): GameState {
        return when (card.wonders) {
            Wonders.THE_GREAT_LIBRARY -> {
                val updatedDecisionQueue = gameState.decisionQueue.
                        insert(0, addProgressTokenSelectionAction(gameState))
                gameState.update(decisionQueue_ = updatedDecisionQueue)
            }

            Wonders.THE_MAUSOLEUM -> {
                return if (gameState.burnedCards.size() > 0) {
                    val updatedDecisionQueue = gameState.decisionQueue.insert(0, buildBurned(gameState))
                    gameState.update(decisionQueue_ = updatedDecisionQueue)
                } else {
                    gameState
                }
            }

            Wonders.THE_COLOSSUS -> {
                addMilitaryProgress(2, gameState)
            }

            Wonders.CIRCUS_MAXIMUS -> {
                val updatedGameState = addMilitaryProgress(1, gameState)
                addBuildingToBurnActions(updatedGameState, CardColor.GRAY)
                        .checkMilitarySupremacy()
            }
            Wonders.THE_STATUE_OF_ZEUS -> {
                val updatedGameState = addMilitaryProgress(1, gameState)
                addBuildingToBurnActions(updatedGameState, CardColor.GRAY)
                        .checkMilitarySupremacy()
            }
            Wonders.THE_APPIAN_WAY -> {
                val playerCity = gameState.getPlayerCity(playerTurn)
                val updatedPlayerCity = addCoinsToCity(playerCity, card.coinsProduced) // Add player coins
                val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
                val updatedOpponentCity = removeCoinsFromCity(opponentCity, 3) // Remove opponent coins
                val updatedCities = gameState.playerCities
                        .put(playerTurn, updatedPlayerCity)
                        .put(playerTurn.opponent(), updatedOpponentCity)
                gameState.update(playerCities_ = updatedCities)
            }
            Wonders.THE_TEMPLE_OF_ARTEMIS,
            Wonders.THE_HANGING_GARDENS -> {
                val playerCity = gameState.getPlayerCity(playerTurn)
                val updatedPlayerCity = addCoinsToCity(playerCity, card.coinsProduced)
                val updatedCities = gameState.playerCities.put(playerTurn, updatedPlayerCity)
                gameState.update(playerCities_ = updatedCities)
            }
            Wonders.THE_GREAT_LIGHTHOUSE,
            Wonders.THE_SPHINX,
            Wonders.THE_PYRAMIDS,
            Wonders.PIRAEUS -> gameState
            else -> {
                throw Exception()
            }
        }
    }

    private fun buildBurned(gameState: GameState): Decision {
        val actions = gameState.burnedCards.cards
                .map { BuildBurned(playerTurn, it) }
        return Decision(playerTurn, Vector.ofAll(actions), "BuildWonder.buildBurned")
    }

    private fun addProgressTokenSelectionAction(gameState: GameState): Decision {
        val actions = gameState.discardedProgressTokens.cards
                .map { ChooseUnusedProgressToken(playerTurn, it) }
        return Decision(playerTurn, Vector.ofAll(actions), "BuildWonder.addScienceTokenSelection")
    }

    /**
     * Removes from the opponent city the specified amount of coins
     */
    private fun removeCoinsFromCity(city: PlayerCity, coinsToBeRemoved: Int): PlayerCity {
        return city.update(coins_ = city.coins - coinsToBeRemoved)
    }

    /**
     * Add coins generated by this card to the player city coins
     */
    private fun addCoinsToCity(city: PlayerCity, coinsToAdd: Int): PlayerCity {
        return city.update(coins_ = city.coins - coinsToAdd)
    }

    private fun addMilitaryProgress(strength: Int, gameState: GameState): GameState {

        // Move military tokens
        val militaryOutcome = gameState.militaryBoard.addMilitaryPointsTo(strength, playerTurn)

        // Deal with any burning any coins
        return if (militaryOutcome.first == 0) {
            gameState.update(militaryBoard_ = militaryOutcome.second)
        } else {
            val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
            val updatedOpponentCity = removeCoinsFromCity(opponentCity, militaryOutcome.first)
            val updatedCities = gameState.playerCities.put(playerTurn, updatedOpponentCity)
            gameState.update(militaryBoard_ = militaryOutcome.second, playerCities_ = updatedCities)
        }
    }

    private fun getBurnableBuildings(city: PlayerCity, color: CardColor): HashSet<Card> {
        return city.buildings.filter { it.color == color }

    }

    private fun addBuildingToBurnActions(gameState: GameState, color: CardColor): GameState {
        val updatedGameState = addMilitaryProgress(1, gameState)

        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val burnable = getBurnableBuildings(opponentCity, color)
        return if (burnable.isEmpty)
            gameState
        else {
            val actions = burnable.map { BurnOpponentCard(playerTurn, it) }
            val newDecision = Decision(playerTurn, Vector.ofAll(actions), "BuildWonder.addBuildingToBurnActions")
            val updatedDecisionQueue = updatedGameState.decisionQueue.insert(0, newDecision)
            gameState.update(decisionQueue_ = updatedDecisionQueue)
        }

    }

    override fun toString(): String {
        return "Build wonder ${card.name}"
    }
}
