package com.aigamelabs.tictactoe.players

import com.aigamelabs.game.Action
import com.aigamelabs.game.GameData
import com.aigamelabs.game.Player
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.mcts.ActionSelection
import com.aigamelabs.mcts.ActionSelector
import com.aigamelabs.mcts.NodeScoreMapper
import com.aigamelabs.mcts.NodeScoreMapping
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import com.aigamelabs.tictactoe.GameState
import com.aigamelabs.tictactoe.actions.PlaceTile
import kotlin.math.roundToInt

class KeyboardPlayer(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        outPath: String? = null
) : Player<GameState>(playerId, gameData) {

    private var manager = UctParallelizationManager(
            player,
            ActionSelection.get(ActionSelector.HIGHEST_SCORE),
            PlayerTurn.getPlayers(gameData.controllers.size)
                    .map { Pair(it, NodeScoreMapping.get(NodeScoreMapper.IDENTITY)) }
                    .toMap(),
            PlayerTurn.getPlayers(gameData.controllers.size)
                    .map { Pair(it, StateEvaluation.getVictoryEvaluator(it)) }
                    .toMap(),
            outPath,
            false,
            gameId,
            playerId
    )

    override fun decide(gameState: GameState): Action<GameState> {
        val (_, thisDecision) = gameState.dequeDecision()
        val options = thisDecision.options

        when {
            options[0] is PlaceTile -> {
                printBoard(gameState)
                while (true) {

                    println("Where would you like to place your penguin (enter M to print MCTS analysis):")
                    val selection = readLine() ?: continue

                    if (selection == "M") {
                        printMctsAnalysis(gameState)
                        continue
                    }

                    if (selection.length != 2)
                        continue

                    val location = humanToCoords(selection)

                    options.forEach { action ->
                        action as PlaceTile
                        if (action.compareLocation(location))
                            return action
                    }
                }
            }
            else -> {
                throw Exception("The option type is unknown: ${options[0]}")
            }
        }
    }

    private fun actionToString(action: Action<GameState>): String {
        return when (action) {
            is PlaceTile -> coordsToHuman(action.location)
            else -> throw Exception("Unknown action type: $action")
        }
    }

    private fun printMctsAnalysis(gameState: GameState) {
        manager.run(gameState)
        println("MCTS analysis:")
        val rootNode = manager.rootNode!!
        val builder = StringBuilder()
        rootNode.children!!.values
                .sortedBy { -it.playersScore[rootNode.player]!! / it.games }
                .forEach {
                    if (it.games > 0) {
                        val score = (100 * it.playersScore[rootNode.player]!! / it.games).roundToInt()
                        builder.append("Victory chance $score%: ${actionToString(it.selectedAction!!)}\n")
                    }
                    else
                        builder.append("Victory chance NaN: $${actionToString(it.selectedAction!!)}\n")
                }
        print(builder.toString())
    }

    private fun getCell(gameState: GameState, row: Int, col: Int): String {
        val coords = Pair(row, col)
        val tile = gameState.board[coords].orNull
        return if (tile == null)
            coordsToHuman(coords)
        else
            "$tile "
    }

    private fun printBoard(gameState: GameState) {
        val f = { r: Int, c: Int -> getCell(gameState,r,c)}
        val s = " ${f(0,0)} | ${f(0,1)} | ${f(0,2)} \n" +
                "____|____|____\n" +
                " ${f(1,0)} | ${f(1,1)} | ${f(1,2)} \n" +
                "____|____|____\n" +
                " ${f(2,0)} | ${f(2,1)} | ${f(2,2)} \n" +
                "    |    |    \n"
        System.out.println(s)
        val w = gameState.calcWinner()
        System.out.println(w)
    }

    private fun humanToCoords(selection: String): Pair<Int,Int> {
        val rowCoord = rowInt(selection[0])
        val colCoord = selection[1] - '0'
        return Pair(rowCoord, colCoord)
    }

    private fun coordsToHuman(coords: Pair<Int,Int>): String {
        val row = rowString(coords.first)
        val col = coords.second
        return "$row$col"
    }

   private  fun rowString(num: Int): String {
       return "ABC"[num].toString()
    }

   private fun rowInt (char: Char): Int{
       return when (char) {
           'A', 'a' -> 0
           'B', 'b' -> 1
           'C', 'c' -> 2
           else -> 3
       }
   }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}

}