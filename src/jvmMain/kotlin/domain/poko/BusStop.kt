package domain.poko

import data.database.entities.BusStopEntity
import data.database.entities.BusStops
import data.database.entities.toBusStops

fun BusStopEntity.toBusStop() = BusStop(
    id = id.value,
    lat = latitude,
    lon = longitude,
    address = address,
    busStops = busStops.toBusStops()
)

data class BusStop(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val address: String,
    val busStops: BusStops,
) {
    fun toLocationPair(): Pair<Double, Double> = lat to lon
}
