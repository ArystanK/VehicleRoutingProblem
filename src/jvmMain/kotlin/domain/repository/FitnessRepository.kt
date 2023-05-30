package domain.repository

import data.database.VRPDatabase
import data.database.entities.BusStops
import data.database.entities.Fitness
import data.database.entities.FitnessList
import kotlinx.coroutines.coroutineScope

class FitnessRepository {
    suspend fun safeFitness(fitnessList: FitnessList, avgFitness: Double, maxFitness: Double): Result<Fitness> =
        coroutineScope {
            Result.success(
                VRPDatabase.createFitness(
                    fitnessList = fitnessList,
                    avgFitness = avgFitness,
                    maxFitness = maxFitness
                )
            )
        }

    suspend fun safeFitnessList(busStops: BusStops): Result<FitnessList> = coroutineScope {
        Result.success(VRPDatabase.createFitnessList(busStops))
    }

    suspend fun safeFitnessList(busStopsId: Int): Result<FitnessList> = coroutineScope {
        val busStops = VRPDatabase.getBusStopsById(busStopsId)
            ?: return@coroutineScope Result.failure(Exception("Cannot find bus stops"))
        Result.success(VRPDatabase.createFitnessList(busStops))
    }
}