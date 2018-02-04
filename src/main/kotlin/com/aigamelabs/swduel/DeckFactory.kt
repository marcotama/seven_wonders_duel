package com.aigamelabs.swduel

import io.vavr.collection.Vector

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        val firstAgeCards = CardFactory.createFromFirstAge()
        return Deck("First Age", Vector.of(Pair(firstAgeCards, 3)))
    }

    fun createSecondAgeDeck() : Deck {
        val secondAgeCards = CardFactory.createFromSecondAge()
        return Deck("Second Age", Vector.of(Pair(secondAgeCards, 3)))
    }

    fun createThirdAgeDeck() : Deck {
        val thirdAgeCards = CardFactory.createFromThirdAge()
        val guildAgeCards = CardFactory.createFromGuilds()
        return Deck("Third Age", Vector.of(Pair(thirdAgeCards, 3), Pair(guildAgeCards, 4)))
    }

    fun createWondersDeck() : Deck {
        val wondersCards = CardFactory.createFromWonders()
        return Deck("Wonders", Vector.of(Pair(wondersCards, 0)))
    }

    fun createProgressTokensDeck() : Deck {
        val progressTokens = CardFactory.createFromScience()
        return Deck("Progress Tokens", Vector.of(Pair(progressTokens, 0)))
    }
}