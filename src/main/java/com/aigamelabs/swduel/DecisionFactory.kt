package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.actions.Build
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
     * @param gameState the state of the game when the decision is to be made
     * @return a [Decision] including all possible actions a player can do at the start of her turn
     */
    fun makeTurnDecision(playerTurn: PlayerTurn, gameState: GameState, main : Boolean) : Decision {
        val playerCity = gameState.playerCities[playerTurn]
                .getOrElseThrow { Exception("Player not found") }
        val canBuildSomeWonders = !playerCity.wonders
                .filter { w -> playerCity.canBuild(w) != null }
                .isEmpty

        val availCards = gameState.currentGraph.verticesWithNoIncomingEdges()
        // The player can always burn any uncovered card for money
        var actions : Vector<Action> = availCards.map { card -> BurnForMoney(playerTurn, card) }
        // If the player can afford at least a wonder, then he can also sacrifice any uncovered card to build the wonder
        if (canBuildSomeWonders) {
            actions = actions.appendAll(availCards.map { card -> BurnForWonder(playerTurn, card) })
        }
        // The player can also build a card, if the city can afford it
        actions = actions.appendAll(
                availCards.filter { card -> playerCity.canBuild(card) != null }
                        .map { card -> Build(playerTurn, card) }
        )
        return Decision(playerTurn, actions, main)
    }
}