package com.aigamelabs.swduel

import com.aigamelabs.utils.Deck
import io.vavr.collection.Vector

object DeckFactory {

    fun createFirstAgeDeck() : Deck<Card> {
        val firstAgeCards = CardFactory.firstAge
        return Deck("First Age", Vector.of(Pair(firstAgeCards, 3)))
    }

    fun createSecondAgeDeck() : Deck<Card> {
        val secondAgeCards = CardFactory.secondAge
        return Deck("Second Age", Vector.of(Pair(secondAgeCards, 3)))
    }

    fun createThirdAgeDeck() : Deck<Card> {
        val thirdAgeCards = CardFactory.thirdAge
        val guildAgeCards = CardFactory.guilds
        return Deck("Third Age", Vector.of(Pair(thirdAgeCards, 3), Pair(guildAgeCards, 4)))
    }

    fun createWondersDeck() : Deck<Card> {
        val wondersCards = CardFactory.wonders
        return Deck("Wonders", Vector.of(Pair(wondersCards, 0)))
    }

    fun createProgressTokensDeck() : Deck<Card> {
        val progressTokens = CardFactory.progressTokens
        return Deck("Progress Tokens", Vector.of(Pair(progressTokens, 0)))
    }
}