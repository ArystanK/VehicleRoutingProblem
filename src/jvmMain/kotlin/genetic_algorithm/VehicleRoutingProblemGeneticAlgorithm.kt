package genetic_algorithm

import VehicleRoutingProblem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlin.random.Random

class VehicleRoutingProblemGeneticAlgorithm : VehicleRoutingProblem {
    private suspend fun geneticAlgorithm(
        distMatrix: Array<DoubleArray>,
        populationSize: Int,
        numGenerations: Int,
        numberOfRoutes: Int,
        mutationRate: Double = 0.1,
    ): List<List<Int>> {
        val population = (0 until populationSize).map { Array(numberOfRoutes) { randomRoute(distMatrix.size) } }

        (0 until numGenerations).asFlow()
            .buffer(Channel.UNLIMITED)
            .flowOn(Dispatchers.Default)
            .fold(population) { currentPopulation, _ ->
                val fitness = currentPopulation.map { evaluateFitness(it, distMatrix) }
//                val bestRoutes = selectBest(currentPopulation, fitness)
//                val offspring = crossover(bestRoutes)
                println("Minimum fitness: ${fitness.min()}")
                println("Average fitness: ${fitness.average()}")
//                println("Optimal route: ${currentPopulation[fitness.indexOf(fitness.min())]}")
//                mutate(offspring, mutationRate, distMatrix.size)
                listOf()
            }.minBy {
                evaluateFitness(it, distMatrix)
            }
        TODO()
    }

    private fun randomRoute(n: Int): List<Int> = (0 until n).toList().shuffled()

    private fun evaluateFitness(routes: Array<List<Int>>, distMatrix: Array<DoubleArray>): Double =
        routes.maxOf { route -> route.windowed(2).sumOf { distMatrix[it[0]][it[1]] } }

    private fun selectBest(population: List<List<List<Int>>>, fitness: List<Double>): List<List<List<Int>>> =
        with(fitness.min()) { population.zip(fitness).filter { it.second == this }.map { it.first } }

    private fun crossover(bestRoutes: List<List<Int>>): List<List<Int>> = bestRoutes.flatMap { parent1 ->
        bestRoutes.map { parent2 ->
            parent1
                .zip(parent2)
                .map {
                    if (Math.random() < 0.5) it.first else it.second
                }
        }
    }

    private fun mutate(
        population: List<Array<List<Int>>>,
        mutationRate: Double,
    ): List<Array<List<Int>>> {
        if (Random.nextDouble(0.0, 1.0) >= mutationRate) {
            //mutate
            return population
        } else {
            return population
        }
    }

    private fun allVerticesAreVisited(routes: Array<List<Int>>, numberOfVertices: Int): Boolean {
        return routes.flatMap { it }.toSet().size == numberOfVertices
    }


    override suspend fun solve(
        numberOfRoutes: Int,
        distMatrix: Array<DoubleArray>,
    ): List<List<Int>> {
        return geneticAlgorithm(
            distMatrix = distMatrix,
            populationSize = 10,
            numGenerations = 10000000,
            numberOfRoutes = numberOfRoutes
        )
    }
}

fun main() {
    val a = "saTISFYING THE VEHICLE CAPACITY CONSTRAINT"
    println(a.lowercase())
}

