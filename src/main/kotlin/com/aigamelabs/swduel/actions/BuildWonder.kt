package com.aigamelabs.swduel.actions

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.*
import com.aigamelabs.swduel.enums.*
import java.util.logging.Logger


class BuildWonder(playerTurn: PlayerTurn, val card: Card) : Action(playerTurn) {

    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?): GameState {

        // Gather data
        val playerCity = gameState.getPlayerCity(player)
        val opponentCity = gameState.getPlayerCity(player.opponent())
        val cost = playerCity.canBuild(card, opponentCity) ?: throw Exception("Wonder not affordable")
        val playerCoins = playerCity.coins

        // Move card
        val updatedUnbuiltWonders = playerCity.unbuiltWonders.remove(card)
        val updatedWonders = playerCity.wonders.add(card)
        val updatedPlayerCity = playerCity.update(wonders_ = updatedWonders, unbuiltWonders_ = updatedUnbuiltWonders,
                coins_ = playerCoins - cost)


        val hasExtraTurn = gameState.getPlayerCity(player).hasProgressToken(Enhancement.THEOLOGY) ||
                setOf(
                        Wonders.PIRAEUS,
                        Wonders.THE_SPHINX,
                        Wonders.THE_APPIAN_WAY,
                        Wonders.THE_HANGING_GARDENS,
                        Wonders.THE_TEMPLE_OF_ARTEMIS
                ).contains(card.wonders)

        val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else opponentCity
        val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else opponentCity
        val updatedGameState = if (hasExtraTurn)
            gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City)
        else
            gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City,
                    nextPlayer_ = player.opponent())

        return processWonders(updatedGameState, generator, logger)
    }

    private fun processWonders(gameState: GameState, generator: RandomWithTracker, logger: Logger?): GameState {
        return when (card.wonders) {
            Wonders.THE_GREAT_LIBRARY -> {
                gameState.addSelectDiscardedProgressTokenDecision(player)
            }

            Wonders.THE_MAUSOLEUM -> {
                return if (gameState.burnedCards.size() > 0) {
                    gameState.addSelectBurnedBuildingToBuildDecision(player)
                } else {
                    gameState.addMainTurnDecision(generator, logger)
                }
            }

            Wonders.THE_COLOSSUS -> {
                gameState.addMilitaryProgress(2, player)
                        .addMainTurnDecision(generator, logger)
            }

            Wonders.CIRCUS_MAXIMUS -> {
                val updatedGameState = gameState.addBurnOpponentBuildingDecision(player, CardColor.GRAY)
                        ?: gameState.addMainTurnDecision(generator, logger)

                return updatedGameState
                        .addMilitaryProgress(1, player)
                        .checkMilitarySupremacy()
            }
            Wonders.THE_STATUE_OF_ZEUS -> {
                val updatedGameState = gameState.addBurnOpponentBuildingDecision(player, CardColor.BROWN)
                        ?: gameState.addMainTurnDecision(generator, logger)

                return updatedGameState
                        .addMilitaryProgress(1, player)
                        .checkMilitarySupremacy()
            }
            Wonders.THE_APPIAN_WAY -> {
                val playerCity = gameState.getPlayerCity(player)
                val opponentCity = gameState.getPlayerCity(player.opponent())
                val updatedPlayerCity = playerCity.addCoins(card.coinsProduced) // Add player coins
                val updatedOpponentCity = opponentCity.removeCoins(3) // Remove opponent coins
                val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else updatedOpponentCity
                val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else updatedOpponentCity
                gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City)
                        .addMainTurnDecision(generator, logger)
            }
            Wonders.THE_TEMPLE_OF_ARTEMIS,
            Wonders.THE_HANGING_GARDENS -> {
                val playerCity = gameState.getPlayerCity(player)
                val opponentCity = gameState.getPlayerCity(player.opponent())
                val updatedPlayerCity = playerCity.addCoins(card.coinsProduced)
                val updatedPlayer1City = if (player == PlayerTurn.PLAYER_1) updatedPlayerCity else opponentCity
                val updatedPlayer2City = if (player == PlayerTurn.PLAYER_2) updatedPlayerCity else opponentCity
                gameState.update(player1City_ = updatedPlayer1City, player2City_ = updatedPlayer2City)
                        .addMainTurnDecision(generator, logger)
            }
            Wonders.THE_GREAT_LIGHTHOUSE,
            Wonders.THE_SPHINX,
            Wonders.THE_PYRAMIDS,
            Wonders.PIRAEUS -> gameState.addMainTurnDecision(generator, logger)
            else -> {
                throw Exception()
            }
        }
    }

    override fun toString(): String {
        return "Build wonder ${card.name}"
    }
}
