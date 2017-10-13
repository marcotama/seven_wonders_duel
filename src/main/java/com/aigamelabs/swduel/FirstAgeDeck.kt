package com.aigamelabs.swduel

import io.vavr.kotlin.toVavrList
import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.enums.*

class FirstAgeDeck() : Deck(DeckName.FIRST_AGE) {
    init {
        val cards_mutable = ArrayList<Card>()

        cards_mutable.add(Card(deck = this.deckName, name = "Lumber yard", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.WOOD to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Clay pool", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.CLAY to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Quarry", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.STONE to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Logging camp", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.WOOD to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Clay pit", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.CLAY to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Stone pit", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.STONE to 1)))

        cards_mutable.add(Card(deck = this.deckName, name = "Glassworks", color = CardColor.GRAY, coinCost = 1, resourcesProduced = hashMapOf(Resource.GLASS to 1)))
        cards_mutable.add(Card(deck = this.deckName, name = "Press", color = CardColor.GRAY, coinCost = 1, resourcesProduced = hashMapOf(Resource.PAPER to 1)))

        cards_mutable.add(Card(deck = this.deckName, name = "Pharmacist", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.GEAR, linksTo = LinkingSymbol.NONE, victoryPoints = 0, scienceSymbol = ScienceSymbol.MORTAR_AND_PESTEL))
        cards_mutable.add(Card(deck = this.deckName, name = "Scriptorium", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.BOOK, linksTo = LinkingSymbol.NONE, victoryPoints = 0, scienceSymbol = ScienceSymbol.QUILL_AND_INKPEN))
        cards_mutable.add(Card(deck = this.deckName, name = "Workshop", resourceCost = mapOf(Resource.PAPER to 1), coinCost = 2, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.INCLINOMETER))
        cards_mutable.add(Card(deck = this.deckName, name = "Apothecary", resourceCost = mapOf(Resource.GLASS to 1), coinCost = 2, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.WHEEL))

        cards_mutable.add(Card(deck = this.deckName, name = "Baths", resourceCost = mapOf(Resource.STONE to 1), linkingSymbol = LinkingSymbol.DROP, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards_mutable.add(Card(deck = this.deckName, name = "Altar", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MOON, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards_mutable.add(Card(deck = this.deckName, name = "Theater", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MASK, linksTo = LinkingSymbol.NONE, victoryPoints = 3))

        cards_mutable.add(Card(deck = this.deckName, name = "Palisade", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.TOWER, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(deck = this.deckName, name = "Stable", resourceCost = mapOf(Resource.WOOD to 1), coinCost = 0, linkingSymbol = LinkingSymbol.HORSESHOE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(deck = this.deckName, name = "Garrison", resourceCost = mapOf(Resource.CLAY to 1), coinCost = 0, linkingSymbol = LinkingSymbol.SWORD, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(deck = this.deckName, name = "Guard tower", resourceCost = emptyMap(), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))

        cards_mutable.add(Card(deck = this.deckName, name = "Wood reserve", coinCost = 3, tradingBonuses = setOf(Resource.WOOD)))
        cards_mutable.add(Card(deck = this.deckName, name = "Clay reserve", coinCost = 3, tradingBonuses = setOf(Resource.CLAY)))
        cards_mutable.add(Card(deck = this.deckName, name = "Stone reserve", coinCost = 3, tradingBonuses = setOf(Resource.STONE)))

        cards_mutable.add(Card(deck = this.deckName, name = "Tavern", coinsProduced = 4, linkingSymbol = LinkingSymbol.VASE))

        cards = cards_mutable.toVavrList()

    }
}