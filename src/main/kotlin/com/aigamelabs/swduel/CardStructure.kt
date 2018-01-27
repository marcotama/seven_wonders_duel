package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.utils.Graph
import io.vavr.collection.Vector
import javax.json.stream.JsonGenerator

class CardStructure(var graph: Graph<CardPlaceholder>, var faceDownPool: Deck) {
    fun pickUpCard(card: Card, generator : RandomWithTracker?) : CardStructure{
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
                val drawOutcome = faceDownPool.drawCard(generator)
                faceDownPool = drawOutcome.second
                newVertices[j] = drawOutcome.first
            }
            graph = Graph(Vector.ofAll(newVertices), graph.adjMatrix)
        }
        return CardStructure(graph, faceDownPool)
    }

    fun availableCards() : Vector<Card> {
        val availableCards = graph.verticesWithNoIncomingEdges()
                .map { cp -> cp as Card }
        return availableCards as Vector<Card>
    }

    fun isEmpty() : Boolean{
        return graph.vertices.filter { c -> c != null }.isEmpty
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator) {
        generator.writeStartObject("face_down_pool")
        faceDownPool.toJson(generator)
        generator.writeEnd()
        generator.writeStartObject("graph")
        graph.toJson(generator)
        generator.writeEnd()
    }
}