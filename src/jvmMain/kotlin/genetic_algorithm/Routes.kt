package genetic_algorithm

import uniqueIn
import kotlin.math.min
import kotlin.random.Random

class Routes private constructor(
    val routes: List<List<Int>>,
    private val distMatrix: Array<DoubleArray>,
    private val mutationRate: Double,
) {
    constructor(
        distMatrix: Array<DoubleArray>,
        numberOfRoutes: Int,
        mutationRate: Double,
        initialRoutes: List<List<Int>>? = null,
    ) :
            this(initialRoutes ?: List(numberOfRoutes) { randomRoute(distMatrix.size) }, distMatrix, mutationRate)

    val fitness: Double =
        100 / routes.maxOf { route -> route.windowed(2).sumOf { distMatrix[it[0]][it[1]] } }

    private val nodesVisited = routes.flatten().distinct()


    fun crossover(other: Routes): Pair<Routes, Routes> {
        val (routes1, routes2) = Companion.routesCrossover(routes, other.routes)
        val r1 = ArrayList<List<Int>>()
        val r2 = ArrayList<List<Int>>()
        routes1.zip(routes2).forEach {
            val (a, b) = Companion.crossover(it.first, it.second)
            r1.add(a)
            r2.add(b)
        }
        return copy(routes = r1) to copy(routes = r2)
    }

    private fun copy(routes: List<List<Int>>): Routes {
        return Routes(
            routes = routes,
            distMatrix = distMatrix,
            mutationRate = mutationRate,
        )
    }

    fun mutate(): Routes {
        fun List<Int>.mutate(): List<Int> {
            fun swapMutate(): List<Int> {
                val swapId = Random.nextInt(size)
                val temp = get(swapId)
                val copy = this.toMutableList()
                copy[swapId] = last()
                copy[lastIndex] = temp
                return copy
            }

            fun rotateMutate(): List<Int> {
                val rotateValue = Random.nextInt(0, size)
                val rotationStart = Random.nextInt(0, size)
                val rotationEnd = Random.nextInt(rotationStart, size)
                val tail = slice(rotationStart..rotationEnd)
                return slice(0 until rotationStart) + tail.drop(rotateValue) + tail.take(rotateValue) + slice(
                    rotationEnd + 1 until size
                )
            }

            fun removeMutate(): List<Int> {
                if (this@Routes.nodesVisited.size < distMatrix.size) return this
                if (size <= 10) return this
                val removeId = Random.nextInt(size)
                return filterIndexed { index, _ -> index == removeId }
            }

            fun addMutate(): List<Int> {
                if (distMatrix.size == size) return this
                val newNode = uniqueIn(
                    Random.nextInt(distMatrix.size),
                    this
                ) { Random.nextInt(distMatrix.size) }
                return this + newNode
            }

            return when {
                Random.nextDouble() <= 0.25 -> swapMutate()
                Random.nextDouble() <= 0.50 -> rotateMutate()
                Random.nextDouble() <= 0.75 -> removeMutate()
                else -> addMutate()
            }
        }
        if (Random.nextDouble() < mutationRate) {
            return copy(routes = routes.map { it.mutate() })
        }
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Routes

        if (routes != other.routes) return false
        if (!distMatrix.contentDeepEquals(other.distMatrix)) return false
        return fitness == other.fitness
    }

    override fun hashCode(): Int {
        var result = routes.hashCode()
        result = 31 * result + distMatrix.contentDeepHashCode()
        result = 31 * result + fitness.hashCode()
        return result
    }


    override fun toString(): String {
        return routes.joinToString("\n") { it.joinToString() } + " -> " + fitness
    }

    companion object {
        private fun randomRoute(n: Int): List<Int> = (0 until Random.nextInt(2, n)).toList().shuffled()

        fun crossover(route1: List<Int>, route2: List<Int>): Pair<List<Int>, List<Int>> {
            val splitPoint = Random.nextInt(min(route1.size, route2.size)) + 1
            val firstParentHead = route1.take(splitPoint)
            val firstParentHeadSet = firstParentHead.toSet()
            val r1 = firstParentHead + route2.filter { it !in firstParentHeadSet }
            val secondParentHead = route2.take(splitPoint)
            val secondParentHeadSet = secondParentHead.toSet()
            val r2 = secondParentHead + route1.filter { it !in secondParentHeadSet }
            return r1 to r2
        }

        fun routesCrossover(
            routes1: List<List<Int>>,
            routes2: List<List<Int>>,
        ): Pair<List<List<Int>>, List<List<Int>>> {
            val (first, second) = (routes1 + routes2).shuffled().chunked(routes1.size)
            return first to second
        }
    }
}

fun main() {
    val (a, b) = Routes.routesCrossover(
        listOf(listOf(1), listOf(2), listOf(3)),
        listOf(listOf(4), listOf(5), listOf(6))
    )
    println(a)
    println(b)
}
