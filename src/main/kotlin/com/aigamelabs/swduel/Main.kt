package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.players.MctsHighestScore
import com.aigamelabs.swduel.players.RandomPlayer
import com.aigamelabs.utils.RandomWithTracker
import java.util.*

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val gameData = GameData(args[0], args[1])
            val player1 = Pair(PlayerTurn.PLAYER_1, getPlayer(args[0], gameData, args[2]))
            val player2 = Pair(PlayerTurn.PLAYER_2, getPlayer(args[1], gameData, args[2]))
            val game = Game(mapOf(player1, player2), args[2])
            val generator = RandomWithTracker(Random().nextLong())
            val initGameState = GameStateFactory.createNewGameState()
            game.mainLoop(initGameState, generator)
            System.exit(0)
        }

        private fun getPlayer(playerClass: String, gameData: GameData, logsPath: String): Player {
            return when (playerClass) {
                "MctsHighestScore" -> MctsHighestScore("MCTS(HS)", gameData, logsPath + "mcts.log.json")
                "RandomPlayer" -> RandomPlayer("Random", gameData)
                else -> throw Exception("Unknown player controller " + playerClass)
            }
        }
    }
}