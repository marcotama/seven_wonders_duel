package com.aigamelabs.myfish

import com.aigamelabs.game.Action
import com.aigamelabs.game.Decision
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.actions.PlacePenguin
import com.aigamelabs.myfish.enums.GamePhase
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

        val players = when (numberOfPlayers) {
            2 -> HashSet.of(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2)
            3 -> HashSet.of(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3)
            4 -> HashSet.of(PlayerTurn.PLAYER_1, PlayerTurn.PLAYER_2, PlayerTurn.PLAYER_3, PlayerTurn.PLAYER_4)
            else -> throw Exception("This game cannot have $numberOfPlayers players")
        }

        return GameState(
                board = board,
                decisionQueue = Queue.of(decision),
                gamePhase = GamePhase.PENGUINS_PLACEMENT,
                nextPlayer = PlayerTurn.PLAYER_1,
                penguins = HashMap.empty<PlayerTurn,HashMap<Int,Triple<Int,Int,Int>>>(),
                score = HashMap.ofAll(players.map { Pair(it, 0) }.toMap())
        )
    }
}