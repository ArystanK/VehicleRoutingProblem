import linear_programming.travelingSalesmanProblemLinearProgramming
import kotlin.math.pow

fun Map<Int, Map<Int, Boolean>>.connectComponents(
    distanceMatrix: Array<DoubleArray>,
): Map<Int, Map<Int, Boolean>> {
    fun getConnectedComponents(adjacencyMatrix: Map<Int, Map<Int, Boolean>>): List<Pair<Int, Int>> {
        fun dfs(node: Int, visited: MutableSet<Int> = mutableSetOf()): Int {
            visited.add(node)
            for (neighbor in adjacencyMatrix.keys)
                if (adjacencyMatrix[node]?.get(neighbor) == true && neighbor !in visited)
                    dfs(neighbor, visited)
            return node
        }

        val routes = mutableListOf<Pair<Int, Int>>()
        for (i in adjacencyMatrix.keys) {
            if (adjacencyMatrix[i]?.entries?.sumOf { if (it.value) 1 else 0.toInt() } == 1)
                routes.add(i to dfs(i))
        }

        return routes
    }

    val components = getConnectedComponents(this)
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

fun List<Pair<Int, Int>>.flatten(): List<Int> {
    return map { listOf(it.first, it.second) }.flatten()
}

fun distance(pair: Pair<Int, Int>, pair1: Pair<Int, Int>, distanceMatrix: Array<DoubleArray>): Double {
    return (distanceMatrix[pair.first][pair1.first] + distanceMatrix[pair.second][pair1.second]) / 2

}

fun Map<Int, Map<Int, Boolean>>.toRoute(): List<Int> {
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
    val start = indices.firstOrNull { get(it).sumOf { if (it) 1.toInt() else 0 } == 1 }
    if (start == null) {
        dfs(0)
        if (addLastToCycle) route.add(0)
    } else dfs(start)
    return route
}

fun Array<BooleanArray>.diagonalize(): Array<BooleanArray> =
    Array(size) { i -> BooleanArray(size) { j -> this[i][j] || this[j][i] } }

fun Array<BooleanArray>.removeZeroRows(): Map<Int, Map<Int, Boolean>> {
    val zeroIndices = indices.filter { get(it).sumOf { it.toInt() } == 0 }
    val result = mutableMapOf<Int, MutableMap<Int, Boolean>>()
    for (i in indices) {
        if (i in zeroIndices) continue
        for (j in indices) {
            if (j in zeroIndices) continue
            result[i] = (result.getOrElse(i) { mutableMapOf() } + (j to this[i][j])).toMutableMap()
        }
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

