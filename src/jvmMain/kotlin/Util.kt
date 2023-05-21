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

fun <T> List<T>.order(order: List<Int>): List<T> {
    require(this.size == order.size) { "The order list must have the same size as the original list." }

    val orderedList = mutableListOf<T>()

    for (index in order) {
        orderedList.add(this[index])
    }

    return orderedList
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
