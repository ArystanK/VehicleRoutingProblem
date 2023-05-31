package domain.repository

import data.database.VRPDatabase
import domain.poko.BusStops
import domain.poko.Fitness
import domain.poko.FitnessList
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