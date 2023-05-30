package domain.poko

import data.database.entities.FitnessEntity

fun FitnessEntity.toFitness() = Fitness(
    id = id.value,
    fitnessList = fitnessList.toFitnessList(),
    maxFitness = maxFitness,
    avgFitness = avgFitness
)

data class Fitness(val id: Int, val fitnessList: FitnessList, val maxFitness: Double, val avgFitness: Double)