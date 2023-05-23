package data

import kotlinx.serialization.Serializable

@Serializable
data class VRPSolution(
    val distanceMatrix: Array<DoubleArray>,
    val numberOfRoutes: Int,
    val solution: Array<Array<BooleanArray>>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VRPSolution

        if (!distanceMatrix.contentDeepEquals(other.distanceMatrix)) return false
        if (numberOfRoutes != other.numberOfRoutes) return false
        return solution.contentDeepEquals(other.solution)
    }

    override fun hashCode(): Int {
        var result = distanceMatrix.contentDeepHashCode()
        result = 31 * result + numberOfRoutes
        result = 31 * result + solution.contentDeepHashCode()
        return result
    }

}
