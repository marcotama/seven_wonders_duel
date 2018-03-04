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
import io.vavr.collection.List
import io.vavr.collection.Vector
import java.util.*
import kotlin.collections.HashMap

class KeyboardPlayer(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        outPath: String? = null
) : Player<GameState>(playerId, gameData) {
    private val asciiBoard = HashMap<Pair<Int,Int>,BoardTile>()
    private val scanner = Scanner(System.`in`)
    private var movePenguinAction: MovePenguin? = null

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
                    options.forEachIndexed { indx, action ->
                        action as MovePenguin
                        if (move.location == action.location) {
                            movePenguinAction = null
                            return action
                        }
                    }
                } else {
                    throw Exception ("Move cache was empty, this should not happen")
                }
            }
            options[0] is PlacePenguin -> {
                printBoard(gameState, null)
                while (true) {

                    println("Where would you like to place your penguin:")
                    var selection = readLine()

                    if (selection!!.length == 2) {
                        var coords = selection.toCharArray()
                        var rowCoord = rowInt(coords[0])


                        var temp = "" + coords[1]
                        var colCoord = temp.toInt() - 1


                        var location = evenr_to_cube(rowCoord, colCoord)

                        System.out.println("X " + location.first + "y " + location.second + "Z " + location.third)
                        options.forEach { action ->
                            var validPlacementAction = action as PlacePenguin
                            if (validPlacementAction.compareLocation(location)) {
                                return action
                            }
                        }
                    }
                }
            }
            else -> {
                throw Exception("Something didnt work please come back later")
            }
        }
    throw Exception ("Something didnt work please come back later")
    }

/*        printBoard(gameState, null)

        println("Decide one of the following options:")
        println("  0. Run MCTS and print analysis")
        options.forEachIndexed { idx, action ->
            println("  ${idx+1}. $action")
        }
        println(gameState.toString())
        var choice = readInt(0, options.size())
        if (choice == 0) {
            manager.run(gameState)
            println("MCTS analysis:")
            println(manager.rootNode!!)
            choice = readInt(1, options.size())
        }
        return options[choice - 1]*/

    private fun selectAMove(gameState: GameState, selectedPenguin: Action<GameState>): Boolean {
        var choosenPenguin = selectedPenguin as ChoosePenguin
        var playerTurn = choosenPenguin.getPlayerTurn()

        gameState.penguins.forEach {
            if (playerTurn == it._1) {
                it._2.forEach { penguinLocations ->
                    if (choosenPenguin.penguinId == penguinLocations._1) {
                        var choosenPenguinLocation = penguinLocations._2
                        var listOfLocations = gameState.getAvailableDestinations(choosenPenguinLocation)
                        printBoard(gameState, listOfLocations)
                        var valid = false

                        while (!valid) {
                            println("Enter 0 to select a new penguin to move or a title to move the currently selected penguin:")
                            var selection = readLine()
                            if (selection!!.length == 1) {
                                if (selection.toInt() == 0)
                                    return false
                            }
                            else if (selection!!.length < 3 && !selection!!.isEmpty())
                            {

                                var coords = selection.toCharArray()
                                var rowCoord = rowInt(coords[0])
                                var temp = "" + coords[1]
                                var colCoord = temp.toInt() - 1
                                var location = evenr_to_cube(rowCoord, colCoord)


                                listOfLocations.forEach {
                                    if (it.first == location.first && it.second == location.second && it.third == location.third) {
                                        movePenguinAction = MovePenguin(playerTurn, choosenPenguin.penguinId, location)
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
        println("Choose a penguin to move:")
        var valid = false
        var selectionIndex: Int = -1
        while (!valid) {
            var choice = readLine()
            if (choice!!.isNotEmpty() && choice!!.length < 2 && stringComparePenguinID(choice)) {
                options.forEachIndexed { idx, option ->
                    var realPenguins = option as ChoosePenguin
                    if (penguinToString(realPenguins.penguinId).equals(choice, ignoreCase = true)) {
                        selectionIndex = idx
                        valid = true
                    }
                }
            }
        }
        return options[selectionIndex]
    }


    private fun printBoard(gameState: GameState, listOfLocations: List<Triple<Int, Int, Int>>?) {
        val printer = LargePointyAsciiHexPrinter()
        val board = AsciiBoard(0, 7, 0, 7, printer)

        gameState.board.forEach {
            val findPenguinOnTile = gameState.findPenguinOnTile(it._1)
            val tileLocation = cube_to_evenr(it._1)
            val row = tileLocation.second
            val col = if (tileLocation.second % 2 == 0) tileLocation.first + 1 else tileLocation.first
            val penguin =  if (findPenguinOnTile != null) penguinToString(findPenguinOnTile.second) else ""
            val lineOne = if (it._2 != BoardTile.EATEN_TILE) rowString(row) + col else ""
            val lineTwo =  if (it._2 != BoardTile.EATEN_TILE) numberOfFishString(it._2) else ""
            var fill = ' '


            listOfLocations?.forEach { listOfLocationsIt ->
                if (listOfLocationsIt == it._1)
                    fill = '.'
            }
            if (findPenguinOnTile != null)
                fill = playerChar(findPenguinOnTile.first)


            board.printHex(lineOne,lineTwo, penguin,fill, col, row)

        }
        System.out.print(board.prettPrint(false))

    }

    private fun cube_to_evenr(locations: Triple<Int, Int, Int>?): Pair<Int,Int> {
        var col = locations!!.first + (locations!!.third + (locations!!.third and 1)) / 2
        var row = locations.third
        return Pair(col, row)
    }

    private fun evenr_to_cube(row: Int, col: Int): Triple<Int,Int,Int>{
        var x = col - (row - (row and 1)) / 2
        var z = row
        var y = -x-z
        return Triple<Int,Int,Int>(x,y,z)
    }

    private fun readInt(inclusiveLowerBound: Int, inclusiveUpperBound: Int): Int {
        do {
            try {
                val value = scanner.nextInt()
                if (value in inclusiveLowerBound..inclusiveUpperBound)
                    return value
            } catch (e: NoSuchElementException) {}
        } while (true)
    }

   private  fun rowString(num: Int): String {
        when(num) {
            0 -> return "A"
            1 -> return "B"
            2 -> return "C"
            3 -> return "D"
            4 -> return "E"
            5 -> return "F"
            6 -> return "G"
            7 -> return "H"
            else -> return "FUCK"
        }
    }

    private fun rowInt (char: Char): Int{
        return when(char) {
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

    private fun playerString(playerTurn: PlayerTurn): String{
        when(playerTurn) {
            PlayerTurn.PLAYER_1 -> return "P1"
            PlayerTurn.PLAYER_2 -> return "P2"
            PlayerTurn.PLAYER_3 -> return "P3"
            PlayerTurn.PLAYER_4 -> return "P4"
            else -> return "FUCK"
        }
    }

    private fun playerChar(playerTurn: PlayerTurn): Char {
        when(playerTurn) {
            PlayerTurn.PLAYER_1 -> return '1'
            PlayerTurn.PLAYER_2 -> return '2'
            PlayerTurn.PLAYER_3 -> return '3'
            PlayerTurn.PLAYER_4 -> return '4'
            else -> return '5'
        }
    }

    private fun numberOfFishString(boardTile: BoardTile): String{
        when(boardTile) {
            BoardTile.TILE_WITH_1_FISH -> return "&"
            BoardTile.TILE_WITH_2_FISH -> return "&&"
            BoardTile.TILE_WITH_3_FISH -> return "&&&"
            BoardTile.EATEN_TILE -> return "0"
            else-> return "Fuck"
        }
    }

    private fun penguinToString(penguinId: PenguinId): String{
        when(penguinId) {
            PenguinId.A -> return "A"
            PenguinId.B -> return "b"
            PenguinId.C -> return "C"
            PenguinId.D -> return "D"
            else -> return "FUCK"
        }
    }
    private fun stringComparePenguinID(selection: String): Boolean{
        return (selection.equals("a") || selection.equals("A")
                || selection.equals("b") || selection.equals("B")
                || selection.equals("c") || selection.equals("C")
                || selection.equals("d") || selection.equals("D"))
    }





    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}

/*    private fun cube_to_evenr()
    function cube_to_evenr(cube):
    col = cube.x + (cube.z + (cube.z&1)) / 2
    row = cube.z
    return Hex(col, row)

    function evenr_to_cube(hex):
    x = hex.col - (hex.row + (hex.row&1)) / 2
    z = hex.row
    y = -x-z
    return Cube(x, y, z)*/
}