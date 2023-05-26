package linear_programming

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import toAdjacencyMatrices

suspend fun solveVRPLinearProgramming(
    numberOfRoutes: Int,
    distMatrix: Array<DoubleArray>,
): Array<Array<BooleanArray>> {
    if (distMatrix.isEmpty()) return emptyArray<Array<BooleanArray>>()

    if (distMatrix.size == 1) return Array(numberOfRoutes) {
        if (it == 0) arrayOf(booleanArrayOf(true))
        else emptyArray()
    }

    if (distMatrix.size == 2) return Array(numberOfRoutes) {
        if (it == 0) arrayOf(
            booleanArrayOf(false, true),
            booleanArrayOf(true, false)
        ) else emptyArray()
    }

    if (numberOfRoutes == 1) return arrayOf(travelingSalesmanProblemLinearProgramming(distMatrix))

    Loader.loadNativeLibraries()
    val solver = MPSolver.createSolver("SCIP")

    val x = solver.createVariableX(numberOfRoutes, distMatrix.size)
    val z = solver.createVariableZ()

    solver.makeIsVisitedConstraints(x)
    solver.makeDegreeConstraints(x)
    solver.makeNoSingleRouteConstraint(x)
    solver.makeDiagonalConstraint(x)
    solver.makeNoSelfLoopsConstrain(x)
    solver.makeMaxDistanceConstraint(z, x, distMatrix)
    solver.setObjective(z)

    val result = solver.solve()

    if (result == MPSolver.ResultStatus.OPTIMAL) return x.toAdjacencyMatrices { it.solutionValue() == 1.0 }

    throw Exception("Infeasible solution")
}


private fun MPSolver.setObjective(z: MPVariable) {
    val objective = objective()
    objective.setCoefficient(z, 1.0)
    objective.setMinimization()
}

private fun MPSolver.createVariableX(m: Int, totalNumberOfNodes: Int): Array<Array<Array<MPVariable>>> =
    Array(totalNumberOfNodes) { i -> Array(totalNumberOfNodes) { j -> Array(m) { makeBoolVar("x[$i, $j, $it]") } } }

private fun MPSolver.createVariableZ(): MPVariable = makeNumVar(0.0, Double.POSITIVE_INFINITY, "z")

private fun MPSolver.makeIsVisitedConstraints(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) {
        val c = makeConstraint(1.0, Double.POSITIVE_INFINITY)
        for (j in x.indices) {
            if (i == j) continue
            for (k in x[i][j].indices) c.setCoefficient(x[i][j][k], 1.0)
        }
    }
}

private fun MPSolver.makeNoSelfLoopsConstrain(x: Array<Array<Array<MPVariable>>>) {
    for (k in x.first().first().indices) for (i in x.indices) {
        val c = makeConstraint(0.0, 0.0)
        c.setCoefficient(x[i][i][k], 1.0)
    }
}

private fun MPSolver.makeDiagonalConstraint(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) for (j in x.indices) for (k in x[i][j].indices) {
        val c = makeConstraint(0.0, .0)
        c.setCoefficient(x[i][j][k], 1.0)
        c.setCoefficient(x[j][i][k], -1.0)
    }
}

private fun MPSolver.makeDegreeConstraints(x: Array<Array<Array<MPVariable>>>) {
    val t = Array(x.size) { i -> Array(x[i][i].size) { j -> makeBoolVar("t[$i, $j]") } }
    for (k in x.first().first().indices) for (i in x.indices) {
        val c3 = makeConstraint(0.0, 0.0)
        for (j in x[i].indices) c3.setCoefficient(x[i][j][k], 1.0)
        c3.setCoefficient(t[i][k], -2.0)
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

private fun MPSolver.makeMaxDistanceConstraint(
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


