package com.aigamelabs.swduel.players

import com.aigamelabs.mcts.actionselection.HighestScore
import com.aigamelabs.mcts.nodeevaluation.DistanceFromHalfUnit
import com.aigamelabs.mcts.stateevaluation.GameVictory
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
        HighestScore(),
        DistanceFromHalfUnit(),
        null,
        GameVictory(player),
        GameVictory(player.opponent()),
        logFileName
)