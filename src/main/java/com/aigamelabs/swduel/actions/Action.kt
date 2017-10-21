package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.PlayerTurn
import java.util.Random

abstract class Action (val playerTurn: PlayerTurn) {
    abstract fun process(gameState: GameState, generator : Random) : GameState
}