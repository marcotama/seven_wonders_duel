package com.aigamelabs.swduel

import io.vavr.collection.Vector

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        val firstAgeCards = CardFactory.firstAge
        return Deck("First Age", Vector.of(Pair(firstAgeCards, 3)))
    }

    fun createSecondAgeDeck() : Deck {
        val secondAgeCards = CardFactory.secondAge
        return Deck("Second Age", Vector.of(Pair(secondAgeCards, 3)))
    }

    fun createThirdAgeDeck() : Deck {
        val thirdAgeCards = CardFactory.thirdAge
        val guildAgeCards = CardFactory.guilds
        return Deck("Third Age", Vector.of(Pair(thirdAgeCards, 3), Pair(guildAgeCards, 4)))
    }

    fun createWondersDeck() : Deck {
        val wondersCards = CardFactory.wonders
        return Deck("Wonders", Vector.of(Pair(wondersCards, 0)))
    }

    fun createProgressTokensDeck() : Deck {
        val progressTokens = CardFactory.progressTokens
        return Deck("Progress Tokens", Vector.of(Pair(progressTokens, 0)))
    }
}