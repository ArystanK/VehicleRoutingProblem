package data

import toRoute

sealed class VRPSolution(
    val distanceMatrix: Array<DoubleArray>,
    val numberOfRoutes: Int,
) {
    abstract fun toRoutes(): List<List<Int>>
    data class ArrayVRPSolution(
        val solution: Array<Array<BooleanArray>>,
        private val _distanceMatrix: Array<DoubleArray>,
        private val _numberOfRoutes: Int,
    ) : VRPSolution(_distanceMatrix, _numberOfRoutes) {
        override fun toRoutes(): List<List<Int>> = solution.map { it.toRoute() }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ArrayVRPSolution

            if (!_distanceMatrix.contentDeepEquals(other._distanceMatrix)) return false
            if (_numberOfRoutes != other._numberOfRoutes) return false
            return solution.contentDeepEquals(other.solution)
        }

        override fun hashCode(): Int {
            var result = _distanceMatrix.contentDeepHashCode()
            result = 31 * result + _numberOfRoutes
            result = 31 * result + solution.contentDeepHashCode()
            return result
        }
    }

    data class ListVRPSolution(
        private val _distanceMatrix: Array<DoubleArray>,
        private val _numberOfRoutes: Int,
        val solution: List<List<Int>>,
    ) : VRPSolution(_distanceMatrix, _numberOfRoutes) {
        override fun toRoutes(): List<List<Int>> = solution
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ListVRPSolution

            if (!_distanceMatrix.contentDeepEquals(other._distanceMatrix)) return false
            if (_numberOfRoutes != other._numberOfRoutes) return false
            return solution == other.solution
        }

        override fun hashCode(): Int {
            var result = _distanceMatrix.contentDeepHashCode()
            result = 31 * result + _numberOfRoutes
            result = 31 * result + solution.hashCode()
            return result
        }
    }
}