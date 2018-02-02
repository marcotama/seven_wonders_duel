package com.aigamelabs.swduel

import com.aigamelabs.utils.Graph
import io.vavr.collection.Vector
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphTest : Spek({
    describe("a graph") {
        /*
              0
             / \
            1   2
           / \ / \
          3   4   5   7
                   \ /
                    6
         */
        val graph = Graph<String>(8)
                .setVertex(0, "A")
                .setVertex(1, "B")
                .setVertex(2, "C")
                .setVertex(3, "D")
                .setVertex(4, "E")
                .setVertex(5, "F")
                .setVertex(6, "G")
                .setVertex(7, "H")
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(1, 3)
                .addEdge(1, 4)
                .addEdge(2, 4)
                .addEdge(2, 5)
                .addEdge(5, 6)
                .addEdge(7, 6)


        on("set vertex") {
            val idx = 0
            val newValue = "root"
            val newGraph = graph.setVertex(idx, newValue)

            it("should return a new graph where the vertex is renamed") {
                assertEquals(newGraph.vertices[idx], newValue)
            }
        }

        on("replace vertex") {
            val oldValue = "A"
            val idx = graph.vertices.indexOf(oldValue)
            val newValue = "root"
            val newGraph = graph.replaceVertex(oldValue, newValue)

            it("should return a new graph where the vertex is renamed") {
                assertEquals(newGraph.vertices[idx], newValue)
            }

        }

        on("add edge") {
            val i = 3
            val j = 4
            val newGraph = graph.addEdge(i, j)

            it("should return a new graph with the edge added") {
                assertTrue(newGraph.isEdge(i, j))
            }
        }

        on("remove edge") {
            val i = 2
            val j = 5
            val newGraph = graph.removeEdge(i, j)

            it("should return a new graph with the edge removed") {
                assertTrue(!newGraph.isEdge(i, j))
            }
        }

        on("query on existence of an edge") {
            it("should return true if the edge exists, false otherwise") {
                assertTrue(graph.isEdge(1, 4))
                assertTrue(!graph.isEdge(4, 6))
            }
        }

        on("query for vertices with no incoming edges") {
            it("should return the vertices with no incoming edges") {
                val answer : Vector<String> = graph.verticesWithNoIncomingEdges()
                assertTrue(answer.containsAll(listOf("A", "H")))
                assertEquals(answer.size(), 2)
            }
        }

        on("query for incoming edges of a given vertex") {
            it("should return the departure vertices of the incoming edges") {
                val answer : Vector<Int> = graph.getIncomingEdges(4)
                assertTrue(answer.containsAll(listOf(1, 2)))
                assertEquals(answer.size(), 2)
            }

        }

        on("query for outgoing edges of a given vertex") {
            it("should return the arrival vertices of the outgoing edges") {
                val answer : Vector<Int> = graph.getOutgoingEdges(2)
                assertTrue(answer.containsAll(listOf(4, 5)))
                assertEquals(answer.size(), 2)
            }

        }
    }
})