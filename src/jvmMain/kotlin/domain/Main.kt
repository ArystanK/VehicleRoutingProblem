package domain


import center.sciprog.maps.compose.WebMercatorSpace.Rectangle
import center.sciprog.maps.coordinates.Gmc
import domain.genetic_algorithm.VehicleRoutingProblemGeneticAlgorithm
import domain.linear_programming.solveVRPLinearProgramming
import domain.linear_programming.toRoute
import domain.repository.BusStopsRepository
import domain.repository.FitnessRepository
import domain.repository.RoutesRepository
import euclideanDistance
import generateDistanceMatrix
import kotlinx.coroutines.runBlocking
import partition
import space.kscience.kmath.geometry.Degrees

fun main(): Unit = runBlocking {
//    val result = File("RESULT.json")
//    val solutions = Json.decodeFromStream<List<VRPSolution>>(result.inputStream())
    val searchBox = AstanaArea.partition(3)[2]
    val repository = BusStopsRepository()
    repository
        .getBusStops(searchBox).onSuccess {
            repository.getBusStopsBySearchBox(searchBox).onSuccess { busStopsKey ->
                val coordinates = it
                    .groupBy { it.busStops }
                    .getOrDefault(busStopsKey, emptyList())
                println(coordinates.size)
                val numberOfRoutes = 5
                val distanceMatrix =
                    generateDistanceMatrix(coordinates) { first, second ->
                        euclideanDistance(first.lat to first.lon, second.lat to second.lon)
                    }

                val routesRepository = RoutesRepository()
                val startTime = System.nanoTime()
                val routes = solveVRPLinearProgramming(numberOfRoutes, distanceMatrix).map {
                    it.toRoute().map { coordinates[it] }
                }
//                val algo = VehicleRoutingProblemGeneticAlgorithm(
//                    numberOfRoutes = numberOfRoutes,
//                    distMatrix = distanceMatrix,
//                    busStops = busStopsKey,
//                    fitnessRepository = FitnessRepository()
//                )
//                val routes = algo.solve().map { it.map { coordinates[it] } }

                val endTime = System.nanoTime()
                println(endTime - startTime)
                routesRepository.safeSolution(routes, busStopsKey, "LP").onSuccess {
                    println(it)
                    println(routes)
                }
//    result.writeText(
//        Json.encodeToJsonElement(solutions + VRPSolution(distanceMatrix, numberOfRoutes, solution)).toString()
//    )
            }
        }
}

