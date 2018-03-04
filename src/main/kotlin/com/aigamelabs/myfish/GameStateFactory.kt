package com.aigamelabs.myfish

import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.actions.PlacePenguin
import com.aigamelabs.myfish.enums.GamePhase
import com.aigamelabs.myfish.enums.PenguinId
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.HashSet
import io.vavr.collection.HashMap
import io.vavr.collection.Queue
import io.vavr.collection.Vector

object GameStateFactory {

    fun createNewGameState(generator : RandomWithTracker) : GameState {
        return createNewGameState(2, generator)
    }

    fun createNewGameState(numberOfPlayers: Int, generator : RandomWithTracker) : GameState {

        val board = GameState.generateBoard(generator)

        // Create decision
        val actions : Vector<Action<GameState>> = Vector.ofAll(board.keySet()
                .map { PlacePenguin(PlayerTurn.PLAYER_1, it) })
        val decision = Decision(PlayerTurn.PLAYER_1, actions)

        val players = HashSet.ofAll(PlayerTurn.getPlayers(numberOfPlayers))

        return GameState(
                board = board,
                decisionQueue = Queue.of(decision),
                gamePhase = GamePhase.PENGUINS_PLACEMENT,
                nextPlayer = PlayerTurn.PLAYER_1,
                penguins = HashMap.ofAll(players.map { Pair(it, HashMap.empty<PenguinId,Triple<Int,Int,Int>>()) }.toMap() ),
                score = HashMap.ofAll(players.map { Pair(it, 0) }.toMap())
        )
    }
}