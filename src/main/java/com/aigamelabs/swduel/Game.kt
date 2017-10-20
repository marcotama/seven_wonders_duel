package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn

class Game(private val players : HashMap<PlayerTurn,Player>) {
    fun mainLoop(startingGameState : GameState) {
        var gameState = startingGameState
        while (!gameState.decisionQueue.isEmpty) {

            // Dequeue decision and enqueue the next one
            val dequeueOutcome = gameState.decisionQueue.dequeue()
            val thisDecision = dequeueOutcome._1
            val nextDecision = DecisionFactory.makeTurnDecision(thisDecision.player.opponent(), gameState, true)
            val newDecisionsQueue = dequeueOutcome._2.enqueue(nextDecision)

            gameState = gameState.update(decisionQueue_ = newDecisionsQueue)

            // Query player for action
            val action = players[thisDecision.player]!!.decide(gameState, thisDecision.options)

            // Check for cheating
            if (!thisDecision.options.contains(action)) {
                throw Exception("Player cheated: selected action\n" + action +
                        "\nbut available actions are\n" + thisDecision.options)
            }

            // Process action
            gameState = action.process(gameState)

            // TODO check if the cardStructure is empty: if so, add a ChooseNextPlayer decision
            // ChooseNextPlayer.process() should take care of switching the age and calling gameState.initGamePhase()
        }
    }
}