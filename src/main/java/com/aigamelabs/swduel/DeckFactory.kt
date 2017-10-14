package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.*
import io.vavr.collection.Stream
import io.vavr.collection.Vector

object DeckFactory {

    fun createFirstAgeDeck() : Deck {
        val cardGroup = CardGroup.FIRST_AGE
        val cards_mutable = ArrayList<Card>()

        // Brown cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Lumber yard", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.WOOD to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Clay pool", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.CLAY to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Quarry", color = CardColor.BROWN, coinCost = 0, resourcesProduced = hashMapOf(Resource.STONE to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Logging camp", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.WOOD to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Clay pit", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.CLAY to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Stone pit", color = CardColor.BROWN, coinCost = 1, resourcesProduced = hashMapOf(Resource.STONE to 1)))

        // Gray cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Glassworks", color = CardColor.GRAY, coinCost = 1, resourcesProduced = hashMapOf(Resource.GLASS to 1)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Press", color = CardColor.GRAY, coinCost = 1, resourcesProduced = hashMapOf(Resource.PAPER to 1)))

        // Green cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Pharmacist", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.GEAR, linksTo = LinkingSymbol.NONE, victoryPoints = 0, scienceSymbol = ScienceSymbol.MORTAR_AND_PESTEL))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Scriptorium", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.BOOK, linksTo = LinkingSymbol.NONE, victoryPoints = 0, scienceSymbol = ScienceSymbol.QUILL_AND_INKPEN))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Workshop", resourceCost = mapOf(Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.INCLINOMETER))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Apothecary", resourceCost = mapOf(Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.WHEEL))

        // Blue cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Baths", resourceCost = mapOf(Resource.STONE to 1), linkingSymbol = LinkingSymbol.DROP, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Altar", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MOON, linksTo = LinkingSymbol.NONE, victoryPoints = 3))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Theater", resourceCost = emptyMap(), linkingSymbol = LinkingSymbol.MASK, linksTo = LinkingSymbol.NONE, victoryPoints = 3))

        // Red cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Palisade", resourceCost = emptyMap(), coinCost = 2, linkingSymbol = LinkingSymbol.TOWER, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Stable", resourceCost = mapOf(Resource.WOOD to 1), coinCost = 0, linkingSymbol = LinkingSymbol.HORSESHOE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Garrison", resourceCost = mapOf(Resource.CLAY to 1), coinCost = 0, linkingSymbol = LinkingSymbol.SWORD, linksTo = LinkingSymbol.NONE, militaryPoints = 1))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Guard tower", resourceCost = emptyMap(), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 1))

        // Gold cards
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Wood reserve", coinCost = 3, tradingBonuses = setOf(Resource.WOOD)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Clay reserve", coinCost = 3, tradingBonuses = setOf(Resource.CLAY)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Stone reserve", coinCost = 3, tradingBonuses = setOf(Resource.STONE)))
        cards_mutable.add(Card(cardGroup = cardGroup, name = "Tavern", coinsProduced = 4, linkingSymbol = LinkingSymbol.VASE))

        return Deck("First Age", Vector.ofAll(cards_mutable))
    }


    fun createSecondAgeDeck() : Deck {
        val deckName = CardGroup.SECOND_AGE
        val cards_mutable = ArrayList<Card>()

        // Brown cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2)))
        cards_mutable.add(Card(cardGroup = deckName, name = "Brickyard", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.CLAY to 2)))
        cards_mutable.add(Card(cardGroup = deckName, name = "Shelf quarry", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.STONE to 2)))

        // Gray cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Glass-blower", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.GLASS to 1)))
        cards_mutable.add(Card(cardGroup = deckName, name = "Drying room", color = CardColor.GRAY, coinCost = 0, resourcesProduced = hashMapOf(Resource.PAPER to 1)))

        // Green cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Dispensary", resourceCost = hashMapOf(Resource.CLAY to 2, Resource.STONE to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.GEAR, victoryPoints = 2, scienceSymbol = ScienceSymbol.MORTAR_AND_PESTEL))
        cards_mutable.add(Card(cardGroup = deckName, name = "Library", resourceCost = hashMapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.BOOK, victoryPoints = 2, scienceSymbol = ScienceSymbol.QUILL_AND_INKPEN))
        cards_mutable.add(Card(cardGroup = deckName, name = "School", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.PAPER to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LYRE, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.WHEEL))
        cards_mutable.add(Card(cardGroup = deckName, name = "Laboratory", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.GLASS to 2), coinCost = 0, linkingSymbol = LinkingSymbol.LAMP, linksTo = LinkingSymbol.NONE, victoryPoints = 1, scienceSymbol = ScienceSymbol.INCLINOMETER))

        // Blue cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Temple", resourceCost = mapOf(Resource.WOOD to 1, Resource.PAPER to 1), linkingSymbol = LinkingSymbol.SUN, linksTo = LinkingSymbol.MOON, victoryPoints = 4))
        cards_mutable.add(Card(cardGroup = deckName, name = "Statue", resourceCost = mapOf(Resource.CLAY to 2), linkingSymbol = LinkingSymbol.COLUMN, linksTo = LinkingSymbol.MASK, victoryPoints = 4))
        cards_mutable.add(Card(cardGroup = deckName, name = "Rostrum", resourceCost = mapOf(Resource.STONE to 1, Resource.WOOD to 1), linkingSymbol = LinkingSymbol.TEMPLE, linksTo = LinkingSymbol.NONE, victoryPoints = 4))
        cards_mutable.add(Card(cardGroup = deckName, name = "Courthouse", resourceCost = mapOf(Resource.WOOD to 2, Resource.GLASS to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 5))
        cards_mutable.add(Card(cardGroup = deckName, name = "Aqueduct", resourceCost = mapOf(Resource.STONE to 3), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.DROP, victoryPoints = 5))

        // Red cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Barracks", resourceCost = emptyMap(), coinCost = 3, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.SWORD, militaryPoints = 1))
        cards_mutable.add(Card(cardGroup = deckName, name = "Horse breeders", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.HORSESHOE, militaryPoints = 1))
        cards_mutable.add(Card(cardGroup = deckName, name = "Walls", resourceCost = mapOf(Resource.STONE to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 2))
        cards_mutable.add(Card(cardGroup = deckName, name = "Parade ground", resourceCost = mapOf(Resource.CLAY to 2, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.HELMET, linksTo = LinkingSymbol.NONE, militaryPoints = 2))
        cards_mutable.add(Card(cardGroup = deckName, name = "Archery range", resourceCost = mapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.TARGET, linksTo = LinkingSymbol.NONE, militaryPoints = 2))

        // Gold cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Customs house", coinCost = 4, tradingBonuses = setOf(Resource.PAPER, Resource.GLASS)))
        cards_mutable.add(Card(cardGroup = deckName, name = "Brewery", coinsProduced = 6, linkingSymbol = LinkingSymbol.BARREL))
        cards_mutable.add(Card(cardGroup = deckName, name = "Caravansery", resourceCost = mapOf(Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 2, resourceAlternativeProduction = ResourcesAlternative.WOOD_OR_CLAY_OR_STONE))
        cards_mutable.add(Card(cardGroup = deckName, name = "Forum", resourceCost = mapOf(Resource.CLAY to 1), coinCost = 3, resourceAlternativeProduction = ResourcesAlternative.GLASS_OR_PAPER))

        return Deck("Second Age", Vector.ofAll(cards_mutable))
    }


    fun createThirdAgeDeck() : Deck {
        val deckName = CardGroup.THIRD_AGE
        val cards_mutable = ArrayList<Card>()

        // Green cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Observatory", resourceCost = hashMapOf(Resource.STONE to 1, Resource.PAPER to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.LAMP, victoryPoints = 2, scienceSymbol = ScienceSymbol.ARMILLARY_SPHERE))
        cards_mutable.add(Card(cardGroup = deckName, name = "University", resourceCost = hashMapOf(Resource.CLAY to 1, Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.LYRE, victoryPoints = 2, scienceSymbol = ScienceSymbol.ARMILLARY_SPHERE))
        cards_mutable.add(Card(cardGroup = deckName, name = "Academy", resourceCost = hashMapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 3, scienceSymbol = ScienceSymbol.SUNDIAL))
        cards_mutable.add(Card(cardGroup = deckName, name = "Study", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 3, scienceSymbol = ScienceSymbol.SUNDIAL))

        // Blue cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Senate", resourceCost = mapOf(Resource.CLAY to 2, Resource.STONE to 1, Resource.PAPER to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TEMPLE, victoryPoints = 5))
        cards_mutable.add(Card(cardGroup = deckName, name = "Obelisk", resourceCost = mapOf(Resource.STONE to 2, Resource.GLASS to 1), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 5))
        cards_mutable.add(Card(cardGroup = deckName, name = "Pantheon", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.PAPER to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.SUN, victoryPoints = 6))
        cards_mutable.add(Card(cardGroup = deckName, name = "Gardens", resourceCost = mapOf(Resource.CLAY to 2, Resource.WOOD to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.COLUMN, victoryPoints = 6))
        cards_mutable.add(Card(cardGroup = deckName, name = "Palace", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 7))
        cards_mutable.add(Card(cardGroup = deckName, name = "Town hall", resourceCost = mapOf(Resource.STONE to 3, Resource.WOOD to 2), linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, victoryPoints = 7))

        // Red cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Circus", resourceCost = mapOf(Resource.CLAY to 2, Resource.STONE to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.HELMET, militaryPoints = 2))
        cards_mutable.add(Card(cardGroup = deckName, name = "Fortifications", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPER to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TOWER, militaryPoints = 2))
        cards_mutable.add(Card(cardGroup = deckName, name = "Siege workshop", resourceCost = mapOf(Resource.WOOD to 3, Resource.GLASS to 1), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.TARGET, militaryPoints = 2))
        cards_mutable.add(Card(cardGroup = deckName, name = "Pretorium", resourceCost = emptyMap(), coinCost = 8, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 3))
        cards_mutable.add(Card(cardGroup = deckName, name = "Arsenal", resourceCost = mapOf(Resource.CLAY to 3, Resource.WOOD to 2), coinCost = 0, linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE, militaryPoints = 3))

        // Gold cards
        cards_mutable.add(Card(cardGroup = deckName, name = "Port", resourceCost = mapOf(Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPER to 1), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 2, coinsProducedFormula = Formula.PER_BROWN_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Chamber of commerce", resourceCost = mapOf(Resource.PAPER to 2), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 3, coinsProducedFormula = Formula.PER_GRAY_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Armory", resourceCost = mapOf(Resource.STONE to 2, Resource.GLASS to 1), linksTo = LinkingSymbol.NONE, victoryPoints = 3, coinsProduced = 1, coinsProducedFormula = Formula.PER_RED_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Lighthouse", resourceCost = mapOf(Resource.CLAY to 2, Resource.GLASS to 1), linksTo = LinkingSymbol.VASE, victoryPoints = 3, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Arena", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1), linksTo = LinkingSymbol.BARREL, victoryPoints = 3, coinsProduced = 2, coinsProducedFormula = Formula.PER_WONDER))

        var thirdAgeCards = Deck(":Third Age", Vector.ofAll(cards_mutable))
        var guildsCards = DeckFactory.createGuildsDeck()

        // Remove 3 cards from the third age

        Stream.of(0, 1, 2
        ).forEach {
            val drawOutcome = thirdAgeCards.drawCard().getOrElseThrow({ -> Exception("Problem removing third age cards") })
            thirdAgeCards = drawOutcome.second
        }

        // Add 3 guild cards to deck

        Stream.of(0, 1, 2
        ).forEach {
            val drawOutcome = guildsCards.drawCard().getOrElseThrow({ -> Exception("Problem removing guildCards age cards")})
            guildsCards = drawOutcome.second
            thirdAgeCards = thirdAgeCards.addCard(drawOutcome.first)
        }
        return thirdAgeCards
    }


    fun createGuildsDeck() : Deck {
        val deckName = CardGroup.GUILDS
        val cards_mutable = ArrayList<Card>()

        cards_mutable.add(Card(cardGroup = deckName, name = "Merchants guild", resourceCost = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_GOLD_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GOLD_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Scientists guild", resourceCost = mapOf(Resource.CLAY to 2, Resource.WOOD to 2), victoryPoints = 1, victoryPointsFormula = Formula.PER_GREEN_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_GREEN_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Tacticians guild", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_RED_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_RED_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Magistrates guild", resourceCost = mapOf(Resource.WOOD to 2, Resource.CLAY to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_BLUE_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_BLUE_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Shipowners guild", resourceCost = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPER to 1), victoryPoints = 1, victoryPointsFormula = Formula.PER_BROWN_AND_GRAY_CARD, coinsProduced = 1, coinsProducedFormula = Formula.PER_BROWN_AND_GRAY_CARD))
        cards_mutable.add(Card(cardGroup = deckName, name = "Builders guild", resourceCost = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1), victoryPoints = 2, victoryPointsFormula = Formula.PER_WONDER, coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE))
        cards_mutable.add(Card(cardGroup = deckName, name = "Moneylenders guild", resourceCost = mapOf(Resource.STONE to 2, Resource.WOOD to 2), victoryPoints = 1, victoryPointsFormula = Formula.PER_THREE_COINS, coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE))

        return Deck("Guilds", Vector.ofAll(cards_mutable))
    }


    fun createWondersDeck() : Deck {
        val deckName = CardGroup.WONDERS
        val cards_mutable = ArrayList<Card>()

        cards_mutable.add(Card(cardGroup = deckName, name = "The Great Library", resourceCost = hashMapOf(Resource.WOOD to 3, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 4, militaryPoints = 0, bonuses = setOf(Bonus.DRAW_PROGRESS_TOKEN)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Hanging Gardens", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 6, victoryPoints = 3, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Mausoleum", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.PAPER to 1, Resource.CLAY to 2), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 2, militaryPoints = 0, bonuses = setOf(Bonus.DRAW_FROM_DISCARDED)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Colossus", resourceCost = hashMapOf(Resource.GLASS to 1, Resource.CLAY to 3), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 2, bonuses = emptySet()))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Great Lighthouse", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2), resourceAlternativeProduction = ResourcesAlternative.WOOD_OR_CLAY_OR_STONE, coinsProduced = 0, victoryPoints = 4, militaryPoints = 0, bonuses = emptySet()))
        cards_mutable.add(Card(cardGroup = deckName, name = "Circus Maximus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 2, Resource.GLASS to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 1, bonuses = setOf(Bonus.BURN_GRAY_BUILDING)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Statue of Zeus", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.PAPER to 2, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 3, militaryPoints = 1, bonuses = setOf(Bonus.BURN_BROWN_BUILDING)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Temple of Artemis", resourceCost = hashMapOf(Resource.WOOD to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 12, victoryPoints = 0, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Appian Way", resourceCost = hashMapOf(Resource.PAPER to 1, Resource.STONE to 2, Resource.CLAY to 2), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 3, victoryPoints = 3, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN, Bonus.BURN_THREE_COINS)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Sphinx", resourceCost = hashMapOf(Resource.GLASS to 2, Resource.STONE to 1, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 6, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))
        cards_mutable.add(Card(cardGroup = deckName, name = "The Pyramids", resourceCost = hashMapOf(Resource.STONE to 3, Resource.PAPER to 1), resourceAlternativeProduction = ResourcesAlternative.NONE, coinsProduced = 0, victoryPoints = 9, militaryPoints = 0, bonuses = emptySet()))
        cards_mutable.add(Card(cardGroup = deckName, name = "Piraeus", resourceCost = hashMapOf(Resource.WOOD to 2, Resource.STONE to 1, Resource.CLAY to 1), resourceAlternativeProduction = ResourcesAlternative.GLASS_OR_PAPER, coinsProduced = 0, victoryPoints = 2, militaryPoints = 0, bonuses = setOf(Bonus.EXTRA_TURN)))

        return Deck("Wonders", Vector.ofAll(cards_mutable))
    }
}