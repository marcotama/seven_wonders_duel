package com.aigamelabs.swduel

import io.vavr.collection.Vector

data class Graph<T>(private val vertices: Vector<T?>, private val adjMatrix : Vector<Boolean>) {
    private val numVertices : Int = vertices.size()

    constructor(numVertices: Int) :
            this(Vector.fill(numVertices, {null}), Vector.fill(numVertices * numVertices, {false}))


    fun setElement(i: Int, e: T?) : Graph<T> {
        return if (vertices[i] == e) {
            this
        }
        else {
            val newVertices = vertices.toJavaList()
            newVertices[i] = e
            Graph(Vector.ofAll(newVertices), adjMatrix)
        }
    }


    fun addEdge(i: Int, j: Int) : Graph<T> {
        val k = i * numVertices + j
        return if (adjMatrix[k]) {
            this
        }
        else {
            val newAdjMatrix = adjMatrix.toJavaList()
            newAdjMatrix[k] = true
            Graph(vertices, Vector.ofAll(newAdjMatrix))
        }
    }

    fun removeEdge(i: Int, j: Int) : Graph<T> {
        val k = i * numVertices + j
        return if (!adjMatrix[k]) {
            this
        }
        else {
            val newAdjMatrix = adjMatrix.toJavaList()
            newAdjMatrix[k] = false
            Graph(vertices, Vector.ofAll(newAdjMatrix))
        }
    }

    fun isEdge(i: Int, j: Int) : Boolean {
        val k = i * numVertices + j
        return adjMatrix[k]
    }

}