package com.aigamelabs.swduel

import com.aigamelabs.swduel.Deck
import com.aigamelabs.swduel.enums.Formula
import com.aigamelabs.swduel.enums.*

class GuildsDeck() : Deck(DeckName.GUILDS) {
    init {
        val cards = ArrayList<Card>()

        cards.add(Card(deck = this.deckName, name = "Merchants guild", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_GOLD_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards.add(Card(deck = this.deckName, name = "Scientists guild", resourceCost = mapOf(Resource.CLAY to 2, Resource.WOOD to 2), victoryPoints = 1, victoryPointsFormula = Formula.PER_GREEN_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards.add(Card(deck = this.deckName, name = "Tacticians guild", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_RED_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards.add(Card(deck = this.deckName, name = "Magistrates guild", resourceCost = mapOf(Resource.WOOD to 2, Resource.CLAY to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_BLUE_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards.add(Card(deck = this.deckName, name = "Shipowners guild", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_BROWN_AND_GRAY_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards.add(Card(deck = this.deckName, name = "Builders guild", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1), victoryPoints = 2, victoryPointsFormula = Formula.PER_WONDER, coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE))
        cards.add(Card(deck = this.deckName, name = "Moneylenders guild", resourceCost = mapOf(Resource.STONE to 2, Resource.WOOD to 2), victoryPoints = 1, victoryPointsFormula = Formula.PER_THREE_COINS, coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE))
    }
}