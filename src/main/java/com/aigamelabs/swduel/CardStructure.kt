package com.aigamelabs.swduel

import io.vavr.collection.Vector

class CardStructure(var graph: Graph<CardPlaceholder>, var faceDownPool: Deck) {
    fun pickUpCard(card: Card) : CardStructure{
        val i = graph.vertices.indexOf(card)
        if (i == -1) {
            throw Exception("Element not found in graph")
        }
        else {
            val newVertices = graph.vertices.toJavaList()
            // Remove card from the graph
            newVertices[i] = null
            // Turn face-up previously covered cards
            val previouslyCoveredCardsIdx = graph.getOutgoingEdges(i)
            previouslyCoveredCardsIdx.forEach { j ->
                val drawOutcome = faceDownPool.drawCard()
                faceDownPool = drawOutcome.second
                newVertices[j] = drawOutcome.first
            }
            graph = Graph(Vector.ofAll(newVertices), graph.adjMatrix)
        }
        return CardStructure(graph, faceDownPool)
    }
}