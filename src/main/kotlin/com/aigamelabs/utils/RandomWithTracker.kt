package com.aigamelabs.utils

import java.util.Random
import java.util.LinkedList

/**
 * Provides facilities to generate random integers and shuffle lists of integers.
 * Remembers all generated numbers (directly or indirectly by the shuffle function).
 */
class RandomWithTracker(seed: Long) {
    private val generator: Random = Random(seed)
    private val memory: LinkedList<Int> = LinkedList()

    fun nextInt(limit: Int): Int {
        val generated = generator.nextInt(limit)
        memory.add(generated)
        return generated
    }

    private fun swap(arr: MutableList<Int>, i: Int, j: Int) {
        arr[i] = arr.set(j, arr[i])
    }

    fun shuffle(list: MutableList<Int>) {
        val size = list.size
        for (i in size downTo 2) {
            swap(list, i - 1, generator.nextInt(i))
        }

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