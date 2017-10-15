package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.GamePhase
import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.enums.ProgressToken
import io.vavr.collection.HashSet

data class GameState (
        val firstAgeDeck : Deck,
        val secondAgeDeck : Deck,
        val thirdAgeDeck : Deck,
        val wondersDeck : Deck,
        val burnedCardsDeck : Deck,
        val currentGraph : Graph<Card>?,
        val progressTokens : HashSet<ProgressToken>,
        val militaryBoard: MilitaryBoard,
        val player1City : PlayerCity,
        val player2City : PlayerCity,
        val gamePhase: GamePhase,
        val playerTurn: PlayerTurn
)