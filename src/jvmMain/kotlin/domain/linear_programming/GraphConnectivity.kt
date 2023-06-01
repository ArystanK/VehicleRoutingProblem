package domain.linear_programming

import multiMin
import order

fun Map<Int, Map<Int, Boolean>>.connectComponents(
    distanceMatrix: Array<DoubleArray>,
): Array<BooleanArray> {
    val components = getConnectedComponents()
    if (components.size == 1) return toAdjacencyMatrix()
    val componentStartAndEnds = components.map {
        it.first() to it.last()
    }
    val newDistanceMatrix = Array(components.size) { i ->
        DoubleArray(components.size) { j ->
            distance(
                start = componentStartAndEnds[i],
                end = componentStartAndEnds[j],
                distanceMatrix = distanceMatrix
            )
        }
    }
    val route = travelingSalesmanProblemLinearProgramming(
        newDistanceMatrix
    ).toRoute(false)

    return components
        .order(route)
        .connect(distanceMatrix)
        .toAdjacencyMatrix()
}

fun List<List<Int>>.connect(
    distanceMatrix: Array<DoubleArray>,
): List<Int> {
    return windowed(2) { (route1, route2) ->
        when (multiMin(
            distance(
                start = route1.first() to route1.last(),
                end = route2.first() to route2.last(),
                distanceMatrix = distanceMatrix
            )
        )) {
            distanceMatrix[route1.first()][route2.first()] ->
                route1.reversed() + route2

            distanceMatrix[route1.first()][route2.last()] ->
                route1.reversed() + route2.reversed()

            distanceMatrix[route1.last()][route2.first()] ->
                route1 + route2

            else -> route1 + route2.reversed()
        }
    }.flatten()
}

fun Map<Int, Map<Int, Boolean>>.toAdjacencyMatrix()
        : Array<BooleanArray> {
    val maxIndex = keys.max()
    return Array(maxIndex + 1) { i ->
        BooleanArray(maxIndex + 1) { j ->
            get(i)?.get(j) ?: false
        }
    }
}

fun List<Int>.toAdjacencyMatrix(): Array<BooleanArray> {
    val maxNodeIndex = max()
    val result = Array(maxNodeIndex + 1) {
        BooleanArray(maxNodeIndex + 1)
    }
    windowed(2) { (a, b) -> result[a][b] = true }
    return result
}

fun Map<Int, Map<Int, Boolean>>.getConnectedComponents()
        : List<List<Int>> {
    val visited = mutableSetOf<Int>()
    val components = mutableListOf<List<Int>>()

    fun dfs(
        adjacencyMatrix: Map<Int, Map<Int, Boolean>>,
        vertex: Int,
        visited: MutableSet<Int>,
        component: MutableList<Int>,
    ) {
        visited.add(vertex)
        component.add(vertex)
        for (neighbor in adjacencyMatrix[vertex].orEmpty().keys) {
            if (adjacencyMatrix[vertex]?.get(neighbor) == true
                && neighbor !in visited
            ) {
                dfs(adjacencyMatrix, neighbor, visited, component)
            }
        }
    }

    for (vertex in keys) {
        if (vertex !in visited) {
            val component = mutableListOf<Int>()
            dfs(this, vertex, visited, component)
            components.add(component)
        }
    }

    return components
}

private fun distance(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    distanceMatrix: Array<DoubleArray>,
): Double {
    return multiMin(
        distanceMatrix[start.first][end.first],
        distanceMatrix[start.second][end.second],
        distanceMatrix[start.second][end.first],
        distanceMatrix[start.first][end.second],
    )
}

fun Array<BooleanArray>.toRoute(
    addLastToCycle: Boolean = true,
): List<Int> {
    val route = mutableListOf<Int>()
    fun dfs(node: Int, visited: MutableSet<Int> = mutableSetOf()) {
        route.add(node)
        visited.add(node)
        for (neighbor in indices)
            if (this[node][neighbor] && neighbor !in visited)
                dfs(neighbor, visited)
    }
    // find a node with degree = 1
    // run dfs starting with this node
    val start = indices.firstOrNull {
        get(it).sumOf { if (it) 1.toInt() else 0 } == 1
    }
        ?: indices.firstOrNull {
            get(it).sumOf { if (it) 1.toInt() else 0 } == 2
        } ?: return emptyList()
    dfs(start)
    if (addLastToCycle) route.add(start)
    return route
}

fun <T> Array<Array<Array<T>>>.toAdjacencyMatrices(f: (T) -> Boolean): Array<Array<BooleanArray>> =
    Array(first().first().size) { k ->
        Array(size) { i ->
            BooleanArray(size) { j ->
                f(this[i][j][k])
            }
        }
    }

fun Map<Int, Map<Int, Boolean>>.toRoute(addLastToCycle: Boolean = true): List<Int> {
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
    if (addLastToCycle) route.add(start)
    return route
}


fun <T> Array<Array<T>>.isConnected(exists: (T) -> Boolean): Boolean {
    val visited = BooleanArray(size)
    fun dfs(node: Int) {
        visited[node] = true
        for (neighbor in indices)
            if (exists(this[node][neighbor]) && !visited[neighbor])
                dfs(neighbor)
    }
    dfs(0)
    return visited.all { it }
}