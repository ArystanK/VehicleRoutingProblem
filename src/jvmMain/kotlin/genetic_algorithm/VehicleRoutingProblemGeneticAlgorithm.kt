package genetic_algorithm

import database.saveRouteToDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class VehicleRoutingProblemGeneticAlgorithm(
    private val distMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun solve(): List<List<Int>> {
        val initRoutes: List<List<Int>> = Json.decodeFromStream<List<List<Int>>>(File("RESULT.json").inputStream())
        val ga = World(
            distanceMatrix = distMatrix,
            numberOfRoutes = numberOfRoutes,
            generationSize = 1_000,
            populationSize = 1_000,
            mutationRate = 0.15,
            initialRoutes = initRoutes
        )
        ga.solve()
        val routes = ga.population.maxBy { it.fitness }.routes
        return saveRouteToDatabase(numberOfRoutes = numberOfRoutes, distMatrix = distMatrix, routes = routes)
    }
}
