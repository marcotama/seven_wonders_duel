package com.aigamelabs.swduel.players

import com.aigamelabs.swduel.GameData
import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.Player
import com.aigamelabs.swduel.GameState
import java.util.*

class KeyboardPlayer(name: String, gameData: GameData) : Player(name, gameData) {
    private val scanner = Scanner(System.`in`)

    override fun decide(gameState: GameState): Action {
        val (_, thisDecision) = gameState.dequeAction()
        val options = thisDecision.options
        println("Decide one of the following options:")
        options.forEachIndexed { idx, option -> println("  ${idx+1}. $option") }
        val choice = readInt(options.size())
        return options[choice - 1]
    }

    private fun readInt(inclusiveUpperBound: Int): Int {
        do {
            try {
                val value = scanner.nextInt()
                if (value <= inclusiveUpperBound)
                    return value
            } catch (e: NoSuchElementException) {}
        } while (true)
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}