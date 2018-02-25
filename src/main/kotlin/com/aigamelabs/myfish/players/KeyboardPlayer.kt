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
import java.util.*

class KeyboardPlayer(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        outPath: String? = null
) : Player<GameState>(playerId, gameData) {
    private val scanner = Scanner(System.`in`)
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
        return options[choice - 1]
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

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}