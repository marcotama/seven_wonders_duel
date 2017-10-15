package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

object DecisionFactory {
    fun makeMainTurnDecision(playerTurn: PlayerTurn) : Decision {
        return Decision(playerTurn, Vector.empty(), true) // TODO
    }
}