package com.aigamelabs.swduel

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        var firstAgedeck = CardFactory.createFromFirstAge()

        repeat(3, {
            val drawOutcome = firstAgedeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            firstAgedeck = drawOutcome.second
        })

        return firstAgedeck
    }


    fun createSecondAgeDeck() : Deck {
        var secondAgeDeck = CardFactory.createFromSecondAge()

        repeat(3, {
            val drawOutcome = secondAgeDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            secondAgeDeck = drawOutcome.second
        })

        return secondAgeDeck
    }


    fun createThirdAgeDeck() : Deck {
        var thirdAgeDeck = CardFactory.createFromThirdAge()
        var guildsDeck = CardFactory.createFromGuilds()

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
        var wondersDeck = CardFactory.createFromWonders()

        repeat(4, {
            val drawOutcome = wondersDeck.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            wondersDeck = drawOutcome.second
        })

        return wondersDeck
    }
}