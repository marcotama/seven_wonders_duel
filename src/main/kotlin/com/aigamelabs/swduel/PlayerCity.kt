package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.*
import io.vavr.collection.Vector
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import io.vavr.collection.Stream
import org.json.JSONObject
import javax.json.stream.JsonGenerator

data class PlayerCity(
        val name : String,
        val coins : Int,
        val buildings : HashSet<Card>,
        val wonders : HashSet<Card>,
        val progressTokens: HashSet<Card>,
        val unbuiltWonders: HashSet<Card>
) {

    constructor(name : String) : this(name, 7, HashSet.empty(), HashSet.empty(), HashSet.empty(), HashSet.empty())

    fun update(
            name_ : String? = null,
            coins_ : Int? = null,
            buildings_ : HashSet<Card>? = null,
            wonders_ : HashSet<Card>? = null,
            progressTokens_: HashSet<Card>? = null,
            unbuiltWonders_ : HashSet<Card>? = null
    ) : PlayerCity {
        return PlayerCity(
                name_ ?: name,
                coins_ ?: coins,
                buildings_ ?: buildings,
                wonders_ ?: wonders,
                progressTokens_ ?: progressTokens,
                unbuiltWonders_ ?: unbuiltWonders
        )
    }

    /**
     * Calculates whether this city can afford building a given new building and returns the cost associated with an
     * optimal choice of alternatively produced resources.
     */
    fun canBuild(newBuilding: Card, opponentCity: PlayerCity) : Int? {
        var resourceCost = newBuilding.resourceCost

        var altProduction : Vector<ResourcesAlternative> = Vector.empty()
        for (building in buildings.plus(wonders)) {
            // Check linking symbols
            if (newBuilding.linksTo != LinkingSymbol.NONE && newBuilding.linksTo == building.linkingSymbol) {
                return 0
            }
            // Calculate resources production
            building.resourceProduction.forEach { resource, tot ->
                resourceCost = resourceCost.put(resource, resourceCost.getOrElse(resource, 0) - tot)
            }
            // Calculate resource alternatives production
            if (building.resourceAlternativeProduction != ResourcesAlternative.NONE) {
                altProduction = altProduction.append(building.resourceAlternativeProduction)
            }
        }

        // Handle Architecture and Masonry discounts
        if (newBuilding.color == CardColor.WONDER && hasProgressToken(Enhancement.ARCHITECTURE)) {
            altProduction.appendAll(Stream.of(ResourcesAlternative.ANY, ResourcesAlternative.ANY))
        }
        if (newBuilding.color == CardColor.BLUE && hasProgressToken(Enhancement.MASONRY)) {
            altProduction.appendAll(Stream.of(ResourcesAlternative.ANY, ResourcesAlternative.ANY))
        }

        // Calculate remaining coin cost given optimal choices
        val minCost = calcMinCost(resourceCost, altProduction, opponentCity) + newBuilding.coinCost

        return if (minCost > coins) null else minCost

    }

    fun hasProgressToken(enhancement: Enhancement) : Boolean {
        return !progressTokens.filter { it.enhancement == enhancement}.isEmpty
    }

    /**
     * Calculates the production of a given resource from brown/gray cards.
     *
     * @param resource the resource of interest
     * @return the total production of the given resource
     */
    fun pureResourceProduction(resource : Resource) : Int {
        var resourceProduction = 0
        buildings.forEach {
            it.resourceProduction.forEach { r, tot ->
                resourceProduction += if (r == resource) tot else 0
            }
        }
        return resourceProduction
    }

    /**
     * Removes from the opponent city the specified amount of coins
     */
    fun removeCoins(coinsToBeRemoved: Int): PlayerCity {
        return update(coins_ = coins - coinsToBeRemoved)
    }

    /**
     * Add coins generated by this card to the player city coins
     */
    fun addCoins(coinsToAdd: Int): PlayerCity {
        return update(coins_ = coins + coinsToAdd)
    }

    fun getBurnableBuildings(color: CardColor): HashSet<Card> {
        return buildings.filter { it.color == color }

    }

    fun twoScienceCardsWithSymbol(symbol: ScienceSymbol): Boolean {
        return if (symbol == ScienceSymbol.NONE)
            false
        else
            buildings.filter { it.scienceSymbol == symbol }
                    .size() == 2
    }

    /**
     * Checks whether this city has a trading agreement for a given resource.
     *
     * @param resource the resource of interest
     * @return `true` if this city has a trading agreement for the given resource, `false` otherwise
     */
    fun hasTradingAgreement(resource : Resource) : Boolean {
        buildings.forEach {
            if (it.tradingBonuses.contains(resource)) {
                return true
            }
        }
        return false
    }

    private fun costAfterPaying(resource : Resource, cost : HashMap<Resource, Int>) : HashMap<Resource, Int> {
        val oldAmount = cost.getOrElse(resource, 0)
        return cost.put(resource, oldAmount - 1)
    }


    /**
     * Calculates the cost in coins to cover what remains of a given amount of resources after an optimal choice for
     * resource-alternative productions.
     * Note: this also considers both trading agreements and the production of the opponent city.
     *
     * @param cost the resources to cover
     * @param altProduction the alternative production choices
     * @return the minimum remaining cost to cover the given amount of resources
     */
    private fun calcMinCost(cost: HashMap<Resource, Int>, altProduction: Vector<ResourcesAlternative>,
                            opponentCity: PlayerCity) : Int {
        // Base case
        if (altProduction.isEmpty) {
            var coinCost = 0
            // Calculate coin cost of missing resources
            cost.forEach { resource, tot ->
                if (tot > 0) {
                    val opponentProduction = opponentCity.pureResourceProduction(resource)
                    val costPerUnit = if (hasTradingAgreement(resource)) 1 else 2 + opponentProduction
                    coinCost += tot * costPerUnit
                }
            }
            return coinCost
        }
        // Recursive case
        else {
            val last = altProduction.size() - 1
            val alternative = altProduction.get(last)
            val updatedProduction = altProduction.removeAt(last)

            when (alternative) {
                ResourcesAlternative.ANY -> {
                    return Stream.of(Resource.WOOD, Resource.CLAY, Resource.STONE, Resource.GLASS, Resource.PAPER)
                            .map { calcMinCost(costAfterPaying(it, cost), updatedProduction, opponentCity) }
                            .minBy { c -> c?.toFloat() ?: Float.POSITIVE_INFINITY }
                            .orNull
                }
                ResourcesAlternative.WOOD_OR_CLAY_OR_STONE -> {
                    return Stream.of(Resource.WOOD, Resource.CLAY, Resource.STONE)
                            .map { calcMinCost(costAfterPaying(it, cost), updatedProduction, opponentCity) }
                            .minBy { c -> c?.toFloat() ?: Float.POSITIVE_INFINITY }
                            .orNull
                }
                ResourcesAlternative.GLASS_OR_PAPER -> {
                    return Stream.of(Resource.GLASS, Resource.PAPER)
                            .map { calcMinCost(costAfterPaying(it, cost), updatedProduction, opponentCity) }
                            .minBy { c -> c?.toFloat() ?: Float.POSITIVE_INFINITY }
                            .orNull
                }
                else -> {
                    return calcMinCost(cost, updatedProduction, opponentCity)
                }
            }
        }
    }

    fun countBuildingsByColor(color: CardColor) : Int {
        return buildings.filter { it.color == color }.size()
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("name", name)
        generator.write("coins", coins)
        generator.writeStartArray("buildings")
        buildings.forEach { generator.write(it.name) }
        generator.writeEnd()
        generator.writeStartArray("wonders")
        wonders.forEach { generator.write(it.name) }
        generator.writeEnd()
        generator.writeStartArray("progressTokens")
        progressTokens.forEach { generator.write(it.name) }
        generator.writeEnd()
        generator.writeStartArray("unbuilt_wonders")
        unbuiltWonders.forEach { generator.write(it.name) }
        generator.writeEnd()

        generator.writeEnd()
    }

    override fun toString(): String {
        val ret = StringBuilder()
        ret.append("  Coins: $coins\n")
        if (!buildings.isEmpty) {
            ret.append("  Buildings:\n")
            CardColor.values().forEach { color ->
                val names = buildings.filter { it.color == color }
                if (!names.isEmpty) {
                    ret.append("    Color $color:\n")
                    names.forEach { ret.append("      ${it.name}\n") }
                }
            }
        }
        if (!wonders.isEmpty) {
            ret.append("  Wonders:\n")
            wonders.forEach { ret.append("    ${it.name}\n") }
            ret.append("\n")
        }
        if (!progressTokens.isEmpty) {
            ret.append("  Progress tokens:\n")
            progressTokens.forEach { ret.append("    ${it.name}\n") }
            ret.append("\n")
        }
        if (!unbuiltWonders.isEmpty) {
            ret.append("  Unbuilt wonders:\n")
            unbuiltWonders.forEach { ret.append("    ${it.name}\n") }
            ret.append("\n")
        }
        return ret.toString()
    }

    companion object {
        fun loadFromJson(obj: JSONObject): PlayerCity {
            val name = obj.getString("name")
            val coins = obj.getInt("coins")
            val buildings = HashSet.ofAll(
                    obj.getJSONArray("buildings")
                            .map { CardFactory.getByName(it as String) }
            )
            val wonders = HashSet.ofAll(
                    obj.getJSONArray("wonders")
                            .map { CardFactory.getByName(it as String) }
            )
            val progressTokens = HashSet.ofAll(
                    obj.getJSONArray("progressTokens")
                            .map { CardFactory.getByName(it as String) }
            )
            val unbuiltWonders = HashSet.ofAll(
                    obj.getJSONArray("unbuilt_wonders")
                            .map { CardFactory.getByName(it as String) }
            )
            return PlayerCity(
                    name = name,
                    coins = coins,
                    buildings = buildings,
                    wonders = wonders,
                    progressTokens = progressTokens,
                    unbuiltWonders = unbuiltWonders
            )
        }
    }
}