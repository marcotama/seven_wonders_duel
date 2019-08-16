package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.CardGroup
import com.aigamelabs.utils.Deck
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.utils.Graph
import io.vavr.collection.Vector
import org.json.JSONArray
import org.json.JSONObject
import javax.json.stream.JsonGenerator

class CardStructure(var graph: Graph<CardPlaceholder>, var faceDownPool: Deck<Card>) {
    fun pickUpCard(card: Card, generator : RandomWithTracker) : CardStructure{
        val i = graph.vertices.indexOf(card)
        if (i == -1) {
            throw Exception("Element not found in graph")
        }
        else {
            // Remove card from the vertices list
            var updatedGraph = graph.setVertex(i, null)
            // Turn face-up previously covered cards
            val previouslyCoveredCardsIdx = graph.getOutgoingEdges(i)
            var updatedFaceDownPool = faceDownPool
            previouslyCoveredCardsIdx.forEach {
                updatedGraph = updatedGraph.removeEdge(i, it)
                if (updatedGraph.getIncomingEdges(it).size() == 0 && updatedGraph.vertices[it] !is Card) {
                    val drawOutcome = updatedFaceDownPool.drawCard(generator)
                    val drawnCard = drawOutcome.first
                    updatedFaceDownPool = drawOutcome.second
                    updatedGraph = updatedGraph.setVertex(it, drawnCard)
                }
            }
            return CardStructure(updatedGraph, updatedFaceDownPool)
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


        generator.writeStartObject("graph")
        generator.writeStartArray("vertices")
        graph.vertices.forEach { generator.write(it.toString()) }
        generator.writeEnd() // vertices
        generator.writeStartArray("edges")
        (0 until graph.numVertices * graph.numVertices)
                .filter { graph.adjMatrix[it] }
                .map { Graph.toCoords(it, graph.numVertices) }
                .forEach {
                    generator.writeStartArray()
                    generator.write(it.first)
                    generator.write(it.second)
                    generator.writeEnd()
                }
        generator.writeEnd() // edges
        generator.writeEnd() // graph

        generator.writeEnd()
    }

    override fun toString(): String {
        return "$graph\n\nFace-down cards pool:\n" +
                faceDownPool.cards.map { "  ${it.name}\n" }
                .fold("") { acc, s -> "$acc$s"} + "\n"
    }

    companion object {
        fun loadFromJson(obj: JSONObject): CardStructure {
            val faceDownPool = loadDeckFromJson(obj.getJSONObject("face_down_pool"))

            val graphObj = obj.getJSONObject("graph")
            val verticesObj = graphObj.getJSONArray("vertices")
            val vertices = Vector.ofAll<CardPlaceholder>(verticesObj.map {
                it as String
                when (it) {
                    "null" -> null
                    "Face down card" -> FaceDownCard(CardGroup.FIRST_AGE)
                    else -> CardFactory.getByName(it)
                }
            })

            val edgesObj = graphObj.getJSONArray("edges")
            val numVertices = vertices.size()
            val edges = (0 until numVertices * numVertices).map { false }.toMutableList()
            Vector.of(edgesObj.forEach {
                it as JSONArray
                val orig = it[0] as Int
                val dest = it[1] as Int
                edges[Graph.toIndex(orig, dest, numVertices)] = true
            })

            val graph = Graph(vertices, Vector.ofAll(edges))
            return CardStructure(graph, faceDownPool)
        }
    }
}