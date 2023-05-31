package domain


import center.sciprog.maps.compose.WebMercatorSpace.Rectangle
import center.sciprog.maps.coordinates.Gmc
import domain.linear_programming.solveVRPLinearProgramming
import domain.linear_programming.toRoute
import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository
import euclideanDistance
import generateDistanceMatrix
import kotlinx.coroutines.runBlocking
import space.kscience.kmath.geometry.Degrees

fun main(): Unit = runBlocking {
//    val result = File("RESULT.json")
//    val solutions = Json.decodeFromStream<List<VRPSolution>>(result.inputStream())
    val searchBox = Rectangle(
        Gmc(Degrees(61.28193274061607), Degrees(71.19865356830648)),
        Gmc(Degrees(60.996389716773805), Degrees(71.65595628500706))
    )
    val repository = BusStopsRepository()
    repository
        .getBusStops(searchBox).onSuccess {
            repository.getBusStopsBySearchBox(searchBox).onSuccess { busStopsKey ->
                val coordinates = it
                    .groupBy { it.busStops }
                    .getOrDefault(busStopsKey, emptyList())
                println(coordinates.size)
                val numberOfRoutes = 1
                val distanceMatrix =
                    generateDistanceMatrix(coordinates) { first, second ->
                        euclideanDistance(first.lat to first.lon, second.lat to second.lon)
                    }
                val routesRepository = RoutesRepository()
                val startTime = System.nanoTime()
//    val routes = VehicleRoutingProblemGeneticAlgorithm(distanceMatrix, numberOfRoutes).solve()
                val routes = solveVRPLinearProgramming(
                    numberOfRoutes,
                    distanceMatrix
                ).map { it.toRoute() }

                val endTime = System.nanoTime()
                println(endTime - startTime)
                routesRepository.safeSolution(routes, busStopsKey, "LP")
//    result.writeText(
//        Json.encodeToJsonElement(solutions + VRPSolution(distanceMatrix, numberOfRoutes, solution)).toString()
//    )
            }
        }
}

