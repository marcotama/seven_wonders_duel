package com.aigamelabs.swduel

import java.util.Random

object DeckFactory {

    fun createFirstAgeDeck(generator: Random? = null) : Deck {
        var firstAgeDeck = Deck("First Age", CardFactory.createFromFirstAge())
        val drawOutcome = firstAgeDeck.drawCards(3, generator)
        firstAgeDeck = drawOutcome.second

        return firstAgeDeck
    }


    fun createSecondAgeDeck(generator: Random? = null) : Deck {
        var secondAgeDeck = Deck("Second Age", CardFactory.createFromSecondAge())
        val drawOutcome = secondAgeDeck.drawCards(3, generator)
        secondAgeDeck = drawOutcome.second

        return secondAgeDeck
    }


    fun createThirdAgeDeck(generator: Random? = null) : Deck {
        var thirdAgeDeck = Deck("Third Age", CardFactory.createFromThirdAge())
        val draw1Outcome = thirdAgeDeck.drawCards(3, generator)
        thirdAgeDeck = draw1Outcome.second

        var guildsDeck = Deck("Guilds", CardFactory.createFromGuilds())
        val draw2Outcome = guildsDeck.drawCards(4, generator)
        guildsDeck = draw2Outcome.second

        return thirdAgeDeck.merge(guildsDeck)
    }


    fun createWondersDeck(generator: Random? = null) : Deck {
        var wondersDeck = Deck("Wonders", CardFactory.createFromWonders())
        val drawOutcome = wondersDeck.drawCards(4, generator)
        wondersDeck = drawOutcome.second

        return wondersDeck
    }

    fun createScienceTokenDeck () : Deck {
        return Deck("Science Tokens", CardFactory.createFromScience())
    }
}