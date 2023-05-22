package linear_programming

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import toInt

fun travelingSalesmanProblemLinearProgramming(distanceMatrix: Array<DoubleArray>): Array<BooleanArray> {
    fun MPSolver.setNoSelfLoopConstraint(x: Array<Array<MPVariable>>) {
        for (i in x.indices) {
            val c = makeConstraint(0.0, 0.0)
            c.setCoefficient(x[i][i], 1.0)
        }
    }

    fun MPSolver.setDegreeConstraint(x: Array<Array<MPVariable>>) {
        for (i in x.indices) {
            val c = makeConstraint(2.0, 2.0)
            for (j in x.indices) if (i == j) continue else c.setCoefficient(x[i][j], 1.0)
        }
        for (i in x.indices) for (j in x.indices) {
            val c = makeConstraint(0.0, 0.0)
            c.setCoefficient(x[j][i], 1.0)
            c.setCoefficient(x[i][j], -1.0)
        }
    }

    fun Array<Array<MPVariable>>.toAdjacencyMatrix(): Array<BooleanArray> =
        Array(size) { i -> BooleanArray(size) { j -> this[i][j].solutionValue() == 1.0 } }

    Loader.loadNativeLibraries()
    val solver = MPSolver.createSolver("SCIP")
    val x = Array(distanceMatrix.size) { i -> Array(distanceMatrix.size) { j -> solver.makeBoolVar("x[$i, $j]") } }
    solver.setDegreeConstraint(x)
    solver.setNoSelfLoopConstraint(x)
    val objective = solver.objective()
    for (i in x.indices) for (j in (i + 1) until x.size) objective.setCoefficient(x[i][j], distanceMatrix[i][j])
    objective.setMinimization()
    val result = solver.solve()
    if (result == MPSolver.ResultStatus.OPTIMAL) return x.toAdjacencyMatrix()
    println(distanceMatrix.joinToString("\n") { it.joinToString() })
    throw Exception("Infeasible solution")
}

fun main() {
    val distanceMatrix = arrayOf(
        doubleArrayOf(0.0, 3.0, 1.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(3.0, 0.0, 1.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(1.0, 1.0, 0.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(5.0, 5.0, 5.0, 0.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(0.0, 3.0, 1.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(3.0, 0.0, 1.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(1.0, 1.0, 0.0, 5.0, 0.0, 3.0, 1.0, 5.0),
        doubleArrayOf(5.0, 5.0, 5.0, 0.0, 0.0, 3.0, 1.0, 5.0),
    )
    println(travelingSalesmanProblemLinearProgramming(distanceMatrix).map { it.map { it.toInt() } }
        .joinToString("\n") { it.joinToString() })
}
