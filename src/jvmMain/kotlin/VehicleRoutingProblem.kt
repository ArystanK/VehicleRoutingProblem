interface VehicleRoutingProblem {
    suspend fun solve(
        numberOfRoutes: Int,
        distMatrix: Array<DoubleArray>,
    ): List<List<Int>>
}