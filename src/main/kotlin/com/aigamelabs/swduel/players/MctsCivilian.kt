package com.aigamelabs.swduel.players

import com.aigamelabs.game.GameData
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.mcts.*
import com.aigamelabs.swduel.GameState

class MctsCivilian(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        logFileName: String? = null
) : MctsBasedBot<GameState>(
        player,
        playerId,
        gameId,
        gameData,
        ActionSelection.get(ActionSelector.HIGHEST_SCORE),
        PlayerTurn.getPlayers(gameData.controllers.size)
                .map { Pair(it, NodeScoreMapping.get(NodeScoreMapper.IDENTITY)) }
                .toMap(),
        PlayerTurn.getPlayers(gameData.controllers.size)
                .map {
                    if (player == it)
                        Pair(it, StateEvaluation.getCivilianVictoryEvaluator(it))
                    else
                        Pair(it, StateEvaluation.getVictoryEvaluator(it))
                }
                .toMap(),
        logFileName
)