package com.aigamelabs.swduel.players

import com.aigamelabs.mcts.actionselection.HighestScore
import com.aigamelabs.mcts.nodeevaluation.GameVictory
import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

class MctsHighestScore(
        name: String,
        gameData: GameData
) : MctsBasedBot(
        name,
        gameData,
        HighestScore(),
        GameVictory(PlayerTurn.PLAYER_1),
        GameVictory(PlayerTurn.PLAYER_2)
) {

    override fun finalize(gameState: GameState) {}

    override fun decide(gameState: GameState, options: Vector<Action>): Action {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}