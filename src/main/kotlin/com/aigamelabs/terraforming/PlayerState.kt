package com.aigamelabs.terraforming

import com.aigamelabs.game.Action
import com.aigamelabs.terraforming.enums.*
import io.vavr.Tuple2
import io.vavr.collection.Vector
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet
import org.json.JSONObject
import javax.json.stream.JsonGenerator

data class PlayerState(
        val name : String,
        val tr : Int,
        val production: HashMap<Resource, Int>,
        val inventory: HashMap<Resource, Int>,
        val cardsInHand : HashSet<Card>,
        val playedCards: HashSet<Card>,
        val activatedCards: HashSet<Card>
) {

    constructor(name : String) : this(name, 0, HashMap.empty(), HashMap.empty(), HashSet.empty(), HashSet.empty(), HashSet.empty())

    fun update(
            name_ : String? = null,
            tr_ : Int? = null,
            production_ : HashMap<Resource, Int>? = null,
            inventory_ : HashMap<Resource, Int>? = null,
            cardsInHand_: HashSet<Card>? = null,
            playedCards_ : HashSet<Card>? = null,
            cardsAlreadyActivatedInThisTurn_ : HashSet<Card>? = null
    ) : PlayerState {
        return PlayerState(
                name_ ?: name,
                tr_ ?: tr,
                production_ ?: production,
                inventory_ ?: inventory,
                cardsInHand_ ?: cardsInHand,
                playedCards_ ?: playedCards,
                cardsAlreadyActivatedInThisTurn_ ?: activatedCards
        )
    }

    /**
     * Applies production, therefore increasing resources in the inventory
     */
    fun applyProduction() : PlayerState {
        val updatedInventory = HashMap.ofAll(
                inventory
                .toStream()
                .toJavaMap { Tuple2(it._1, it._2 + production.getOrElse(it._1, 0)) }
        )
        return update(inventory_ = updatedInventory)
    }

    fun getLegalActions() : Vector<Action<GameState>> {
        return Vector.of(
            // sell card (all cards)
            // 11 -> increase energy production
            // 14 -> increase temperature
            // 18 -> place ocean tile
            // 23 -> place greenery tile
            // 25 -> place city tile
            // activate card
            // play a card (affordable cards)
            // pass -> reset activated cards
        )
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("name", name)
        generator.write("tr", tr)
        generator.writeStartObject("production")
        production.forEach { generator.write(it._1.toString(), it._2) }
        generator.writeEnd()
        generator.writeStartObject("inventory")
        inventory.forEach { generator.write(it._1.toString(), it._2) }
        generator.writeEnd()
        generator.writeStartArray("cards_in_hand")
        cardsInHand.forEach { generator.write(it.name) }
        generator.writeEnd()
        generator.writeStartArray("played_cards")
        playedCards.forEach { generator.write(it.name) }
        generator.writeEnd()
        generator.writeStartArray("activated_cards")
        activatedCards.forEach { generator.write(it.name) }
        generator.writeEnd()

        generator.writeEnd()
    }

    override fun toString(): String {
        val ret = StringBuilder()
        ret.append("  TR: $tr\n")
        ret.append("  Production:\n")
        production.fold("") { acc, it ->
            val player = it._1
            val score = it._2
            "$acc\n  $player: $score"
        }
        ret.append("  Inventory:\n")
        inventory.fold("") { acc, it ->
            val player = it._1
            val score = it._2
            "$acc\n  $player: $score"
        }
        ret.append("  Cards in hand:\n")
        cardsInHand.forEach { ret.append("    ${it.name}\n") }
        ret.append("\n")
        ret.append("  Played cards:\n")
        playedCards.forEach { ret.append("    ${it.name}\n") }
        ret.append("\n")
        ret.append("  Activated cards:\n")
        activatedCards.forEach { ret.append("    ${it.name}\n") }
        ret.append("\n")
        return ret.toString()
    }

    companion object {
        fun loadFromJson(obj: JSONObject): PlayerState {
            val name = obj.getString("name")
            val tr = obj.getInt("tr")

            val productionObj = obj.getJSONObject("production")
            val production = HashMap.ofAll(productionObj.toMap()
                    .map { Pair(getResourceFromString(it.key), (it.value as Int)) }
                    .toMap()
            )
            val inventoryObj = obj.getJSONObject("inventory")
            val inventory = HashMap.ofAll(inventoryObj.toMap()
                    .map { Pair(getResourceFromString(it.key), (it.value as Int)) }
                    .toMap()
            )
            val cardsInHand = HashSet.ofAll(
                    obj.getJSONArray("cards_in_hand")
                            .map { CardFactory.getByName(it as String) }
            )
            val playedCards = HashSet.ofAll(
                    obj.getJSONArray("played_cards")
                            .map { CardFactory.getByName(it as String) }
            )
            val activatedCards = HashSet.ofAll(
                    obj.getJSONArray("played_cards")
                            .map { CardFactory.getByName(it as String) }
            )
            return PlayerState(
                    name = name,
                    tr = tr,
                    production = production,
                    inventory = inventory,
                    cardsInHand = cardsInHand,
                    playedCards = playedCards,
                    activatedCards = activatedCards
            )
        }
    }
}


fun getResourceFromString(s: String): Resource {
    return when (s) {
        "MEGACREDIT" -> Resource.MEGACREDIT
        "STEEL" -> Resource.STEEL
        "TITANIUM" -> Resource.TITANIUM
        "PLANT" -> Resource.PLANT
        "ENERGY" -> Resource.ENERGY
        "HEAT" -> Resource.HEAT
        else -> throw Exception("Unknown resource $s")
    }
}