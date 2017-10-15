package com.aigamelabs.swduel

import io.vavr.collection.Stream

object GraphFactory {


    private fun makeGraph(origDeck : Deck, vertices : Stream<Int>, edges : Stream<Pair<Int,Int>>) : Pair<Graph<Card>, Deck> {
        var graph = Graph<Card>(vertices.size())
        var deck = origDeck

        // Set edges
        edges.forEach { (i, j) -> graph = graph.addEdge(i, j) }

        // Set vertices
        vertices.forEach {
            val drawOutcome = deck.drawCard()!!
            deck = drawOutcome.second
            graph = graph.setVertex(0, drawOutcome.first)
        }

        return Pair(graph, deck)

    }


    fun makeFirstAgeGraph(firstAgeDeck : Deck) : Pair<Graph<Card>, Deck> {
        /*
                19  20
              16  17  18
            12  13  14  15
          07  08  09  10  11
        00  01  02  03  04  05
         */

        // Set edges
        val edges = Stream.of(
                Pair(0,7), Pair(1,7), Pair(1,8), Pair(2,8), Pair(2,9), Pair(3,9), Pair(3,10), Pair(4,10), Pair(4,11), Pair(5,11),
                Pair(7,12), Pair(8,12), Pair(8,13), Pair(9,13), Pair(9,14), Pair(10,14), Pair(10,15), Pair(11,15),
                Pair(12,16), Pair(13,16), Pair(13,17), Pair(14,17), Pair(14,18), Pair(15,18),
                Pair(16,19), Pair(17,19), Pair(17,20), Pair(18,20)
        )

        // Set vertices
        val vertices = Stream.of(
                0, 1, 2, 3, 4, 5, 12, 13, 14, 15, 19, 20
        )

        return makeGraph(firstAgeDeck, vertices, edges)

    }

    fun makeSecondAgeGraph(secondAgeDeck: Deck) : Pair<Graph<Card>, Deck> {
        /*
        14  15  16  17  18  19
          09  10  11  12  13
            05  06  07  08
              02  03  04
                00  01
         */

        // Set edges
        val edges = Stream.of(
                Pair(0,2), Pair(0,3), Pair(1,3), Pair(1,4),
                Pair(2,5), Pair(2,6), Pair(3,6), Pair(3,7), Pair(4,7), Pair(4,8),
                Pair(5,9), Pair(5, 10), Pair(6,10), Pair(6,11), Pair(7,11), Pair(7,12), Pair(8,12), Pair(8,13),
                Pair(9,14), Pair(9,15), Pair(10,15), Pair(10,16), Pair(11,16), Pair(11,17), Pair(12,17), Pair(12,18), Pair(13,18), Pair(13,19)
        )

        // Set vertices
        val vertices = Stream.of(
                0, 1, 5, 6, 7, 8, 14, 15, 16, 17, 18, 19
        )

        return makeGraph(secondAgeDeck, vertices, edges)
    }

    fun makeThirdAgeGraph(thirdAgeDeck: Deck) : Pair<Graph<Card>, Deck> {
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
        val edges = Stream.of(
                Pair(0,2), Pair(0,3), Pair(1,3), Pair(1,4),
                Pair(2,5), Pair(2,6), Pair(3,6), Pair(3,7), Pair(4,7), Pair(4,8),
                Pair(5,9), Pair(6,9), Pair(7,10), Pair(8,10),
                Pair(9,11), Pair(9,12), Pair(10,13), Pair(10,14),
                Pair(11,15), Pair(12,15), Pair(12,16), Pair(13,16), Pair(13,17), Pair(14,17),
                Pair(15,18), Pair(16,18), Pair(16,19), Pair(17,19)
        )

        // Set vertices
        val vertices = Stream.of(
                0, 1, 5, 6, 7, 8, 11, 12, 13, 14, 18, 19
        )

        return makeGraph(thirdAgeDeck, vertices, edges)
    }
}