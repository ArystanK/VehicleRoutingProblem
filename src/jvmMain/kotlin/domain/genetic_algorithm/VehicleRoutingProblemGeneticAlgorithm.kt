package domain.genetic_algorithm

import domain.poko.BusStops
import domain.repository.FitnessRepository

class VehicleRoutingProblemGeneticAlgorithm(
    distMatrix: Array<DoubleArray>,
    numberOfRoutes: Int,
    fitnessRepository: FitnessRepository,
    busStops: BusStops,
) {
    private val ga = World(
        distanceMatrix = distMatrix,
        numberOfRoutes = numberOfRoutes,
        generationSize = 10000,
        populationSize = 10000,
        mutationRate = 0.15,
        initialRoutes = null,
        fitnessRepository = fitnessRepository,
        busStop = busStops
    )

    suspend fun solve(): List<List<Int>> {
        ga.solve()
        return ga.population.maxBy { it.fitness }.routes
    }

    val fitness: Double = ga.population.maxBy { it.fitness }.fitness
}
