package domain

import data.BusStop
import data.toBusStop
import domain.linear_programming.solveVRPLinearProgramming
import domain.linear_programming.toRoute
import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.math.pow

fun main(): Unit = runBlocking {
//    val result = File("RESULT.json")
//    val solutions = Json.decodeFromStream<List<VRPSolution>>(result.inputStream())
    val busStopsId = 0
    val coordinates = BusStopsRepository().getBusStops().filter { it.busStopsCollection.id.value == busStopsId }
    val numberOfNodes = coordinates.size
    val numberOfRoutes = 1
    val distanceMatrix = generateDistanceMatrix(coordinates.map { it.toBusStop() }, numberOfNodes) { first, second ->
        euclideanDistance(first.lat to first.lon, second.lat to second.lon)
    }
//    val routes = VehicleRoutingProblemGeneticAlgorithm(distanceMatrix, numberOfRoutes).solve()
    val routesRepository = RoutesRepository()
    val startTime = System.nanoTime()
    val routes = solveVRPLinearProgramming(
        numberOfRoutes,
        distanceMatrix
    ).map { it.toRoute() }
    val endTime = System.nanoTime()
    println(routes)
    println(endTime - startTime)
    routesRepository.safeSolution(routes, busStopsId)
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