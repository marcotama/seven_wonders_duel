package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.players.KeyboardPlayer
import com.aigamelabs.swduel.players.MctsHighestScore
import com.aigamelabs.swduel.players.RandomPlayer
import com.aigamelabs.utils.RandomWithTracker
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val gameId = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Calendar.getInstance().time)
            val gameData = GameData(args[0], args[1])
            val player1 = Pair(PlayerTurn.PLAYER_1, getPlayer(args[0], gameData, gameId, args[2]))
            val player2 = Pair(PlayerTurn.PLAYER_2, getPlayer(args[1], gameData, gameId, args[2]))
            val game = Game(gameId, mapOf(player1, player2), args[2])
            val generator = RandomWithTracker(Random().nextLong(), true)
            val initGameState = if (args.size > 3) {
                val content = readFile(args[3], Charset.defaultCharset())
                GameState.loadFromJson(JSONObject(content))
            }
            else {
                GameStateFactory.createNewGameState(generator)
            }
            generator.popAll()
            game.mainLoop(initGameState, generator)
            System.exit(0)
        }

        @Throws(IOException::class)
        // From https://stackoverflow.com/a/326440
        private fun readFile(path: String, encoding: Charset): String {
            val encoded = Files.readAllBytes(Paths.get(path))
            return String(encoded, encoding)
        }

        private fun getPlayer(playerClass: String, gameData: GameData, gameId: String, logsPath: String): Player {
            return when (playerClass) {
                "MctsHighestScore" -> MctsHighestScore(PlayerTurn.PLAYER_1, "MCTS(HS)", gameId, gameData, logsPath)
                "RandomPlayer" -> RandomPlayer("Random", gameData)
                "KeyboardPlayer" -> KeyboardPlayer("Keyboard", gameData)
                else -> throw Exception("Unknown player controller " + playerClass)
            }
        }
    }
}