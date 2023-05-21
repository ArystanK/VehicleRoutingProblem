package genetic_algorithm

class VehicleRoutingProblemGeneticAlgorithm(
    private val distMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
) {
    fun solve(): List<List<Int>> {
        val ga = World(
            distanceMatrix = distMatrix,
            numberOfRoutes = numberOfRoutes,
            generationSize = 1_000,
            populationSize = 1_000,
            mutationRate = 0.1
        )
        ga.solve()
        val bestRoutes = ga.bestRoutes()
        return bestRoutes.routes
    }
}
