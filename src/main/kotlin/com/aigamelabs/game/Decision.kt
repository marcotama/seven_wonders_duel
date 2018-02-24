package com.aigamelabs.game

import io.vavr.collection.Vector
import javax.json.stream.JsonGenerator

/**
 * Represents a decision to be made. It includes a list of actions to choose from and the player who will make the
 * decision.
 */
data class Decision<T: AbstractGameState<T>>(val player: PlayerTurn, val options: Vector<out Action<T>>) {

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    override fun toString (): String {
        return "Decision for player $player; options:" +
                options.map { "  $it\n" }.fold("", {acc, s -> "$acc$s"})
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson (generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("player", player.toString())
        generator.writeStartArray("options")
        options.forEach { generator.write(it.toString()) }
        generator.writeEnd()

        generator.writeEnd()
    }
}

