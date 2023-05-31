package domain.poko

import data.database.entities.BusStopsEntity

fun BusStopsEntity.toBusStops() = BusStops(
    id = id.value,
    startSearchBoxLatitude = startSearchBoxLatitude,
    startSearchBoxLongitude = startSearchBoxLongitude,
    endSearchBoxLatitude = endSearchBoxLatitude,
    endSearchBoxLongitude = endSearchBoxLongitude,
)

data class BusStops(
    val id: Int,
    val startSearchBoxLatitude: Double,
    val startSearchBoxLongitude: Double,
    val endSearchBoxLatitude: Double,
    val endSearchBoxLongitude: Double,
)