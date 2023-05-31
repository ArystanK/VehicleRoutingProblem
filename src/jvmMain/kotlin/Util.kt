import center.sciprog.maps.compose.WebMercatorSpace
import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import domain.poko.BusStop
import space.kscience.kmath.geometry.Degrees
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

fun generateDistanceMatrix(
    busStops: List<BusStop>,
    distance: (BusStop, BusStop) -> Double,
): Array<DoubleArray> = Array(busStops.size) { i ->
    DoubleArray(busStops.size) { j ->
        distance(busStops[i], busStops[j])
    }
}
fun Rectangle<Gmc>.partition(n: Int): List<Rectangle<Gmc>> {
    val width = (b.latitude.toDegrees().value - a.latitude.toDegrees().value) / n
    val height = (b.longitude.toDegrees().value - a.longitude.toDegrees().value) / n
    return List(n) {
        val startX = a.latitude.toDegrees().value + width * it
        List(n) {
            val startY = a.longitude.toDegrees().value + height * it
            WebMercatorSpace.Rectangle(Gmc(Degrees(startX), Degrees(startY)), Gmc(Degrees(startX + width), Degrees(startY + height)))
        }
    }.flatten()
}

fun generateDistanceMatrixMap(
    busStops: List<BusStop>,
    distance: (BusStop, BusStop) -> Double,
): Map<Int, Map<Int, Double>> = busStops.associate { outer ->
    outer.id to busStops.associate { inner ->
        inner.id to distance(outer, inner)
    }
}

fun Map<Int, Map<Int, Double>>.toMatrix(setWhenZero: Double = 0.0): Array<DoubleArray> =
    map { it.value.map { if (it.value == 0.0) setWhenZero else it.value }.toDoubleArray() }.toTypedArray()

fun euclideanDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>) =
    (p1.first - p2.first).pow(2) + (p1.second - p2.second).pow(2)

fun manhattanDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>) =
    abs(p1.first - p2.first) + abs(p1.second - p2.second)

// route = [1, 7, 5, 3]
// nodes = [4, 1, 4, 2]
// result = [4, 2, 4, 1]
fun <T> List<T>.order(order: List<Int>): List<T> {
    require(this.size == order.size) { "The order list must have the same size as the original list." }

    val orderedList = mutableListOf<T>()

    for (index in order) orderedList.add(this[index])

    return orderedList
}

fun <T> List<T>.filterClose(epsilon: Double, getX: (T) -> Double, getY: (T) -> Double): List<T> {
    val filteredPoints = mutableListOf<T>()

    for (i in indices) {
        val currentPoint = this[i]
        var isClose = false

        for (j in i + 1 until size) {
            val otherPoint = this[j]

            val xDiff = getX(currentPoint) - getX(otherPoint)
            val yDiff = getY(currentPoint) - getY(otherPoint)
            val distance = sqrt(xDiff * xDiff + yDiff * yDiff)

            if (distance <= epsilon) {
                isClose = true
                break
            }
        }

        if (!isClose) {
            filteredPoints.add(currentPoint)
        }
    }

    return filteredPoints
}

tailrec fun <T> unique(a: T, b: T, count: Int = 0, f: () -> T): T {
    if (count == 10) return a
    if (a != b) return a
    return unique(f(), b, count + 1, f)
}

tailrec fun <T> uniqueIn(a: T, b: Collection<T>, count: Int = 0, f: () -> T): T {
    if (count == 10) return a
    if (a !in b) return a
    return uniqueIn(f(), b, count + 1, f)
}

fun <T : Comparable<T>> List<T>.myBinarySearch(value: T): Int {
    var left = 0
    var right = size - 1
    var result = -1

    while (left <= right) {
        val mid = (left + right) / 2
        val midValue = this[mid]

        if (midValue == value) {
            return mid // Exact match found
        } else if (midValue < value) {
            result = mid // Keep track of the latest index that is less than the searched value
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return result
}


fun Boolean.toInt(): Int = if (this) 1 else 0

fun Int.toBoolean(): Boolean = this == 1

fun IntArray.toBooleanArray(): BooleanArray = map { it.toBoolean() }.toBooleanArray()

tailrec fun multiMin(vararg a: Double): Double {
    if (a.size == 1) return a.first()
    if (a.size == 2) return min(a.first(), a.last())
    return multiMin(min(a.last(), a[a.lastIndex - 1]), *a.take(a.size - 2).toDoubleArray())
}