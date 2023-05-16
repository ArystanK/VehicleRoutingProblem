import kotlin.random.Random

fun <T> List<T>.randomList(): List<T> {
    val size = Random.nextInt(456, 456 * 5)
    return List(size) { random() }
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

fun <T> Array<Array<T>>.toRoutes(f: (T) -> Int): List<Int> {
    val nodes = ArrayList<Int>()
    val visited = HashSet<Int>()
    while (visited.size < size)
        for (i in indices)
            if (i !in visited) {
                val row = get(i)
                if (row.sumOf { f(it) } == 1) {
                    nodes.add(i)
                    visited.add(i)
                } else {
                    for (j in row.indices) {
                        if (f(row[j]) != 1) continue
                        nodes.add(i)
                        visited.add(i)
                        break
                    }
                }
            }
    return nodes
}


fun main() {
    println(arrayOf(
        arrayOf(0, 1, 0, 1),
        arrayOf(1, 0, 1, 0),
        arrayOf(0, 1, 0, 1),
        arrayOf(1, 0, 1, 0),
    ).toRoutes { it })
}
