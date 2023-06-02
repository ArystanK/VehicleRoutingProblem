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
        generationSize = 1000,
        populationSize = 1000,
        mutationRate = 0.15,
        initialRoutes = null,
        fitnessRepository = fitnessRepository,
        busStop = busStops
    )

    suspend fun solve(): Routes? {
        return ga.solve()
    }

}
