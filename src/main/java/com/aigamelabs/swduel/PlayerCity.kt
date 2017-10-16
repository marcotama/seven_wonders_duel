package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.*
import io.vavr.collection.*

data class PlayerCity(
        val name : String,
        val coins : Int,
        val buildings : HashSet<Card>,
        val wonders : HashSet<Card>,
        val wondersDeck : Deck,
        val opponentCity : PlayerCity?
) {

    constructor(name : String) : this(name, 7, HashSet.empty(), HashSet.empty(), Deck("Wonders deck of " + name), null)

    fun update(
            name_ : String? = null,
            coins_ : Int? = null,
            buildings_ : HashSet<Card>? = null,
            wonders_ : HashSet<Card>? = null,
            wondersDeck_ : Deck? = null,
            opponentCity_ : PlayerCity? = null
    ) : PlayerCity {
        return PlayerCity(
                name_ ?: name,
                coins_ ?: coins,
                buildings_ ?: buildings,
                wonders_ ?: wonders,
                wondersDeck_ ?: wondersDeck,
                opponentCity_ ?: opponentCity
        )
    }

    fun setOpponentCity(oppCity : PlayerCity) : PlayerCity{
        return PlayerCity(name, coins, buildings, wonders, wondersDeck, oppCity)
    }

    /**
     * Calculates whether this city can afford building a given new building and returns the cost associated with an
     * optimal choice of alternatively produced resources.
     */
    fun canBuild(newBuilding: Card) : Int? {
        var resourceCost = newBuilding.resourceCost

        val resourceProduction = HashMap<Resource, Int>()
        var altProduction : Vector<ResourcesAlternative> = Vector.empty()
        for (building in buildings.plus(wonders)) {
            // Check linking symbols
            if (newBuilding.linksTo == building.linkingSymbol) {
                return 0
            }
            // Calculate resources production
            building.resourceProduction.forEach { resource, tot ->
                resourceCost = resourceCost.put(resource, resourceProduction.getOrDefault(resource, 0) - tot)
            }
            // Calculate resource alternatives production
            if (building.resourceAlternativeProduction != ResourcesAlternative.NONE) {
                altProduction = altProduction.append(building.resourceAlternativeProduction)
            }
        }

        val minCost = calcMinCost(resourceCost, altProduction) + newBuilding.coinCost
        return if (minCost >= coins) null else minCost

    }

    fun buildWonder (card: Card) {
        wondersDeck.removeCard(card)
        wonders.add(card)
    }



    /**
     * Calculates the production of a given resource from brown/gray cards.
     */
    private fun pureResourceProduction(resource : Resource) : Int {
        var resourceProduction = 0
        buildings.forEach { building ->
            building.resourceProduction.forEach {r, tot ->
                resourceProduction += if (r == resource) tot else 0
            }
        }
        return resourceProduction
    }

    /**
     * Checks whether this city has a trading agreement for a given resource.
     */
    private fun hasTradingAgreement(resource : Resource) : Boolean {
        buildings.forEach { building ->
            if (building.tradingBonuses.contains(resource)) {
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
     * Calculates the minimum cost in coins to cover a given amount of resources given a set of resource-alternatives
     * productions. Note: this considers both trading agreements and the production of the opponent city.
     */
    private fun calcMinCost(cost: HashMap<Resource, Int>, altProduction: Vector<ResourcesAlternative>) : Int {
        // Base case
        if (altProduction.isEmpty) {
            var coinCost = 0
            // Calculate coin cost of missing resources
            cost.forEach { resource, tot ->
                if (tot > 0) {
                    val opponentProduction = opponentCity!!.pureResourceProduction(resource)
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
            val newProduction = altProduction.removeAt(last)

            when (alternative) {
                ResourcesAlternative.WOOD_OR_CLAY_OR_STONE -> {
                    return Stream.of(Resource.WOOD, Resource.CLAY, Resource.STONE)
                            .map { resource -> calcMinCost(costAfterPaying(resource, cost), newProduction) }
                            .minBy { c -> c?.toFloat() ?: Float.POSITIVE_INFINITY }
                            .orNull
                }
                ResourcesAlternative.GLASS_OR_PAPER -> {
                    return Stream.of(Resource.GLASS, Resource.PAPER)
                            .map { resource -> calcMinCost(costAfterPaying(resource, cost), newProduction) }
                            .minBy { c -> c?.toFloat() ?: Float.POSITIVE_INFINITY }
                            .orNull
                }
                else -> {
                    return calcMinCost(cost, newProduction)
                }
            }
        }
    }
}