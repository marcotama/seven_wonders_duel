package com.aigamelabs.utils

import io.vavr.collection.Stream
import io.vavr.collection.Vector

/**
 * Implements a graph using an adjacency matrix.
 */
data class Graph<T>(val vertices: Vector<T?>, val adjMatrix : Vector<Boolean>) {
    private val numVertices : Int = vertices.size()

    /**
     * Creates an empty graph with the given capacity
     */
    constructor(numVertices: Int) :
            this(Vector.fill(numVertices, {null}), Vector.fill(numVertices * numVertices, {false}))

    /**
     * Converts a pair of vertex indices (i, j) to an index that can be used to access the adjacency matrix (which is
     * stored as a vector).
     *
     * @param i the origin of the edge
     * @param j the destination of the edge
     * @return an index that can be used to access the adjacency matrix.
     */
    fun toIndex(i: Int, j: Int) : Int {
        return i * numVertices + j
    }

    /**
     * Converts an adjacency matrix index to a pair of indices (i, j) representing an edge.
     *
     * @param k the adjacency matrix index
     * @return two vertex indices representing an edge
     */
    fun toCoords(k: Int) : Pair<Int, Int> {
        return Pair(k / numVertices, k % numVertices)
    }

    /**
     * Stores the given element as the i-th vertex.
     *
     * @param i the index in which to store the given element.
     * @param e the element to be stored
     * @return a new instance updated as requested
     */
    fun setVertex(i: Int, e: T?) : Graph<T> {
        return if (vertices[i] == e) {
            this
        }
        else {
            val newVertices = vertices.toJavaList()
            newVertices[i] = e
            Graph(Vector.ofAll(newVertices), adjMatrix)
        }
    }

    /**
     * Replaces a given element with another given one.
     *
     * @param oe the index in which to store the given element.
     * @param ne the element to be stored
     * @return a new instance updated as requested
     */
    fun replaceVertex(oe: T?, ne: T?) : Graph<T> {
        val i = vertices.indexOf(oe)
        if (i == -1) {
            throw Exception("Element not found in graph")
        }
        else {
            return setVertex(i, ne)
        }
    }

    /**
     * Adds an edge to the graph.
     *
     * @param i the origin of the edge
     * @param j the destination of the edge
     * @return a new instance updated as requested
     */
    fun addEdge(i: Int, j: Int) : Graph<T> {
        val k = toIndex(i, j)
        return if (adjMatrix[k]) {
            this
        }
        else {
            val newAdjMatrix = adjMatrix.toJavaList()
            newAdjMatrix[k] = true
            Graph(vertices, Vector.ofAll(newAdjMatrix))
        }
    }

    /**
     * Removes an edge to the graph.
     *
     * @param i the origin of the edge
     * @param j the destination of the edge
     * @return a new instance updated as requested
     */
    fun removeEdge(i: Int, j: Int) : Graph<T> {
        val k = toIndex(i, j)
        return if (!adjMatrix[k]) {
            this
        }
        else {
            val newAdjMatrix = adjMatrix.toJavaList()
            newAdjMatrix[k] = false
            Graph(vertices, Vector.ofAll(newAdjMatrix))
        }
    }
    
    /**
     * Check if the two given nodes are connected by an edge.
     *
     * @param i the first node
     * @param j the second node
     * @return `true` if the nodes are connected by an edge, `false` otherwise
     */
    fun isEdge(i: Int, j: Int) : Boolean {
        val k = toIndex(i, j)
        return adjMatrix[k]
    }
    
    /**
     * Check if the given vertex has incoming edges.
     *
     * @param i the index of the vertex
     * @return `true` if the node has incoming edges, `false` otherwise
     */
    private fun hasIncomingEdges(i: Int) : Boolean {
        return Stream.ofAll(0 until numVertices)
                .map { j -> adjMatrix[toIndex(i, j)] }
                .fold(false, { a, b -> a || b } )
    }

    /**
     * Creates a list of nodes that do not have any incoming edges.
     * 
     * @return a list of the nodes that do not have incoming edges
     */
    fun verticesWithNoIncomingEdges() : Vector<T> {
        return Stream.ofAll(0 until numVertices)
                .filter { i -> !hasIncomingEdges(i) }
                .map { i -> vertices[i]!! }
                .toVector()
    }

    /**
     * Finds the index i of all vertices that are connected to the given vertex j via an edge (i, j).
     *
     * @param i the edge of interest
     * @return a list of vertices connected to the given one by an incoming edge
     */
    fun getIncomingEdges(i : Int) : Vector<Int> {
        return Stream.ofAll(0 until numVertices)
                .filter { j -> adjMatrix[toIndex(i, j)] }
                .toVector()
    }

    /**
     * Finds the index j of all vertices that are connected to the given vertex i via an edge (i, j).
     *
     * @param j the edge of interest
     * @return a list of vertices connected to the given one by an outgoing edge
     */
    fun getOutgoingEdges(j : Int) : Vector<Int> {
        return Stream.ofAll(0 until numVertices)
                .filter { i -> adjMatrix[toIndex(i, j)] }
                .toVector()
    }

    override fun toString() : String {
        return "Vertices: " +
                vertices.zipWithIndex()
                        .fold("", { acc, pair -> acc + "\n" + pair._1 + ": " + pair._2} ) +
        "\n\nEdges:" +
                (0 until numVertices * numVertices)
                        .filter { k -> adjMatrix[k]}
                        .map { k -> toCoords(k) }
                        .fold("", { acc, pair -> acc + "\n" + pair.first + " -> " + pair.second} )
    }

}