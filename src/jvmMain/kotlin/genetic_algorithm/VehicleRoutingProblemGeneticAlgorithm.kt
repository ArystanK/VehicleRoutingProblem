package genetic_algorithm

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

class VehicleRoutingProblemGeneticAlgorithm(
    private val distMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun solve(): List<List<Int>> {
        val initSolutionFile = File("GA_SOLUTION.json")
        val initRoutes: List<List<Int>> = Json.decodeFromStream<List<List<Int>>>(initSolutionFile.inputStream())
        val ga = World(
            distanceMatrix = distMatrix,
            numberOfRoutes = numberOfRoutes,
            generationSize = 100,
            populationSize = 100,
            mutationRate = 0.15,
            initialRoutes = initRoutes.ifEmpty { null }
        )
        ga.solve()
        val routes = ga.population.maxBy { it.fitness }.routes
        initSolutionFile.writeText(Json.encodeToString(routes))
        return routes
    }
}
