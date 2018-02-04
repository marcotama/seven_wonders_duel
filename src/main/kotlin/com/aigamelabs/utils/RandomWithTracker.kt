package com.aigamelabs.utils

import java.util.Random
import java.util.LinkedList

/**
 * Provides facilities to generate random integers and shuffle lists of integers.
 * Remembers all generated numbers (directly or indirectly by the shuffle function).
 */
class RandomWithTracker(seed: Long, private val disableTracking: Boolean = false) {
    private val generator: Random = Random(seed)
    private val memory: LinkedList<Int> = LinkedList()

    fun nextInt(limit: Int): Int {
        val generated = generator.nextInt(limit)
        if (!disableTracking)
            memory.add(generated)
        return generated
    }

    /**
     * Returns a random integer between 0 and weights.size() - 1. The weights vector is normalized and used as weights
     *
     */
    fun nextInt(weights: Iterable<Int>): Int {
        val tot = weights.sum()
        var num = nextInt(tot)
        weights.forEachIndexed { idx, weight ->
            if (num > weight)
                num -= weight
            else {
                memory[memory.size - 1] = idx // replace the actual random number with one that represents the outcome more neatly
                return idx
            }
        }
        throw Exception("This should not happen")
    }

    fun popAll(): List<Int> {
        val ret = memory.toList()
        memory.clear()
        return ret
    }

    fun isEmpty(): Boolean {
        return memory.isEmpty()
    }
}