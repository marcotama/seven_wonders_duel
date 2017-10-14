package com.aigamelabs.swduel.decks

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.Deck
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.Vector

class SecondAgeDeck() : Deck(DeckName.SECOND_AGE, create()) {

    companion object Factory {
        fun create() : Vector<Card> {
            val deckName = DeckName.FIRST_AGE
            val cards_mutable = ArrayList<Card>()

            cards_mutable.add(Card(deck = deckName, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2)))
            cards_mutable.add(Card(deck = deckName, name = "Brickyard", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.CLAY to 2)))
            cards_mutable.add(Card(deck = deckName, name = "Shelf quarry", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.STONE to 2)))

            cards_mutable.add(Card(deck = deckName, name = "Glass-blower", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.GLASS to 1)))
            cards_mutable.add(Card(deck = deckName, name = "Drying room", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.PAPER to 1)))

            cards_mutable.add(Card(deck = deckName, name = "Dispensary", resourceCost = hashMapOf(Resource.CLAY to 2, Resource.STONE to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.GEAR, victoryPoints = 2, scienceSymbol = ScienceSymbol.MORTAR_AND_PESTEL))
            cards_mutable.add(Card(deck = deckName, name = "Library", resourceCost = hashMapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.BOOK, linksTo = LinkingSymbol.NONE, victoryPoints = 2, scienceSymbol = ScienceSymbol.QUILL_AND_INKPEN))
            cards_mutable.add(Card(deck = deckName, name = "School", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.PAPER to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LYRE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.WHEEL))
            cards_mutable.add(Card(deck = deckName, name = "Laboratory", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.GLASS to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LAMP, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.INCLINOMETER))

            cards_mutable.add(Card(deck = deckName, name = "Temple", resourceCost = mapOf(Resource.WOOD to 1, Resource.PAPER to 1), linkingSymbol = LinkingSymbol.SUN, linksTo = LinkingSymbol.MOON, victoryPoints = 4))
            cards_mutable.add(Card(deck = deckName, name = "Statue", resourceCost = mapOf(Resource.CLAY to 2), linkingSymbol = LinkingSymbol.COLUMN, linksTo = LinkingSymbol.MASK, victoryPoints = 4))
            cards_mutable.add(Card(deck = deckName, name = "Rostrum", resourceCost = mapOf(Resource.STONE to 1, Resource.WOOD to 1), linkingSymbol = LinkingSymbol.TEMPLE, linksTo = LinkingSymbol.NONE, victoryPoints = 4))
            cards_mutable.add(Card(deck = deckName, name = "Courthouse", resourceCost = mapOf(Resource.WOOD to 2, Resource.GLASS to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 5))
            cards_mutable.add(Card(deck = deckName, name = "Aqueduct", resourceCost = mapOf(Resource.STONE to 3), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.DROP, victoryPoints = 5))

            cards_mutable.add(Card(deck = deckName, name = "Barracks", resourceCost = emptyMap(), coinCost = 3, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.SWORD, militaryPoints = 1))
            cards_mutable.add(Card(deck = deckName, name = "Horse breeders", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.HORSESHOE, militaryPoints = 1))
            cards_mutable.add(Card(deck = deckName, name = "Walls", resourceCost = mapOf(Resource.STONE to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 2))
            cards_mutable.add(Card(deck = deckName, name = "Parade ground", resourceCost = mapOf(Resource.CLAY to 2, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.HELMET, linksTo = LinkingSymbol.NONE, militaryPoints = 2))
            cards_mutable.add(Card(deck = deckName, name = "Archery range", resourceCost = mapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.TARGET, linksTo = LinkingSymbol.NONE, militaryPoints = 2))

            cards_mutable.add(Card(deck = deckName, name = "Customs house", coinCost = 4, tradingBonuses = setOf(Resource.PAPER, Resource.GLASS)))

            cards_mutable.add(Card(deck = deckName, name = "Brewery", coinsProduced = 6, linkingSymbol = LinkingSymbol.BARREL))

            cards_mutable.add(Card(deck = deckName, name = "Caravansery", resourceCost = mapOf(Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 2, resourceAlternativeProduction = ResourcesAlternative.WOOD_OR_CLAY_OR_STONE))
            cards_mutable.add(Card(deck = deckName, name = "Forum", resourceCost = mapOf(Resource.CLAY to 1), coinCost = 3, resourceAlternativeProduction = ResourcesAlternative.GLASS_OR_PAPER))

            return Vector.ofAll(cards_mutable)
        }
    }
}