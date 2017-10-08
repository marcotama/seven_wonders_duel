import enums.*

class WondersDeckDeck() : Deck(DeckName.WONDERS) {
    init {
        val cards = ArrayList<Card>()

        cards.add(Card(deck = this.deckName, name = "The Great Library",  resourceCost = hashMapOf(Resource.WOOD to 3, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 4, bonuses = bonuses.DRAW_PROGRESS_TOKEN))

        cards.add(Card(deck = this.deckName, name = "The Hanging Gardens", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 3, coinsProduced = 6, bonuses = bonuses.EXTRA_TURN))

        cards.add(Card(deck = this.deckName, name = "The Mausoleum", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.PAPER to 1, Resource.CLAY to 2), victoryPoints = 2, bonuses = bonuses.DRAW_FROM_DISCARDED))

        cards.add(Card(deck = this.deckName, name = "The Colossus", resourceCost = hashMapOf(Resource.GLASS to 1, Resource.CLAY to 3), victoryPoints = 3, militaryPoints = 2))

        cards.add(Card(deck = this.deckName, name = "The Great Lighthouse", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2), victoryPoints = 4, resourceAlternativeProduction = ResourcesAlternative.WOOD_OR_CLAY_OR_STONE))

        cards.add(Card(deck = this.deckName, name = "Circus Maximus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 2, Resource.GLASS to 1), victoryPoints = 3, militaryPoints = 1, bonuses = bonuses.BURN_GRAY_BUILDING))

        cards.add(Card(deck = this.deckName, name = "The Statue of Zeus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2, Resource.CLAY to 1), victoryPoints = 3, militaryPoints = 1, bonuses = bonuses.BURN_BROWN_BUILDING))

        cards.add(Card(deck = this.deckName, name = "The Temple of Artemis", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPER to 1), coinsProduced = 12, bonuses = bonuses.EXTRA_TURN))

        cards.add(Card(deck = this.deckName, name = "The Appian Way", resourceCost = hashMapOf(Resource.PAPER to 1, Resource.STONE to 2, Resource.CLAY to 2), coinsProduced = 3, victoryPoints = 3, bonuses = setOf(bonuses.EXTRA_TURN, bonuses.BURN_THREE_COINS)))

        cards.add(Card(deck = this.deckName, name = "The Sphinx", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.STONE to 1, Resource.CLAY to 1), victoryPoints = 6, bonuses = bonuses.EXTRA_TURN))

        cards.add(Card(deck = this.deckName, name = "The Pyramids", resourceCost = hashMapOf(Resource.STONE to 3, Resource.PAPER to 1), victoryPoints = 9))

        cards.add(Card(deck = this.deckName, name = "Piraeus", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.STONE to 1, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.GLASS_OR_PAPER, victoryPoints = 2, bonuses = bonuses.EXTRA_TURN))
    }
}