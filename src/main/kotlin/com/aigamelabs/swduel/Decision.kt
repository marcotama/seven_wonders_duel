package com.aigamelabs.swduel

import com.aigamelabs.swduel.actions.Action
import com.aigamelabs.swduel.enums.PlayerTurn
import io.vavr.collection.Vector

/**
 * Represents a decision to be made. It includes a list of actions to choose from and the player who will make the
 * decision.
 */
data class Decision(val player: PlayerTurn, val options: Vector<Action>, val isMainTurn : Boolean)

