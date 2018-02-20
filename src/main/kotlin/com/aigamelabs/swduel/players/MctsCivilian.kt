package com.aigamelabs.swduel.players

import com.aigamelabs.mcts.*
import com.aigamelabs.game.GameData
import com.aigamelabs.game.PlayerTurn
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
        NodeScoreMapping.get(NodeScoreMapper.IDENTITY),
        NodeScoreMapping.get(NodeScoreMapper.IDENTITY),
        StateEvaluation.getCivilianVictoryEvaluator(player),
        StateEvaluation.getCivilianVictoryEvaluator(player.opponent()),
        logFileName
)