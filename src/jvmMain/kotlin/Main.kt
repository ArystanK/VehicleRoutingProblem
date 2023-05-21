import client_api.Point
import client_api.Rectangle
import genetic_algorithm.VehicleRoutingProblemGeneticAlgorithm
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import kotlin.math.abs
import kotlin.math.pow

fun main() = runBlocking {
    val result = File("RESULT.json")
    result.writeText("")
    val coordinates = BusStopsRepositoryImplementation().getBusStops(
        Rectangle(
            Point(71.19865356830648, 51.28193274061607),
            Point(71.65595628500706, 50.996389716773805)
        )
    )

    val distanceMatrix = generateDistanceMatrix(coordinates) { first, second ->
        euclideanDistance(first.lat to first.lon, second.lat to second.lon)
    }
    val solution = VehicleRoutingProblemGeneticAlgorithm(
        numberOfRoutes = 10,
        distMatrix = distanceMatrix,
    ).solve()
    result.appendText(Json.encodeToJsonElement(solution).toString())
}

fun generateDistanceMatrix(
    busStops: List<BusStop>,
    distance: (BusStop, BusStop) -> Double,
): Array<DoubleArray> {
    val result = Array(busStops.size) { DoubleArray(busStops.size) }
    busStops.forEach { outer -> busStops.forEach { inner -> result[outer.id][inner.id] = distance(outer, inner) } }
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