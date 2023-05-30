package domain.poko

import data.database.entities.BusStops
import data.database.entities.FitnessListEntity
import data.database.entities.toBusStops

fun FitnessListEntity.toFitnessList() = FitnessList(
    id = id.value,
    busStops = busStops.toBusStops()
)

data class FitnessList(val id: Int, val busStops: BusStops)