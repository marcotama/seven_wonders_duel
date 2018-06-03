package com.aigamelabs.tictactoe

import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.tictactoe.actions.PlaceTile
import com.aigamelabs.tictactoe.enums.GamePhase
import io.vavr.collection.Queue
import io.vavr.collection.Vector

object GameStateFactory {

    fun createNewGameState() : GameState {

        val board = GameState.generateBoard()

        // Create decision
        val actions : Vector<Action<GameState>> = Vector.ofAll(board.keySet()
                .map { PlaceTile(PlayerTurn.PLAYER_1, it) })
        val decision = Decision(PlayerTurn.PLAYER_1, actions)

        return GameState(
                board = board,
                decisionQueue = Queue.of(decision),
                gamePhase = GamePhase.PLAYING,
                nextPlayer = PlayerTurn.PLAYER_1
        )
    }
}