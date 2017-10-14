package com.aigamelabs.swduel

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        var firstAgeDeck = Deck("First Age", CardFactory.createFromFirstAge())

        repeat(3, {
            val drawOutcome = firstAgeDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            firstAgeDeck = drawOutcome.second
        })

        return firstAgeDeck
    }


    fun createSecondAgeDeck() : Deck {
        var secondAgeDeck = Deck("Second Age", CardFactory.createFromSecondAge())

        repeat(3, {
            val drawOutcome = secondAgeDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            secondAgeDeck = drawOutcome.second
        })

        return secondAgeDeck
    }


    fun createThirdAgeDeck() : Deck {
        var thirdAgeDeck = Deck("Third Age", CardFactory.createFromThirdAge())
        var guildsDeck = Deck("Guilds", CardFactory.createFromGuilds())

        repeat(3, {
            val drawOutcome = thirdAgeDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            thirdAgeDeck = drawOutcome.second
        })
        repeat(3, {
            val drawOutcome = guildsDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing guild cards") })
            guildsDeck = drawOutcome.second
        })

        return thirdAgeDeck.addAll(guildsDeck)
    }


    fun createWondersDeck() : Deck {
        var wondersDeck = Deck("Wonders", CardFactory.createFromWonders())

        repeat(4, {
            val drawOutcome = wondersDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            wondersDeck = drawOutcome.second
        })

        return wondersDeck
    }
}