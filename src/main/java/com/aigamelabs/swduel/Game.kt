package com.aigamelabs.swduel

import com.aigamelabs.swduel.Dealer
import com.aigamelabs.swduel.Player

class Game(val player1: Player, val player2: Player) {
    var dealer: Dealer = Dealer(1)
    var firstAgeDeck : Deck = DeckFactory.createFirstAgeDeck()
    var secondAgeDeck : Deck = DeckFactory.createSecondAgeDeck()
    var thirdAgeDeck : Deck = DeckFactory.createThirdAgeDeck()
    var guildsDeck : Deck = DeckFactory.createGuildsDeck()
    var wondersDeck : Deck = DeckFactory.createWondersDeck()

    fun makeFirstAgeGraph() {

    }
}