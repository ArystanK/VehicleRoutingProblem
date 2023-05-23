import linear_programming.travelingSalesmanProblemLinearProgramming

fun Map<Int, Map<Int, Boolean>>.connectComponents(
    distanceMatrix: Array<DoubleArray>,
): Map<Int, Map<Int, Boolean>> {
    val components = getConnectedComponents()
    if (components.size == 1) return this
    val newDistanceMatrix = Array(components.size) { i ->
        DoubleArray(components.size) { j ->
            distance(components[i], components[j], distanceMatrix)
        }
    }
    val adjacencyMatrix = travelingSalesmanProblemLinearProgramming(newDistanceMatrix).toRoute(false)
    val orderedComponents = components.order(adjacencyMatrix).flatten()
    val newAdjacencyMatrix = map { (i, row) -> i to row }.toMap().toMutableMap()
    orderedComponents.windowed(2).forEach {
        newAdjacencyMatrix[it.first()] = newAdjacencyMatrix.getOrDefault(it.first(), mutableMapOf()) + (it[1] to true)
    }
    return newAdjacencyMatrix
}

private fun Map<Int, Map<Int, Boolean>>.getConnectedComponents(): List<Pair<Int, Int>> {
    val visited = mutableSetOf<Int>()
    val components = mutableListOf<Pair<Int, Int>>()

    for (vertex in keys) {
        if (vertex !in visited) {
            val component = dfs(this, vertex, visited)
            components.add(component)
        }
    }

    return components
}

private fun dfs(adjacencyMatrix: Map<Int, Map<Int, Boolean>>, vertex: Int, visited: MutableSet<Int>): Pair<Int, Int> {
    visited.add(vertex)
    var start = vertex
    var end = vertex

    for (neighbor in adjacencyMatrix[vertex].orEmpty().keys) {
        if (adjacencyMatrix[vertex]?.get(neighbor) == true && neighbor !in visited) {
            val component = dfs(adjacencyMatrix, neighbor, visited)
            start = minOf(start, component.first)
            end = maxOf(end, component.second)
        }
    }

    return start to end
}


private fun distance(pair: Pair<Int, Int>, pair1: Pair<Int, Int>, distanceMatrix: Array<DoubleArray>): Double {
    return (distanceMatrix[pair.first][pair1.first] + distanceMatrix[pair.second][pair1.second]) / 2

}

private fun Map<Int, Map<Int, Boolean>>.toRoute(): List<Int> {
    val route = mutableListOf<Int>()
    fun dfs(node: Int, visited: MutableSet<Int> = mutableSetOf()) {
        route.add(node)
        visited.add(node)
        for (neighbor in keys) if (this[node]?.get(neighbor) == true && neighbor !in visited) dfs(neighbor, visited)
    }
    // find a node with degree = 1
    // run dfs starting with this node
    val start = keys.firstOrNull { get(it)!!.entries.sumOf { if (it.value) 1.toInt() else 0 } == 1 } ?: 0
    dfs(start)
    return route
}

fun Array<BooleanArray>.toRoute(addLastToCycle: Boolean = true): List<Int> {
    val route = mutableListOf<Int>()
    fun dfs(node: Int, visited: MutableSet<Int> = mutableSetOf()) {
        route.add(node)
        visited.add(node)
        for (neighbor in indices)
            if (this[node][neighbor] && neighbor !in visited) dfs(neighbor, visited)
    }
    // find a node with degree = 1
    // run dfs starting with this node
    val start = indices.firstOrNull { get(it).sumOf { if (it) 1.toInt() else 0 } == 2 } ?: return emptyList()
    dfs(start)
    if (addLastToCycle) route.add(start)
    return route
}

private fun Array<BooleanArray>.diagonalize(): Array<BooleanArray> =
    Array(size) { i -> BooleanArray(size) { j -> this[i][j] || this[j][i] } }


private fun Array<BooleanArray>.filterFalse(): Map<Int, Map<Int, Boolean>> {
    val rowsWithOnlyFalse = withIndex().filter { booleans -> booleans.value.all { !it } }.map { it.index }
    val result = mutableMapOf<Int, Map<Int, Boolean>>()
    for (i in indices) {
        val row = mutableMapOf<Int, Boolean>()
        if (i in rowsWithOnlyFalse) continue
        for (j in indices) {
            if (j in rowsWithOnlyFalse) continue
            row[j] = this[i][j]
        }
        result[i] = row
    }
    return result
}

fun <T> Array<Array<Array<T>>>.toAdjacencyMatrices(f: (T) -> Boolean): Array<Array<BooleanArray>> =
    Array(first().first().size) { k ->
        Array(size) { i ->
            BooleanArray(size) { j ->
                f(this[i][j][k])
            }
        }
    }

