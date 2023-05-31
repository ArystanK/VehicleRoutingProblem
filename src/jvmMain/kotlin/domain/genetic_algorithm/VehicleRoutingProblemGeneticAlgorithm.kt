package domain.genetic_algorithm

import domain.repository.FitnessRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class VehicleRoutingProblemGeneticAlgorithm(
    private val distMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
    private val fitnessRepository: FitnessRepository,
    private val busStopsId: Int,
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun solve(): List<List<Int>> {
        val initSolutionFile = File("GA_SOLUTION.json")
        val initRoutes: List<List<Int>> = Json.decodeFromStream<List<List<Int>>>(initSolutionFile.inputStream())
        val ga = World(
            distanceMatrix = distMatrix,
            numberOfRoutes = numberOfRoutes,
            generationSize = 100,
            populationSize = 1000,
            mutationRate = 0.15,
            initialRoutes = initRoutes.ifEmpty { null },
            fitnessRepository = fitnessRepository,
            busStopId = busStopsId
        )
        ga.solve()
        val routes = ga.population.maxBy { it.fitness }.routes
        initSolutionFile.writeText(Json.encodeToString(routes))
        return routes
    }
}
