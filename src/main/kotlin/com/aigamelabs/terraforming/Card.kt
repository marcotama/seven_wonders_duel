package com.aigamelabs.terraforming

import com.aigamelabs.terraforming.enums.*
import javax.json.stream.JsonGenerator

data class Card(
        val id: String,
        val name: String,
        val type: CardType,
        val cost: Int,
        val deck: DeckName,
        val requiredTerraformingEffects: Map<TerraformingEffect, Int>,
        val maxTerraformingEffects: Map<TerraformingEffect, Int>,
        val terraformingEffects: Map<TerraformingEffect, Int>,
        val requiredProduction: Map<Resource, Int>,
        val requiredTags: Map<CardTag, Int>,
        val providedTags: Map<CardTag, Int>,
        val increaseProduction: Map<Resource, Int>,
        val decreaseAnyProduction: Map<Resource, Int>,
        val gives: Map<Resource, Int>,
        val ongoingEffect: String,
        val oneTimeEffect: String
        ) : CardPlaceholder () {

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson (generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("id", id)
        generator.write("name", name)
        generator.write("cost", cost.toString())
        generator.write("deck", deck.toString())
        generator.write("ongoingEffect", ongoingEffect)
        generator.write("oneTimeEffect", oneTimeEffect)
        generator.writeStartObject("requiredTerraformingEffects")
        requiredTerraformingEffects.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("maxTerraformingEffects")
        maxTerraformingEffects.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("terraformingEffects")
        terraformingEffects.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("requiredProduction")
        requiredProduction.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("requiredTags")
        requiredTags.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("providedTags")
        providedTags.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("increaseProduction")
        increaseProduction.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("decreaseAnyProduction")
        decreaseAnyProduction.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()
        generator.writeStartObject("gives")
        gives.forEach { generator.write(it.key.toString(), it.value) }
        generator.writeEnd()

        generator.writeEnd()
    }

    override fun toString(): String {
        return name
    }
}