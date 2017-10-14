package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.Formula
import com.aigamelabs.swduel.enums.*

class Card(val deck: DeckName, val name: String, val color: CardColor,
           val resourceCost: Map<Resource, Int>, val coinCost: Int,
           val linkingSymbol: LinkingSymbol, val linksTo: LinkingSymbol,
           val tradingBonuses: Set<Resource>, val resourceProduction: Map<Resource, Int>,
           val resourceAlternativeProduction: ResourcesAlternative,
           val victoryPoints: Int, val victoryPointsFormula: Formula,
           val coinsProduced: Int, val coinsProducedFormula: Formula,
           val referenceCity: CityForFormula,
           val scienceSymbol: ScienceSymbol, militaryPoints: Int,
           val bonuses: Set<Bonus>
           ) {
    
    // Constructor for (red) military cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol, militaryPoints: Int) :
            this(deck = deck, name = name, color = CardColor.RED,
                    resourceCost = resourceCost, coinCost = coinCost,
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = 0, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = militaryPoints,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (green) science cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol,
                victoryPoints: Int, scienceSymbol: ScienceSymbol) :
            this(deck = deck, name = name, color = CardColor.GREEN,
                    resourceCost = resourceCost, coinCost = coinCost,
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = victoryPoints, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = scienceSymbol, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (brown/gray) resources cards
    constructor(deck: DeckName, name: String, color: CardColor, coinCost: Int, resourcesProduced: Map<Resource, Int>) :
            this(deck = deck, name = name, color = color,
                    resourceCost = emptyMap<Resource, Int>(), coinCost = coinCost,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = resourcesProduced,
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = 0, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (golden) trading cards
    constructor(deck: DeckName, name: String, coinCost: Int, tradingBonuses: Set<Resource>) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = emptyMap<Resource, Int>(), coinCost = coinCost,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = tradingBonuses, resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = 0, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (golden) coin cards
    constructor(deck: DeckName, name: String, coinsProduced: Int, linkingSymbol: LinkingSymbol) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = emptyMap<Resource, Int>(), coinCost = 0,
                    linkingSymbol = linkingSymbol, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = 0, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = coinsProduced, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (golden) alternative production cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                resourceAlternativeProduction: ResourcesAlternative) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = resourceCost, coinCost = coinCost,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = resourceAlternativeProduction,
                    victoryPoints = 0, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (golden) bonus cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>, linksTo: LinkingSymbol,
                victoryPoints: Int, coinsProduced: Int, coinsProducedFormula: Formula) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = resourceCost, coinCost = 0,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = linksTo,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = victoryPoints, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = coinsProduced, coinsProducedFormula = coinsProducedFormula,
                    referenceCity = CityForFormula.YOUR_CITY,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (blue) victory points cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol,
                victoryPoints: Int) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = resourceCost, coinCost = 0,
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = victoryPoints, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = 0, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.NOT_APPLICABLE,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (purple) bonus cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>,
                victoryPoints: Int, victoryPointsFormula: Formula,
                coinsProduced: Int, coinsProducedFormula: Formula) :
            this(deck = deck, name = name, color = CardColor.GOLD,
                    resourceCost = resourceCost, coinCost = 0,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = ResourcesAlternative.NONE,
                    victoryPoints = victoryPoints, victoryPointsFormula = victoryPointsFormula,
                    coinsProduced = coinsProduced, coinsProducedFormula = coinsProducedFormula,
                    referenceCity = CityForFormula.CITY_WITH_MOST_UNITS,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (wonder) bonus cards
    constructor(deck: DeckName, name: String, resourceCost: Map<Resource, Int>,
                resourceAlternativeProduction: ResourcesAlternative,
                coinsProduced: Int, victoryPoints: Int,
                militaryPoints: Int,
                bonuses: Set<Bonus>) :
            this(deck = deck, name = name, color = CardColor.WONDER,
                    resourceCost = resourceCost, coinCost = 0,
                    linkingSymbol = LinkingSymbol.NONE, linksTo = LinkingSymbol.NONE,
                    tradingBonuses = emptySet<Resource>(), resourceProduction = emptyMap<Resource, Int>(),
                    resourceAlternativeProduction = resourceAlternativeProduction,
                    victoryPoints = victoryPoints, victoryPointsFormula = Formula.ABSOLUTE,
                    coinsProduced = coinsProduced, coinsProducedFormula = Formula.ABSOLUTE,
                    referenceCity = CityForFormula.CITY_WITH_MOST_UNITS,
                    scienceSymbol = ScienceSymbol.NONE, militaryPoints = militaryPoints,
                    bonuses = bonuses
            )
}