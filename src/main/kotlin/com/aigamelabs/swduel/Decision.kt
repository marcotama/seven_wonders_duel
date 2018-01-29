package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.actions.Action
import io.vavr.collection.Vector
import javax.json.stream.JsonGenerator

/**
 * Represents a decision to be made. It includes a list of actions to choose from and the player who will make the
 * decision.
 */
data class Decision(val player: PlayerTurn, val options: Vector<Action>, val addedBy: String) {

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson (generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        generator.write("player", player.toString())
        generator.writeStartArray("options")
        options.forEach { it.toString() }
        generator.writeEnd()

        generator.writeEnd()
    }
}

