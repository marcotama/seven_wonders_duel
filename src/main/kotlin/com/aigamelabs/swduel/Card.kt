package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.Formula
import com.aigamelabs.swduel.enums.*
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import javax.json.stream.JsonGenerator

data class Card(
        override val cardGroup: CardGroup,
        val color: CardColor,
        val name: String,
        val resourceCost: HashMap<Resource, Int> = HashMap.empty(),
        val coinCost: Int = 0,
        val linkingSymbol: LinkingSymbol = LinkingSymbol.NONE,
        val linksTo: LinkingSymbol = LinkingSymbol.NONE,
        val tradingBonuses: HashSet<Resource> = HashSet.empty(),
        val resourceProduction: HashMap<Resource, Int> = HashMap.empty(),
        val resourceAlternativeProduction: ResourcesAlternative = ResourcesAlternative.NONE,
        val victoryPoints: Int = 0,
        val victoryPointsFormula: Formula = Formula.ABSOLUTE,
        val victoryPointsReferenceCity: CityForFormula = CityForFormula.NOT_APPLICABLE,
        val coinsProduced: Int = 0,
        val coinsProducedFormula: Formula = Formula.ABSOLUTE,
        val coinsProducedReferenceCity: CityForFormula = CityForFormula.NOT_APPLICABLE,
        val scienceSymbol: ScienceSymbol = ScienceSymbol.NONE,
        val militaryPoints: Int = 0,
        val bonuses: Set<Bonus> = emptySet(),
        val wonders: Wonders = Wonders.NONE,
        val enhancement: Enhancement = Enhancement.NONE
) : CardPlaceholder (cardGroup) {

    // Constructor for (red) military cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol, militaryPoints: Int) :
            this(cardGroup = cardGroup, name = name, color = CardColor.RED,
                    resourceCost = HashMap.ofAll(resourceCost), coinCost = coinCost,
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    militaryPoints = militaryPoints
            )

    // Constructor for (green) science cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol,
                victoryPoints: Int, scienceSymbol: ScienceSymbol) :
            this(cardGroup = cardGroup, name = name, color = CardColor.GREEN,
                    resourceCost = HashMap.ofAll(resourceCost), coinCost = coinCost,
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    victoryPoints = victoryPoints,
                    scienceSymbol = scienceSymbol
            )

    // Constructor for (brown/gray) resources cards
    constructor(cardGroup: CardGroup, name: String, color: CardColor, coinCost: Int, resourcesProduced: Map<Resource, Int>) :
            this(cardGroup = cardGroup, name = name, color = color, coinCost = coinCost,
                    resourceProduction = HashMap.ofAll(resourcesProduced)
            )

    // Constructor for (golden) trading cards
    constructor(cardGroup: CardGroup, name: String, coinCost: Int, tradingBonuses: Set<Resource>) :
            this(cardGroup = cardGroup, name = name, color = CardColor.GOLD, coinCost = coinCost,
                    tradingBonuses = HashSet.ofAll(tradingBonuses)
            )

    // Constructor for (golden) coin cards
    constructor(cardGroup: CardGroup, name: String, coinsProduced: Int, linkingSymbol: LinkingSymbol) :
            this(cardGroup = cardGroup, name = name, color = CardColor.GOLD,
                    linkingSymbol = linkingSymbol,
                    coinsProduced = coinsProduced
            )

    // Constructor for (golden) alternative production cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>, coinCost: Int,
                resourceAlternativeProduction: ResourcesAlternative) :
            this(cardGroup = cardGroup, name = name, color = CardColor.GOLD,
                    resourceCost = HashMap.ofAll(resourceCost), coinCost = coinCost,
                    resourceAlternativeProduction = resourceAlternativeProduction
            )

    // Constructor for (golden) bonus cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>, linksTo: LinkingSymbol,
                victoryPoints: Int, coinsProduced: Int, coinsProducedFormula: Formula) :
            this(cardGroup = cardGroup, name = name, color = CardColor.GOLD,
                    resourceCost = HashMap.ofAll(resourceCost),
                    linksTo = linksTo,
                    victoryPoints = victoryPoints,
                    coinsProduced = coinsProduced,
                    coinsProducedFormula = coinsProducedFormula,
                    coinsProducedReferenceCity = CityForFormula.YOUR_CITY
            )

    // Constructor for (blue) victory points cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>,
                linkingSymbol: LinkingSymbol, linksTo: LinkingSymbol,
                victoryPoints: Int) :
            this(cardGroup = cardGroup, name = name, color = CardColor.BLUE,
                    resourceCost = HashMap.ofAll(resourceCost),
                    linkingSymbol = linkingSymbol, linksTo = linksTo,
                    victoryPoints = victoryPoints
            )

    // Constructor for (purple) bonus cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>,
                victoryPoints: Int, victoryPointsFormula: Formula,
                coinsProduced: Int, coinsProducedFormula: Formula) :
            this(cardGroup = cardGroup, name = name, color = CardColor.PURPLE,
                    resourceCost = HashMap.ofAll(resourceCost),
                    victoryPoints = victoryPoints,
                    victoryPointsFormula = victoryPointsFormula,
                    victoryPointsReferenceCity = CityForFormula.CITY_WITH_MOST_UNITS,
                    coinsProduced = coinsProduced,
                    coinsProducedFormula = coinsProducedFormula,
                    coinsProducedReferenceCity = CityForFormula.CITY_WITH_MOST_UNITS,
                    militaryPoints = 0,
                    bonuses = emptySet<Bonus>()
            )

    // Constructor for (wonder) bonus cards
    constructor(cardGroup: CardGroup, name: String, resourceCost: Map<Resource, Int>,
                resourceAlternativeProduction: ResourcesAlternative,
                coinsProduced: Int, victoryPoints: Int,
                militaryPoints: Int,
                bonuses: Set<Bonus>, wonders: Wonders) :
            this(cardGroup = cardGroup, name = name, color = CardColor.WONDER,
                    resourceCost = HashMap.ofAll(resourceCost),
                    resourceAlternativeProduction = resourceAlternativeProduction,
                    victoryPoints = victoryPoints,
                    coinsProduced = coinsProduced,
                    militaryPoints = militaryPoints,
                    bonuses = bonuses,
                    wonders = wonders
            )
    // Constructor for Science Tokens
    constructor(cardGroup: CardGroup, name: String, coinsProduced : Int = 0, victoryPoints: Int = 0,  enhancement: Enhancement) :
            this(
                    color = CardColor.PROGRESS_TOKEN,
                    cardGroup = cardGroup,
                    name = name,
                    coinsProduced = coinsProduced,
                    victoryPoints = victoryPoints,
                    enhancement = enhancement
            )

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson (generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("group", cardGroup.toString())
        generator.write("color", color.toString())
        generator.write("coin_cost", coinCost.toString())
        generator.write("linking_symbol", linkingSymbol.toString())
        generator.write("links_to", linksTo.toString())
        generator.write("resource_alternative_production", resourceAlternativeProduction.toString())
        generator.write("coin_cost", coinCost.toString())
        generator.write("victoryPoints", victoryPoints.toString())
        generator.write("victoryPointsFormula", victoryPointsFormula.toString())
        generator.write("coinsProduced", coinsProduced.toString())
        generator.write("coinsProducedFormula", coinsProducedFormula.toString())
        generator.write("coinsProducedReferenceCity", coinsProducedReferenceCity.toString())
        generator.write("scienceSymbol", scienceSymbol.toString())
        generator.write("militaryPoints", militaryPoints.toString())
        generator.write("wonders", wonders.toString())
        generator.write("enhancement", enhancement.toString())
        generator.writeStartObject("resource_cost")
        resourceCost.forEach { generator.write(it._1.toString(), it._2) }
        generator.writeEnd()
        generator.writeStartArray("trading_bonuses")
        tradingBonuses.forEach { generator.write(it.toString()) }
        generator.writeEnd()
        generator.writeStartObject("resource_production")
        resourceProduction.forEach { generator.write(it._1.toString(), it._2) }
        generator.writeEnd()
        generator.writeStartArray("bonuses")
        bonuses.forEach { generator.write(it.toString()) }
        generator.writeEnd()

        generator.writeEnd()
    }

    override fun toString(): String {
        return name
    }
}