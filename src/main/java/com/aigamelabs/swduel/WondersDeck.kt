package com.aigamelabs.swduel

import io.vavr.kotlin.toVavrList
import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.enums.*

class WondersDeckDeck() : Deck(DeckName.WONDERS) {
    init {
        val cards_mutable = ArrayList<Card>()

        cards_mutable.add(Card(deck = this.deckName, name = "The Great Library", resourceCost = hashMapOf(Resource.WOOD to 3, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 4, militaryPoints = 0, bonuses = setOf(Bonus.DRAW_PROGRESS_TOKEN)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Hanging Gardens", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 6, victoryPoints = 3, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Mausoleum", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.PAPER to 1, Resource.CLAY to 2), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 2, militaryPoints = 0, bonuses = setOf(Bonus.DRAW_FROM_DISCARDED)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Colossus", resourceCost = hashMapOf(Resource.GLASS to 1, Resource.CLAY to 3), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 2, bonuses = emptySet()))
        cards_mutable.add(Card(deck = this.deckName, name = "The Great Lighthouse", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2), resourceAlternativeProduction = ResourcesAlternative.WOOD_OR_CLAY_OR_STONE, coinsProduced = 0, victoryPoints = 4, militaryPoints = 0, bonuses = emptySet()))
        cards_mutable.add(Card(deck = this.deckName, name = "Circus Maximus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 2, Resource.GLASS to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 1, bonuses = setOf(Bonus.BURN_GRAY_BUILDING)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Statue of Zeus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 1, bonuses = setOf(Bonus.BURN_BROWN_BUILDING)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Temple of Artemis", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 12, victoryPoints = 0, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Appian Way", resourceCost = hashMapOf(Resource.PAPER to 1, Resource.STONE to 2, Resource.CLAY to 2), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 3, victoryPoints = 3, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN, Bonus.BURN_THREE_COINS)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Sphinx", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.STONE to 1, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 6, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(deck = this.deckName, name = "The Pyramids", resourceCost = hashMapOf(Resource.STONE to 3, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 9, militaryPoints = 0, bonuses = emptySet()))
        cards_mutable.add(Card(deck = this.deckName, name = "Piraeus", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.STONE to 1, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.GLASS_OR_PAPER, coinsProduced = 0, victoryPoints = 2, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))

        cards = cards_mutable.toVavrList()
    }
}