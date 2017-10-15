package com.aigamelabs.swduel

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        var firstAgeDeck = Deck("First Age", CardFactory.createFromFirstAge())
        val drawOutcome = firstAgeDeck.drawCards(3)!!
        firstAgeDeck = drawOutcome.second

        return firstAgeDeck
    }


    fun createSecondAgeDeck() : Deck {
        var secondAgeDeck = Deck("Second Age", CardFactory.createFromSecondAge())
        val drawOutcome = secondAgeDeck.drawCards(3)!!
        secondAgeDeck = drawOutcome.second

        return secondAgeDeck
    }


    fun createThirdAgeDeck() : Deck {
        var thirdAgeDeck = Deck("Third Age", CardFactory.createFromThirdAge())
        val draw1Outcome = thirdAgeDeck.drawCards(3)!!
        thirdAgeDeck = draw1Outcome.second

        var guildsDeck = Deck("Guilds", CardFactory.createFromGuilds())
        val draw2Outcome = guildsDeck.drawCards(3)!!
        guildsDeck = draw2Outcome.second

        return thirdAgeDeck.addAll(guildsDeck)
    }


    fun createWondersDeck() : Deck {
        var wondersDeck = Deck("Wonders", CardFactory.createFromWonders())
        val drawOutcome = wondersDeck.drawCards(4)!!
        wondersDeck = drawOutcome.second

        return wondersDeck
    }
}