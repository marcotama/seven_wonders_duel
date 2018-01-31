package com.aigamelabs.swduel

import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.utils.Graph
import io.vavr.collection.Vector
import javax.json.stream.JsonGenerator

class CardStructure(var graph: Graph<CardPlaceholder>, private var faceDownPool: Deck) {
    fun pickUpCard(card: Card, generator : RandomWithTracker?) : CardStructure{
        val i = graph.vertices.indexOf(card)
        if (i == -1) {
            throw Exception("Element not found in graph")
        }
        else {
            // Remove card from the vertices list
            var newGraph = graph.setVertex(i, null)
            // Turn face-up previously covered cards
            val previouslyCoveredCardsIdx = graph.getOutgoingEdges(i)
            var newFaceDownPool = faceDownPool
            previouslyCoveredCardsIdx.forEach {
                newGraph = newGraph.removeEdge(i, it)
                if (newGraph.getIncomingEdges(it).size() == 0 && newGraph.vertices[it] !is Card) {
                    val drawOutcome = newFaceDownPool.drawCard(generator)
                    val drawnCard = drawOutcome.first
                    newFaceDownPool = drawOutcome.second
                    newGraph = newGraph.setVertex(it, drawnCard)
                }
            }
            return CardStructure(newGraph, newFaceDownPool)
        }
    }

    fun availableCards() : Vector<Card> {
        val availableCards = graph.verticesWithNoIncomingEdges()
                .map { it as Card }
        return availableCards as Vector<Card>
    }

    fun isEmpty() : Boolean{
        return graph.vertices.filter { it != null }.isEmpty
    }

    /**
     * Dumps the object content in JSON. Assumes the object structure is opened and closed by the caller.
     */
    fun toJson(generator: JsonGenerator, name: String?) {
        if (name == null) generator.writeStartObject()
        else generator.writeStartObject(name)

        faceDownPool.toJson(generator, "face_down_pool")
        graph.toJson(generator, "graph")

        generator.writeEnd()
    }

    override fun toString(): String {
        return "$graph\n\nFace-down cards pool:\n" +
                faceDownPool.cards.map { "  ${it.name}\n" }
                .fold("", { acc, s -> "$acc$s"}) + "\n"
    }
}