import client_api.Point
import client_api.Rectangle
import data.BusStop
import data.BusStopsRepository
import kotlinx.coroutines.runBlocking
import linear_programming.solveVRPLinearProgramming
import kotlin.math.abs
import kotlin.math.pow

fun main(): Unit = runBlocking {
//    val result = File("RESULT.json")
//    val solutions = Json.decodeFromStream<List<VRPSolution>>(result.inputStream())
    val coordinates = BusStopsRepository().getBusStops(
        Rectangle(
            Point(71.19865356830648, 51.28193274061607),
            Point(71.65595628500706, 50.996389716773805)
        )
    )
    val numberOfNodes = (coordinates.size * 0.1).toInt()
    val numberOfRoutes = 1
    val distanceMatrix = generateDistanceMatrix(coordinates, numberOfNodes) { first, second ->
        euclideanDistance(first.lat to first.lon, second.lat to second.lon)
    }
    solveVRPLinearProgramming(
        numberOfRoutes,
        distanceMatrix
    )
        .also { println(it.joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } }) }

//    VehicleRoutingProblemGeneticAlgorithm(numberOfRoutes = numberOfRoutes, distMatrix = distanceMatrix).solve()
//    result.writeText(
//        Json.encodeToJsonElement(solutions + VRPSolution(distanceMatrix, numberOfRoutes, solution)).toString()
//    )
}

fun generateDistanceMatrix(
    busStops: List<BusStop>,
    numberOfNodes: Int,
    distance: (BusStop, BusStop) -> Double,
): Array<DoubleArray> {
    val result = Array(numberOfNodes) { DoubleArray(numberOfNodes) }
    result.indices.forEach { i ->
        result.indices.forEach { j ->
            result[i][j] = distance(busStops[i], busStops[j])
        }
    }
//    busStops.forEach { outer -> busStops.forEach { inner -> result[outer.id][inner.id] = distance(outer, inner) } }
    return result
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