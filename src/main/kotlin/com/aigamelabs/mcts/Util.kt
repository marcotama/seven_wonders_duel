package com.aigamelabs.mcts

import java.util.Random
import java.util.Arrays
import java.util.stream.IntStream

/**
 * Provides some utility functions.
 */
class Util {
    companion object {
        fun indexOfMin(array: DoubleArray): Int {
            val minValue = Arrays.stream(array)
                    .boxed()
                    .filter { d -> !d!!.isNaN() }
                    .min(Comparator.naturalOrder<Double>())
                    .orElse(java.lang.Double.NaN)
            val indicesOfMin = IntStream.range(0, array.size)
                    .filter { i -> array[i] == minValue }
                    .toArray()
            return if (indicesOfMin.isEmpty())
                Random().nextInt(array.size)
            else
                indicesOfMin[Random().nextInt(indicesOfMin.size)]
        }

        fun indexOfMax(array: DoubleArray): Int {
            val maxValue = Arrays.stream(array)
                    .boxed()
                    .filter { !it.isNaN() }
                    .max(Comparator.naturalOrder<Double>())
                    .orElse(java.lang.Double.NaN)
            val indicesOfMax = IntStream.range(0, array.size)
                    .filter { i -> array[i] == maxValue }
                    .toArray()
            return if (indicesOfMax.isEmpty())
                Random().nextInt(array.size)
            else
                indicesOfMax[Random().nextInt(indicesOfMax.size)]
        }

        fun indexOfMedian(array: DoubleArray): Int {
            return indexOfRank(array, array.size / 2)
        }

        fun indexOfRank(array: DoubleArray, rank: Int): Int {
            val sortedIndices = IntStream.range(0, array.size)
                    .boxed()
                    .sorted(Comparator.comparing<Int, Double> { i -> array[i] })
                    .mapToInt { ele -> ele }
                    .toArray()
            return sortedIndices[rank]
        }
    }
}