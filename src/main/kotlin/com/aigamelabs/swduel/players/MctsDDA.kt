package com.aigamelabs.swduel.players

import com.aigamelabs.mcts.*
import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.enums.PlayerTurn

class MctsDDA(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        logFileName: String? = null
) : MctsBasedBot(
        player,
        playerId,
        gameId,
        gameData,
        ActionSelection.get(ActionSelector.HIGHEST_SCORE),
        NodeScoreMapping.get(NodeScoreMapper.DISTANCE_FROM_MIDPOINT),
        NodeScoreMapping.get(NodeScoreMapper.IDENTITY),
        StateEvaluation.getVictoryEvaluator(player),
        StateEvaluation.getVictoryEvaluator(player.opponent()),
        logFileName
)