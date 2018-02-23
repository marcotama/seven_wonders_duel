package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.swduel.enums.CardGroup
import com.aigamelabs.utils.Deck
import com.aigamelabs.utils.Graph
import io.vavr.collection.HashSet
import io.vavr.collection.Vector

object CardStructureFactory {


    private fun makeCardStructure(origDeck : Deck<Card>, faceUpCardsIdx: HashSet<Int>, connections: HashSet<Pair<Int,Int>>,
                                  faceDownCard: FaceDownCard, generator : RandomWithTracker) : CardStructure {

        val numVertices = origDeck.size()

        // Set edges
        val edges : MutableList<Boolean> = (0 until numVertices * numVertices).map { false }.toMutableList()
        connections.forEach { (i, j) -> edges[Graph.toIndex(i, j, origDeck.size())] = true }

        // Set vertices
        val drawOutcome = origDeck.drawCards(faceUpCardsIdx.size(), generator)
        val vertices : MutableList<CardPlaceholder?> = (0 until numVertices).map { faceDownCard }.toMutableList()
        faceUpCardsIdx.zipWithIndex().forEach { vertices[it._1] = drawOutcome.first.get(it._2) }

        val deckAfterDraw = drawOutcome.second
        val newGraph : Graph<CardPlaceholder> = Graph(Vector.ofAll(vertices), Vector.ofAll(edges))

        return CardStructure(newGraph, deckAfterDraw)

    }


    fun makeFirstAgeCardStructure(generator : RandomWithTracker) : CardStructure {
        /*
                18  19
              15  16  17
            11  12  13  14
          06  07  08  09  10
        00  01  02  03  04  05
         */

        // Set edges
        val edges = HashSet.of(
                Pair(0,6), Pair(1,6), Pair(1,7), Pair(2,7), Pair(2,8), Pair(3,8), Pair(3,9), Pair(4,9), Pair(4,10), Pair(5,10),
                Pair(6,11), Pair(7,11), Pair(7,12), Pair(8,12), Pair(8,13), Pair(9,13), Pair(9,14), Pair(10,14),
                Pair(11,15), Pair(12,15), Pair(12,16), Pair(13,16), Pair(13,17), Pair(14,17),
                Pair(15,18), Pair(16,18), Pair(16,19), Pair(17,19)
        )

        // Set vertices
        val vertices = HashSet.of(
                0, 1, 2, 3, 4, 5, 11, 12, 13, 14, 18, 19
        )

        return makeCardStructure(DeckFactory.createFirstAgeDeck(), vertices, edges, FaceDownCard(CardGroup.FIRST_AGE), generator)

    }

    fun makeSecondCardStructure(generator : RandomWithTracker) : CardStructure {
        /*
        14  15  16  17  18  19
          09  10  11  12  13
            05  06  07  08
              02  03  04
                00  01
         */

        // Set edges
        val edges = HashSet.of(
                Pair(0,2), Pair(0,3), Pair(1,3), Pair(1,4),
                Pair(2,5), Pair(2,6), Pair(3,6), Pair(3,7), Pair(4,7), Pair(4,8),
                Pair(5,9), Pair(5, 10), Pair(6,10), Pair(6,11), Pair(7,11), Pair(7,12), Pair(8,12), Pair(8,13),
                Pair(9,14), Pair(9,15), Pair(10,15), Pair(10,16), Pair(11,16), Pair(11,17), Pair(12,17), Pair(12,18), Pair(13,18), Pair(13,19)
        )

        // Set vertices
        val vertices = HashSet.of(
                0, 1, 5, 6, 7, 8, 14, 15, 16, 17, 18, 19
        )

        return makeCardStructure(DeckFactory.createSecondAgeDeck(), vertices, edges, FaceDownCard(CardGroup.SECOND_AGE), generator)
    }

    fun makeThirdAgeCardStructure(generator : RandomWithTracker) : CardStructure {
        /*
                18  19
              15  16  17
            11  12  13  14
              09      10
            05  06  07  08
              02  03  04
                00  01
         */

        // Set edges
        val edges = HashSet.of(
                Pair(0,2), Pair(0,3), Pair(1,3), Pair(1,4),
                Pair(2,5), Pair(2,6), Pair(3,6), Pair(3,7), Pair(4,7), Pair(4,8),
                Pair(5,9), Pair(6,9), Pair(7,10), Pair(8,10),
                Pair(9,11), Pair(9,12), Pair(10,13), Pair(10,14),
                Pair(11,15), Pair(12,15), Pair(12,16), Pair(13,16), Pair(13,17), Pair(14,17),
                Pair(15,18), Pair(16,18), Pair(16,19), Pair(17,19)
        )

        // Set vertices
        val vertices = HashSet.of(
                0, 1, 5, 6, 7, 8, 11, 12, 13, 14, 18, 19
        )

        return makeCardStructure(DeckFactory.createThirdAgeDeck(), vertices, edges, FaceDownCard(CardGroup.THIRD_AGE), generator)
    }
}