package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.actions.BuildBuilding
import com.aigamelabs.swduel.actions.BurnForMoney
import com.aigamelabs.swduel.actions.BurnForWonder
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

/**
 * Provides utilities to generate instances of [Decision].
 */
object DecisionFactory {
    /**
     * Generates a decision for a main turn. This is the first decision a player must take at each turn.
     * That is, every available card in the deck provides three additional possibilities: to build it, to burn it for
     * money or to turn it into a wonder.
     *
     * @param playerTurn the player who needs to make the decision
     * @return a [Decision] including all possible actions a player can do at the start of her turn
     */

    fun makeTurnDecision(playerTurn: PlayerTurn, gameState: GameState) : Decision {
        val playerCity = gameState.getPlayerCity(playerTurn)
        val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
        val canBuildSomeWonders =
                !playerCity.wonders.filter { playerCity.canBuild(it, opponentCity) != null }.isEmpty &&
                        gameState.getPlayerCity(playerTurn.opponent()).wonders.size() < 4

        val availCards = gameState.cardStructure!!.availableCards()
        // The player can always burn any uncovered card for money
        var actions : Vector<Action> = availCards.map { BurnForMoney(playerTurn, it) }
        // If the player can afford at least a wonder, then he can also sacrifice any uncovered card to build the wonder
        if (canBuildSomeWonders) {
            actions = actions.appendAll(availCards.map { BurnForWonder(playerTurn, it) })
        }
        // The player can also build a card, if the city can afford it
        actions = actions.appendAll(availCards
                .filter { playerCity.canBuild(it, opponentCity) != null }
                .map { BuildBuilding(playerTurn, it) }
        )
        return Decision(playerTurn, actions, "DecisionFactory.makeTurnDecision")
    }
}