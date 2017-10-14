package com.aigamelabs.swduel

import com.aigamelabs.swduel.Dealer
import com.aigamelabs.swduel.Player

class Game(val player1: Player, val player2: Player) {
    var dealer: Dealer = Dealer(1)
    var firstAgeDeck = DeckFactory.createFirstAgeDeck()
    var secondAgeDeck = DeckFactory.createSecondAgeDeck()
    var thirdAgeDeck = DeckFactory.createThirdAgeDeck()
    var guildsDeck = DeckFactory.createGuildsDeck()
    var wondersDeck = DeckFactory.createWondersDeck()
    var burnedCardsDeck = Deck("Burned")
    var unusedCardsDeck = Deck("Unused")

    fun makeFirstAgeGraph() {

    }
}