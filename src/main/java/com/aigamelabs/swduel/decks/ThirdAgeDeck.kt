package com.aigamelabs.swduel.decks

import com.aigamelabs.swduel.enums.Formula
import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Deck
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.Vector

class ThirdAgeDeck : Deck(DeckName.THIRD_AGE, create()) {

    companion object Factory {
        fun create() : Vector<Card> {
            val deckName = DeckName.FIRST_AGE
            val cards_mutable = ArrayList<Card>()
    
            cards_mutable.add(Card(deck = deckName, name = "Observatory", resourceCost = hashMapOf(Resource.STONE to 1, Resource.PAPER to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.LAMP, victoryPoints = 2, scienceSymbol = ScienceSymbol.ARMILLARY_SPHERE))
            cards_mutable.add(Card(deck = deckName, name = "University", resourceCost = hashMapOf(Resource.CLAY to 1, Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.BOOK, linksTo = LinkingSymbol.LYRE, victoryPoints = 2, scienceSymbol = ScienceSymbol.ARMILLARY_SPHERE))
            cards_mutable.add(Card(deck = deckName, name = "Academy", resourceCost = hashMapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LYRE, linksTo = LinkingSymbol.NONE, victoryPoints = 3, scienceSymbol = ScienceSymbol.SUNDIAL))
            cards_mutable.add(Card(deck = deckName, name = "Study", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.LAMP, linksTo = LinkingSymbol.NONE, victoryPoints = 3, scienceSymbol = ScienceSymbol.SUNDIAL))
    
            cards_mutable.add(Card(deck = deckName, name = "Senate", resourceCost = mapOf(Resource.CLAY to 2, Resource.STONE to 1, Resource.PAPER to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TEMPLE, victoryPoints = 5))
            cards_mutable.add(Card(deck = deckName, name = "Obelisk", resourceCost = mapOf(Resource.STONE to 2, Resource.GLASS to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 5))
            cards_mutable.add(Card(deck = deckName, name = "Pantheon", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.PAPER to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.SUN, victoryPoints = 6))
            cards_mutable.add(Card(deck = deckName, name = "Gardens", resourceCost = mapOf(Resource.CLAY to 2, Resource.WOOD to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.COLUMN, victoryPoints = 6))
            cards_mutable.add(Card(deck = deckName, name = "Palace", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 7))
            cards_mutable.add(Card(deck = deckName, name = "Town hall", resourceCost = mapOf(Resource.STONE to 3, Resource.WOOD to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 7))
    
            cards_mutable.add(Card(deck = deckName, name = "Circus", resourceCost = mapOf(Resource.CLAY to 2, Resource.STONE to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.HELMET, militaryPoints = 2))
            cards_mutable.add(Card(deck = deckName, name = "Fortifications", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TOWER, militaryPoints = 2))
            cards_mutable.add(Card(deck = deckName, name = "Siege workshop", resourceCost = mapOf(Resource.WOOD to 3, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TARGET, militaryPoints = 2))
            cards_mutable.add(Card(deck = deckName, name = "Pretorium", resourceCost = emptyMap(), coinCost = 8, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 3))
            cards_mutable.add(Card(deck = deckName, name = "Arsenal", resourceCost = mapOf(Resource.CLAY to 3, Resource.WOOD to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 3))
    
            cards_mutable.add(Card(deck = deckName, name = "Port", resourceCost = mapOf(Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPER to 1), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 2, coinsProducedFormula = Formula.PER_BROWN_CARD))
            cards_mutable.add(Card(deck = deckName, name = "Chamber of commerce", resourceCost = mapOf(Resource.PAPER to 2), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 3, coinsProducedFormula = Formula.PER_GRAY_CARD))
            cards_mutable.add(Card(deck = deckName, name = "Armory", resourceCost = mapOf(Resource.STONE to 2, Resource.GLASS to 1), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 1, coinsProducedFormula = Formula.PER_RED_CARD))
            cards_mutable.add(Card(deck = deckName, name = "Lighthouse", resourceCost = mapOf(Resource.CLAY to 2, Resource.GLASS to 1), linksTo = LinkingSymbol.VASE, victoryPoints = 3, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
            cards_mutable.add(Card(deck = deckName, name = "Arena", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1), linksTo = LinkingSymbol.BARREL, victoryPoints = 3, coinsProduced = 2, coinsProducedFormula = Formula.PER_WONDER))
    
            return Vector.ofAll(cards_mutable)
        }
    }
}