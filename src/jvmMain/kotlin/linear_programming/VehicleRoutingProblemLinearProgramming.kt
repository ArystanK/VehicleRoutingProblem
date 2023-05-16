package linear_programming

import VehicleRoutingProblem
import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import kotlinx.coroutines.runBlocking
import java.io.File

class VehicleRoutingProblemLinearProgramming : VehicleRoutingProblem {
    override suspend fun solve(
        numberOfRoutes: Int,
        distMatrix: Array<DoubleArray>,
    ): List<List<Int>> {
        Loader.loadNativeLibraries()
        val solver = MPSolver.createSolver("SCIP")

        val x = solver.createVariableX(numberOfRoutes, distMatrix.size)
        val z = solver.makeNumVar(0.0, Double.POSITIVE_INFINITY, "z")

        solver.setIsVisitedConstraints(x)
//        solver.setConnectedConstraint(x)
        solver.setDegreeConstraints(x)
        solver.makeNoSingleRouteConstraint(x)
        solver.setMaxDistanceConstraint(z, x, distMatrix)

        val objective = solver.objective()
        objective.setCoefficient(z, 1.0)
        objective.setMinimization()
        val result = solver.solve()
        if (result == MPSolver.ResultStatus.OPTIMAL) {
            println(result)
            val file = File("RESULT.txt")
            file.writeText("")
            file.appendText("Solution:\n")
            file.appendText("Objective value = ${objective.value()}\n")
            println(x.toListOfMatrices { it.solutionValue() }.joinToString("\n\n") { it.joinToString("\n") })
//            val routes = x.map { it.toRoutes { it.solutionValue().toInt() } }
//            routes.forEachIndexed { index, pairs ->
//                file.appendText("ROUTE $index:\n")
//                file.appendText("${pairs}\n")
//            }
//            return routes
        } else {
            println(result)
            println("The problem does not have an optimal solution!")
        }
        return listOf()
    }
}

private fun MPSolver.createVariableX(m: Int, totalNumberOfNodes: Int): Array<Array<Array<MPVariable>>> {
    return Array(totalNumberOfNodes) { i ->
        Array(totalNumberOfNodes) { j ->
            Array(m) { makeBoolVar("x[$i, $j, $it]") }
        }
    }
}


private fun MPSolver.setIsVisitedConstraints(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) {
        val c = makeConstraint(1.0, Double.POSITIVE_INFINITY)
        for (j in x.indices) {
            if (i == j) continue
            for (k in x[i][j].indices) {
                c.setCoefficient(x[i][j][k], 1.0)
            }
        }
    }
}

private fun MPSolver.setDegreeConstraints(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) {
        for (k in x.m.iterator()) {
            val c = makeConstraint(0.0, 0.0)
            c.setCoefficient(x[i][i][k], 1.0)
        }
    }
    for (k in x.m.iterator()) {
        for (i in x.indices) {
            val c3 = makeConstraint(Double.NEGATIVE_INFINITY, 2.0)
            for (j in x[i].indices) {
                c3.setCoefficient(x[i][j][k], 1.0)
                if (i != j) c3.setCoefficient(x[j][i][k], 1.0)
            }
        }
    }
}

val <T> Array<Array<Array<T>>>.n: Int
    get() = size
val <T> Array<Array<Array<T>>>.m: Int
    get() = first().first().size

operator fun Int.iterator() = 0 until this

fun MPSolver.makeNoSingleRouteConstraint(x: Array<Array<Array<MPVariable>>>) {
    for (k in 0 until x.m) {
        val c = makeConstraint(1.0, Double.POSITIVE_INFINITY)
        for (i in 0 until x.n) {
            for (j in 0 until x.n) {
                c.setCoefficient(x[i][j][k], 1.0)
                if (i != j) c.setCoefficient(x[j][i][k], 1.0)
            }
        }
    }
}


fun MPSolver.setMaxDistanceConstraint(z: MPVariable, x: Array<Array<Array<MPVariable>>>, d: Array<DoubleArray>) {
    for (k in x.first().first().indices) {
        val c = makeConstraint(0.0, Double.POSITIVE_INFINITY)
        c.setCoefficient(z, 1.0)
        for (i in x.indices) for (j in x[i].indices) c.setCoefficient(x[i][j][k], -d[i][j])
    }
}

fun MPSolver.setConnectedConstraint(x: Array<Array<Array<MPVariable>>>) {
    for (i in x.indices) for (j in x[i].indices) for (k in x[i][j].indices) for (a in x.indices) {
        if (a == i || a == j) continue
        val c = makeConstraint(-2.0, 0.0)
        c.setCoefficient(x[i][j][k], 1.0)
        c.setCoefficient(x[i][a][k], -1.0)
        c.setCoefficient(x[a][j][k], -1.0)
    }
}


fun <T, R> Array<Array<Array<T>>>.toListOfMatrices(f: (T) -> R): List<List<List<R>>> {
    val result = ArrayList<List<List<R>>>()
    for (k in first().first().indices) {
        val matrix = ArrayList<List<R>>()
        for (i in indices) {
            val list = ArrayList<R>()
            for (j in indices) list.add(f(get(i)[j][k]))
            matrix.add(list)
        }
        result.add(matrix)
    }
    return result
}

fun main(): Unit = runBlocking {
    for (i in 1..10)
        VehicleRoutingProblemLinearProgramming().solve(
            i,
            Array(4) { DoubleArray(4) { 1.0 } },
        )
}

