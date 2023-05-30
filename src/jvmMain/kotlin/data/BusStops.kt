package data

import data.database.BusStopEntity

@kotlinx.serialization.Serializable
data class BusStop(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val address: String,
)

fun BusStopEntity.toBusStop() = BusStop(
    id = this.id.value,
    lat = this.latitude,
    lon = this.longitude,
    address = this.address
)

