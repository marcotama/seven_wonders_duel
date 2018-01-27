package com.aigamelabs

import com.aigamelabs.swduel.Game
import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.GameStateFactory
import com.aigamelabs.swduel.Player
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.players.MctsHighestScore
import com.aigamelabs.swduel.players.RandomPlayer
import com.aigamelabs.utils.RandomWithTracker
import java.util.*

class Main {
    fun main(args: Array<String>) {
        val gameData = GameData(args[0], args[1])
        val player1 = Pair(PlayerTurn.PLAYER_1, getPlayer(args[0], gameData))
        val player2 = Pair(PlayerTurn.PLAYER_2, getPlayer(args[1], gameData))
        val game = Game(mapOf(player1, player2))
        val generator = RandomWithTracker(Random().nextLong())
        val initGameState = GameStateFactory.createNewGameState()
        game.mainLoop(initGameState, generator)
    }

    private fun getPlayer(playerClass: String, gameData: GameData): Player {
        return when (playerClass) {
            "MctsHighestScore" -> MctsHighestScore("MCTS(HS)", gameData, "mcts.log.json")
            "RandomPlayer" -> RandomPlayer("Random", gameData)
            else -> throw Exception("Unknown player controller " + playerClass)
        }
    }
}