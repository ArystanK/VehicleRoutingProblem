package genetic_algorithm

import database.Database
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import unique
import kotlin.random.Random

class World(
    private val distanceMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
    private val generationSize: Int = 100,
    private val populationSize: Int = 100,
    private val mutationRate: Double = 0.1,
    initialRoutes: List<List<Int>>? = null,
) {
    var population: MutableList<Routes> =
        MutableList(populationSize) {
            Routes(
                distMatrix = distanceMatrix,
                numberOfRoutes = numberOfRoutes,
                mutationRate = mutationRate,
                initialRoutes = initialRoutes
            )
        }
    private var cumulativeProportions: List<Double> = updateCumulativeProportion()

    private fun tournamentSelection(tournamentSize: Int, previousSelected: Routes?): Routes {
        val tournament = population.filter { it != previousSelected }.shuffled().take(tournamentSize)
        return tournament.maxBy { it.fitness }
    }

    private fun biasedRandomSelection(): Routes {
        val selectedValue = Random.nextDouble()
        return population[cumulativeProportions.indexOfFirst { it >= selectedValue }]
    }

    private fun updateCumulativeProportion(): List<Double> {
        val sum = population.sumOf { it.fitness }
        val proportions = population.map { sum / it.fitness }
        val proportionSum = proportions.sum()
        val normalizedProportions = proportions.map { it / proportionSum }
        return normalizedProportions.runningReduce { acc, d -> acc + d }
    }

    private fun onGeneration(tournamentSize: Int) {
        cumulativeProportions = updateCumulativeProportion()
        val offspring = mutableListOf<Routes>()
        while (offspring.size < populationSize) {
            val (parent1, parent2) = getParents(tournamentSize)
            var (offspring1, offspring2) = parent1.crossover(parent2)
            offspring1 = offspring1.mutate()
            offspring2 = offspring2.mutate()
            offspring.add(offspring1)
            offspring.add(offspring2)
        }
        population.addAll(offspring)
        population.sortByDescending { it.fitness }
        population = population.take(populationSize).toMutableList()
    }

    suspend fun solve() {
        repeat(generationSize) {
            onGeneration(tournamentSize = Random.nextInt((populationSize * 0.2).toInt(), populationSize))
            val fitnesses = population.map { it.fitness }
            coroutineScope {
                launch {
                    Database.saveFitness(fitnesses.average() to fitnesses.max())
                }
            }
        }
    }

    private fun getParent(tournamentSize: Int, previousSelected: Routes? = null): Routes {
        if (Random.nextDouble() > 0.5) return tournamentSelection(tournamentSize, previousSelected)
        return biasedRandomSelection()
    }

    private fun getParents(tournamentSize: Int): Pair<Routes, Routes> {
        val parent1 = getParent(tournamentSize)
        val parent2 = unique(getParent(tournamentSize, parent1), parent1) { getParent(tournamentSize, parent1) }
        return parent1 to parent2
    }

}
