package com.aigamelabs.myfish.players

import com.aigamelabs.game.Action
import com.aigamelabs.game.GameData
import com.aigamelabs.game.Player
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.mcts.ActionSelection
import com.aigamelabs.mcts.ActionSelector
import com.aigamelabs.mcts.NodeScoreMapper
import com.aigamelabs.mcts.NodeScoreMapping
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import com.aigamelabs.myfish.GameState
import com.aigamelabs.myfish.actions.ChoosePenguin
import com.aigamelabs.myfish.actions.MovePenguin
import com.aigamelabs.myfish.actions.PlacePenguin
import com.aigamelabs.myfish.enums.BoardTile
import com.aigamelabs.myfish.enums.PenguinId
import com.aigamelabs.myfish.utils.AsciiBoard
import com.aigamelabs.myfish.utils.printers.LargePointyAsciiHexPrinter
import com.aigamelabs.utils.RandomWithTracker
import io.vavr.collection.List
import io.vavr.collection.Vector
import kotlin.math.roundToInt

class KeyboardPlayer(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        outPath: String? = null
) : Player<GameState>(playerId, gameData) {
    private var movePenguinAction: MovePenguin? = null
    private var generator = RandomWithTracker(0) // actually never used, but an object of this class is required for some calls

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
            options[0] is ChoosePenguin -> {
                var valid = false
                var selectedPenguin = options[0]
                while (!valid) {
                    selectedPenguin = selectAPenguin(gameState, options)
                    valid = selectAMove(gameState, selectedPenguin)
                }
                return selectedPenguin
            }
            options[0] is MovePenguin -> {
                if (movePenguinAction != null) {
                    val move = movePenguinAction as MovePenguin
                    options.forEach { action ->
                        action as MovePenguin
                        if (move.location == action.location) {
                            movePenguinAction = null
                            return action
                        }
                    }
                    throw Exception("None of the locations matches the cached one; this should not happen because a check is done upon caching")
                } else {
                    throw Exception ("Move cache was empty, this should not happen")
                }
            }
            options[0] is PlacePenguin -> {
                printBoard(gameState, null)
                while (true) {

                    println("Where would you like to place your penguin (enter M to print MCTS analysis):")
                    val selection = readLine() ?: continue

                    if (selection == "M") {
                        printMctsAnalysis(gameState)
                        continue
                    }

                    if (selection.length != 2)
                        continue

                    val location = humanToCubeCoords(selection)

                    options.forEach { action ->
                        action as PlacePenguin
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
            is PlacePenguin -> cubeToHumanCoords(action.location)
            is MovePenguin -> cubeToHumanCoords(action.location)
            is ChoosePenguin -> action.penguinId.toString()
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

    private fun selectAMove(gameState: GameState, selectedPenguin: Action<GameState>): Boolean {
        val chosenPenguin = selectedPenguin as ChoosePenguin
        val playerTurn = chosenPenguin.getPlayerTurn()

        gameState.penguins.forEach {
            if (playerTurn == it._1) {
                it._2.forEach { penguinLocations ->
                    if (chosenPenguin.penguinId == penguinLocations._1) {
                        val chosenPenguinLocation = penguinLocations._2
                        val listOfLocations = gameState.getAvailableDestinations(chosenPenguinLocation)
                        printBoard(gameState, listOfLocations)

                        while (true) {
                            println("Enter 0 to select a new penguin, M to print MCTS analysis or a tile to move the currently selected penguin:")
                            val selection = readLine() ?: continue
                            if (selection.length == 1) {
                                when (selection) {
                                    "0" -> return false
                                    "M" -> printMctsAnalysis(gameState.applyAction(chosenPenguin, generator))
                                }
                            }
                            else if (selection.length < 3 && !selection.isEmpty()) {
                                val location = humanToCubeCoords(selection)
                                listOfLocations.forEach {
                                    if (it == location) {
                                        movePenguinAction = MovePenguin(playerTurn, chosenPenguin.penguinId, location)
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }


    private fun selectAPenguin (gameState: GameState, options: Vector<out Action<GameState>>): Action<GameState> {

        printBoard(gameState, null)
        println("Choose a penguin to move, or enter M to print the analysis by MCTS:")
        while (true) {
            val choice = readLine() ?: continue
            when {
                choice == "M" -> printMctsAnalysis(gameState)
                choice.length == 1 && isPenguinId(choice) -> {
                    options.forEachIndexed { idx, option ->
                        val realPenguins = option as ChoosePenguin
                        if (penguinToString(realPenguins.penguinId).equals(choice, ignoreCase = true)) {
                            return options[idx]
                        }
                    }
                }
            }
        }

    }


    private fun printBoard(gameState: GameState, listOfLocations: List<Triple<Int, Int, Int>>?) {
        val printer = LargePointyAsciiHexPrinter()
        val board = AsciiBoard(0, 7, 0, 7, printer)

        gameState.board.forEach {
            val findPenguinOnTile = gameState.findPenguinOnTile(it._1)
            val evenRowCoords = cubeToEvenRowCoords(it._1)
            val (row, col) = getRowAndCol(evenRowCoords)
            val penguin =  if (findPenguinOnTile != null) penguinToString(findPenguinOnTile.second) else ""
            val lineOne = if (it._2 != BoardTile.EATEN_TILE) evenRowToHumanCoords(evenRowCoords) else ""
            val lineTwo =  if (it._2 != BoardTile.EATEN_TILE) numberOfFishString(it._2) else ""
            var fill = ' '


            listOfLocations?.forEach { listOfLocationsIt ->
                if (listOfLocationsIt == it._1)
                    fill = '.'
            }
            if (findPenguinOnTile != null)
                fill = playerChar(findPenguinOnTile.first)


            board.printHex(lineOne, lineTwo, penguin, fill, col, row)

        }
        System.out.print(board.prettyPrint(false))

    }

    private fun getRowAndCol(location: Pair<Int,Int>): Pair<Int,Int> {
        val row = location.second
        val col = if (location.second % 2 == 0) location.first + 1 else location.first
        return Pair(row,col)
    }

    private fun cubeToHumanCoords(coords: Triple<Int,Int,Int>): String {
        val evenRowCoords = cubeToEvenRowCoords(coords)
        return evenRowToHumanCoords(evenRowCoords)
    }

    private fun humanToCubeCoords(selection: String): Triple<Int,Int,Int> {
        val rowCoord = rowInt(selection[0])
        val colCoord = selection[1] - '0' - 1
        return evenRowToCubeCoords(rowCoord, colCoord)
    }

    private fun evenRowToHumanCoords(coords: Pair<Int,Int>): String {
        val row = coords.second
        val col = if (coords.second % 2 == 0) coords.first + 1 else coords.first
        return "${rowString(row)}$col"
    }

    private fun cubeToEvenRowCoords(locations: Triple<Int, Int, Int>?): Pair<Int,Int> {
        val col = locations!!.first + (locations.third + (locations.third and 1)) / 2
        val row = locations.third
        return Pair(col, row)
    }

    private fun evenRowToCubeCoords(row: Int, col: Int): Triple<Int,Int,Int>{
        val x = col - (row - (row and 1)) / 2
        val z = row
        val y = -x - z
        return Triple(x, y, z)
    }

   private  fun rowString(num: Int): String {
       return "ABCDEFGH"[num].toString()
    }

   private fun rowInt (char: Char): Int{
       return when (char) {
           'A', 'a' -> 0
           'B', 'b' -> 1
           'C', 'c' -> 2
           'D', 'd' -> 3
           'E', 'e' -> 4
           'F', 'f' -> 5
           'G', 'g' -> 6
           'H', 'h' -> 7
           else -> 8
       }
   }

    private fun playerChar(playerTurn: PlayerTurn): Char {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1 -> '1'
            PlayerTurn.PLAYER_2 -> '2'
            PlayerTurn.PLAYER_3 -> '3'
            PlayerTurn.PLAYER_4 -> '4'
            else -> throw Exception("Player $playerTurn is not supposed to be playing")
        }
    }

    private fun numberOfFishString(boardTile: BoardTile): String{
        return when (boardTile) {
            BoardTile.TILE_WITH_1_FISH -> "&"
            BoardTile.TILE_WITH_2_FISH -> "&&"
            BoardTile.TILE_WITH_3_FISH -> "&&&"
            BoardTile.EATEN_TILE -> "0"
        }
    }

    private fun penguinToString(penguinId: PenguinId): String{
        return when (penguinId) {
            PenguinId.A -> "A"
            PenguinId.B -> "B"
            PenguinId.C -> "C"
            PenguinId.D -> "D"
        }
    }
    private fun isPenguinId(selection: String): Boolean {
        return "ABCDabcd".contains(selection)
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}

}