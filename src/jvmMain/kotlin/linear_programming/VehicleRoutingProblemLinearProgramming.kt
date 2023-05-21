package linear_programming

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import connectComponents
import diagonalize
import kotlinx.coroutines.runBlocking
import removeZeroRows
import toAdjacencyMatrices
import toRoute

class VehicleRoutingProblemLinearProgramming {
    fun solve(
        numberOfRoutes: Int,
        distMatrix: Array<DoubleArray>,
    ): List<List<Int>> {
        Loader.loadNativeLibraries()
        val solver = MPSolver.createSolver("SCIP")

        val x = solver.createVariableX(numberOfRoutes, distMatrix.size)
        val z = solver.createVariableZ()

        solver.setIsVisitedConstraints(x)
        solver.setDegreeConstraints(x)
        solver.makeNoSingleRouteConstraint(x)
        solver.setMaxDistanceConstraint(z, x, distMatrix)
        solver.setObjective(z)

        val result = solver.solve()
        if (result == MPSolver.ResultStatus.OPTIMAL) {
            return x
                .toAdjacencyMatrices { it.solutionValue() == 1.0 }
                .map {
                    it
                        .diagonalize()
                        .removeZeroRows()
//                        .also {
//                            println(it.keys.joinToString())
//                            println(it.entries.joinToString("\n") { it.value.entries.joinToString(prefix = "${it.key}: ") { it.value.toString() } })
//                        }
                        .connectComponents(distMatrix)
//                        .also { println(it) }
                        .toRoute()
                }
        }
        println(result)
        println("The problem does not have an optimal solution!")
        return listOf()
    }
}

private fun MPSolver.setObjective(z: MPVariable) {
    val objective = objective()
    objective.setCoefficient(z, 1.0)
    objective.setMinimization()
}

private fun MPSolver.createVariableX(m: Int, totalNumberOfNodes: Int): Array<Array<Array<MPVariable>>> =
    Array(totalNumberOfNodes) { i -> Array(totalNumberOfNodes) { j -> Array(m) { makeBoolVar("x[$i, $j, $it]") } } }

private fun MPSolver.createVariableZ(): MPVariable = makeNumVar(0.0, Double.POSITIVE_INFINITY, "z")


private fun MPSolver.setIsVisitedConstraints(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) {
        val c = makeConstraint(1.0, Double.POSITIVE_INFINITY)
        for (j in x.indices) {
            if (i == j) continue
            for (k in x[i][j].indices) c.setCoefficient(x[i][j][k], 1.0)
        }
    }
}

private fun MPSolver.setDegreeConstraints(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) for (k in x.first().first().indices) {
        val c = makeConstraint(0.0, 0.0)
        c.setCoefficient(x[i][i][k], 1.0)
    }
    for (k in x.first().first().indices) for (i in x.indices) {
        val c3 = makeConstraint(Double.NEGATIVE_INFINITY, 2.0)
        for (j in x[i].indices) {
            c3.setCoefficient(x[i][j][k], 1.0)
            if (i != j) c3.setCoefficient(x[j][i][k], 1.0)
        }
    }
}

private fun MPSolver.makeNoSingleRouteConstraint(x: Array<Array<Array<MPVariable>>>) {
    for (k in x.first().first().indices) {
        val c = makeConstraint(1.0, Double.POSITIVE_INFINITY)
        for (i in x.indices)
            for (j in x.indices) {
                c.setCoefficient(x[i][j][k], 1.0)
                if (i != j) c.setCoefficient(x[j][i][k], 1.0)
            }
    }
}


private fun MPSolver.setMaxDistanceConstraint(
    z: MPVariable,
    x: Array<Array<Array<MPVariable>>>,
    d: Array<DoubleArray>,
) {
    for (k in x.first().first().indices) {
        val c = makeConstraint(0.0, Double.POSITIVE_INFINITY)
        c.setCoefficient(z, 1.0)
        for (i in x.indices) for (j in x[i].indices) c.setCoefficient(x[i][j][k], -d[i][j])
    }
}


inline fun <T> Array<Array<Array<T>>>.toListOfMatrices(f: (T) -> Boolean): List<Array<BooleanArray>> =
    List(first().first().size) { k ->
        Array(size) { i ->
            BooleanArray(size) { j ->
                f(get(i)[j][k])
            }
        }
    }

fun main() {
    for (i in 1..10)
        println(
            VehicleRoutingProblemLinearProgramming().solve(
                i,
                Array(4) { DoubleArray(4) { 1.0 } },
            )
        )
}

