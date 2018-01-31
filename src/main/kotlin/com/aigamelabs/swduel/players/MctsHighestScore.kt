package com.aigamelabs.swduel.players

import com.aigamelabs.mcts.actionselection.HighestScore
import com.aigamelabs.mcts.nodeevaluation.GameVictory
import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.enums.PlayerTurn

class MctsHighestScore(
        playerId: String,
        gameId: String,
        gameData: GameData,
        logFileName: String? = null
) : MctsBasedBot(
        playerId,
        gameId,
        gameData,
        HighestScore(),
        GameVictory(PlayerTurn.PLAYER_1),
        GameVictory(PlayerTurn.PLAYER_2),
        logFileName
)