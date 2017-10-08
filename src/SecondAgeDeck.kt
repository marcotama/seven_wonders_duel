import enums.*

class SecondAgeDeck() : Deck(DeckName.FIRST_AGE) {
    init {
        val cards = ArrayList<Card>()

        cards.add(Card(deck = this.deckName, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2)))
        cards.add(Card(deck = this.deckName, name = "Brickyard", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.CLAY to 2)))
        cards.add(Card(deck = this.deckName, name = "Shelf quarry", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.STONE to 2)))

        cards.add(Card(deck = this.deckName, name = "Glass-blower", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.GLASS to 1)))
        cards.add(Card(deck = this.deckName, name = "Drying room", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.PAPER to 1)))

        cards.add(Card(deck = this.deckName, name = "Dispensary", resourceCost = hashMapOf(Resource.CLAY to 2, Resource.STONE to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.GEAR, victoryPoints = 2, scienceSymbol = ScienceSymbol.MORTAR_AND_PESTEL))
        cards.add(Card(deck = this.deckName, name = "Library", resourceCost = hashMapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.BOOK, linksTo = LinkingSymbol.NONE, victoryPoints = 2, scienceSymbol = ScienceSymbol.QUILL_AND_INKPEN))
        cards.add(Card(deck = this.deckName, name = "School", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.PAPER to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LYRE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.WHEEL))
        cards.add(Card(deck = this.deckName, name = "Laboratory", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.GLASS to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LAMP, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.INCLINOMETER))

        cards.add(Card(deck = this.deckName, name = "Baths", resourceCost = mapOf(Resource.STONE to 1), linkingSymbol = LinkingSymbol.DROP, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards.add(Card(deck = this.deckName, name = "Altar", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MOON, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards.add(Card(deck = this.deckName, name = "Theater", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MASK, linksTo = LinkingSymbol.NONE, victoryPoints = 3))

        cards.add(Card(deck = this.deckName, name = "Palisade", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.TOWER, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards.add(Card(deck = this.deckName, name = "Stable", resourceCost = mapOf(Resource.WOOD to 1), coinCost = 0, linkingSymbol = LinkingSymbol.HORSESHOE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards.add(Card(deck = this.deckName, name = "Garrison", resourceCost = mapOf(Resource.CLAY to 1), coinCost = 0, linkingSymbol = LinkingSymbol.SWORD, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards.add(Card(deck = this.deckName, name = "Guard tower", resourceCost = emptyMap(), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))

        cards.add(Card(deck = this.deckName, name = "Wood reserve", coinCost = 3, tradingBonuses = setOf(Resource.WOOD)))
        cards.add(Card(deck = this.deckName, name = "Clay reserve", coinCost = 3, tradingBonuses = setOf(Resource.CLAY)))
        cards.add(Card(deck = this.deckName, name = "Stone reserve", coinCost = 3, tradingBonuses = setOf(Resource.STONE)))

        cards.add(Card(deck = this.deckName, name = "Tavern", coinsProduced = 4, linkingSymbol = LinkingSymbol.VASE))
    }
}