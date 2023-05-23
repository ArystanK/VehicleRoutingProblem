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

// route = [1, 7, 5, 3]
// nodes = [4, 1, 4, 2]
// result = [4, 2, 4, 1]
fun <T> List<T>.order(order: List<Int>): List<T> {
    require(this.size == order.size) { "The order list must have the same size as the original list." }

    val orderedList = mutableListOf<T>()

    for (index in order) orderedList.add(this[index])

    return orderedList
}

inline fun <T> Array<Array<Array<T>>>.toListOfMatrices(f: (T) -> Boolean): List<Array<BooleanArray>> =
    List(first().first().size) { k ->
        Array(size) { i ->
            BooleanArray(size) { j ->
                f(get(i)[j][k])
            }
        }
    }

fun List<Pair<Int, Int>>.flatten(): List<Int> {
    return map { listOf(it.first, it.second) }.flatten()
}

tailrec fun <T> unique(a: T, b: T, f: () -> T): T {
    if (a != b) return a
    return unique(f(), b, f)
}

tailrec fun <T> uniqueIn(a: T, b: Collection<T>, f: () -> T): T {
    if (a !in b) return a
    return uniqueIn(f(), b, f)
}

fun Boolean.toInt(): Int = if (this) 1 else 0
