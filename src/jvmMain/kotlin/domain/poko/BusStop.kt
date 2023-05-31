package domain.poko

import center.sciprog.maps.coordinates.Gmc
import data.database.entities.BusStopEntity
import space.kscience.kmath.geometry.Degrees

fun BusStopEntity.toBusStop() = BusStop(
    id = index,
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
    fun toGmc(): Gmc = Gmc(Degrees(lat), Degrees(lon))
}
