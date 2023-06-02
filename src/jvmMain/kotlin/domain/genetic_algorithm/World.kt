package domain.genetic_algorithm

import domain.poko.BusStops
import domain.repository.FitnessRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import myBinarySearch
import unique
import kotlin.random.Random

class World(
    private val distanceMatrix: Array<DoubleArray>,
    private val numberOfRoutes: Int,
    private val generationSize: Int = 100,
    private val populationSize: Int = 100,
    private val mutationRate: Double = 0.1,
    private val fitnessRepository: FitnessRepository,
    private val busStop: BusStops,
    initialRoutes: List<List<Int>>? = null,
) {
    private var population: MutableList<Routes> =
        MutableList(populationSize) {
            Routes(
                distMatrix = distanceMatrix,
                numberOfRoutes = numberOfRoutes,
                mutationRate = mutationRate
            ).let { if (initialRoutes == null) it else it.mutate() }
        }
    private var cumulativeProportions: List<Double> =
        updateCumulativeProportion()

    private tailrec fun tournamentSelection(
        previousSelected: Routes?,
    ): Routes {
        if (population.all { it == previousSelected })
            return population.first()
        val tournamentSize = Random.nextInt(
            from = (population.size * 0.5).toInt(),
            until = population.size
        )
        val tournament = population.filter {
            it != previousSelected
        }.shuffled().take(tournamentSize)
        if (tournament.isEmpty())
            return tournamentSelection(previousSelected)
        return tournament.maxBy { it.fitness }
    }

    private fun biasedRandomSelection(): Routes {
        val selectedValue = Random.nextDouble()
        if (selectedValue >= cumulativeProportions.last())
            return population.last()
        if (selectedValue <= cumulativeProportions.first())
            return population.first()
        val index = cumulativeProportions
            .myBinarySearch(selectedValue)
        return population[index]
    }

    private fun updateCumulativeProportion(): List<Double> {
        val sum = population.sumOf { it.fitness }
        val proportions = population.map {
            sum / it.fitness
        }
        val proportionSum = proportions.sum()
        val normalizedProportions = proportions
            .map { it / proportionSum }
        return normalizedProportions
            .runningReduce { acc, d -> acc + d }
    }

    private fun onGeneration(): List<Double> {
        cumulativeProportions = updateCumulativeProportion()
        val offspring = mutableListOf<Routes>()
        while (offspring.size < populationSize) {
            val (parent1, parent2) = getParents()
            var (offspring1, offspring2) = parent1.crossover(parent2)
            offspring1 = offspring1.mutate()
            offspring2 = offspring2.mutate()
            offspring.add(offspring1)
            offspring.add(offspring2)
        }
        population.addAll(offspring)
        population.sortByDescending { it.fitness }
        population = population.take(populationSize).toMutableList()
        return population.map { it.fitness }
    }

    suspend fun solve(): Routes? {
        return coroutineScope {
            val fitnessListObject = fitnessRepository.safeFitnessList(busStop).getOrNull() ?: return@coroutineScope null
            repeat(generationSize) {
                val fitness = async { onGeneration() }
                launch {
                    val fitnessList = fitness.await()
                    fitnessRepository.safeFitness(
                        fitnessList = fitnessListObject,
                        avgFitness = fitnessList.average(),
                        maxFitness = fitnessList.max()
                    )
                }
            }
            return@coroutineScope population.maxBy { it.fitness }
        }
    }

    private fun getParent(previousSelected: Routes? = null): Routes {
        if (Random.nextDouble() > 0.5)
            return tournamentSelection(previousSelected)
        return biasedRandomSelection()
    }

    private fun getParents(): Pair<Routes, Routes> {
        val parent1 = getParent()
        val parent2 = unique(
            a = getParent(parent1),
            b = parent1,
            f = { getParent(parent1) }
        )
        return parent1 to parent2
    }
}
