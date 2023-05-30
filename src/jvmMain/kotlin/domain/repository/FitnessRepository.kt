package domain.repository

import data.database.VRPDatabase
import kotlinx.coroutines.coroutineScope

class FitnessRepository {
    suspend fun safeFitness(fitnessListId: Int, avgFitness: Double, maxFitness: Double) = coroutineScope {
        VRPDatabase.createFitness(fitnessListId = fitnessListId, avgFitness = avgFitness, maxFitness = maxFitness)
    }

    suspend fun safeFitnessList(busStopsId: Int) = coroutineScope {
        VRPDatabase.createFitnessList(busStopsId)
    }
}