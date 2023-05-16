import client_api.Point
import client_api.Rectangle
import kotlinx.coroutines.runBlocking
import linear_programming.VehicleRoutingProblemLinearProgramming
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
        euclideanDistance(
            first.lat to first.lon,
            second.lat to second.lon
        )
    }
    val solution = VehicleRoutingProblemLinearProgramming().solve(
        numberOfRoutes = 10,
        distMatrix = distanceMatrix.toMatrix(),
    )

}

fun generateDistanceMatrix(
    busStops: List<BusStop>,
    distance: (BusStop, BusStop) -> Double,
): Map<Int, Map<Int, Double>> {
    return busStops.associate { outer ->
        outer.id to busStops.associate { inner ->
            inner.id to distance(outer, inner)
        }
    }
}

fun Map<Int, Map<Int, Double>>.toMatrix(setWhenZero: Double = 0.0): Array<DoubleArray> =
    map { it.value.map { if (it.value == 0.0) setWhenZero else it.value }.toDoubleArray() }.toTypedArray()

fun euclideanDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>) =
    (p1.first - p2.first).pow(2) + (p1.second - p2.second).pow(2)

fun manhattanDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>) =
    abs(p1.first - p2.first) + abs(p1.second - p2.second)