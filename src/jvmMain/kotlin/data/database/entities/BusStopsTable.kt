package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object BusStopsTable : IntIdTable("bus_stops") {
    val startSearchBoxLatitude: Column<Double> = double("start_search_box_lat")
    val startSearchBoxLongitude: Column<Double> = double("start_search_box_lon")
    val endSearchBoxLatitude: Column<Double> = double("end_search_box_lat")
    val endSearchBoxLongitude: Column<Double> = double("end_search_box_lon")

    init {
        uniqueIndex(startSearchBoxLatitude, startSearchBoxLongitude, endSearchBoxLatitude, endSearchBoxLongitude)
    }
}

class BusStopsEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<BusStopsEntity>(BusStopsTable)

    var startSearchBoxLatitude by BusStopsTable.startSearchBoxLatitude
    var startSearchBoxLongitude by BusStopsTable.startSearchBoxLongitude
    var endSearchBoxLatitude by BusStopsTable.endSearchBoxLatitude
    var endSearchBoxLongitude by BusStopsTable.endSearchBoxLongitude
}

fun BusStopsEntity.toBusStops() = BusStops(
    id = id.value,
    startSearchBoxLatitude = startSearchBoxLatitude,
    startSearchBoxLongitude = startSearchBoxLongitude,
    endSearchBoxLatitude = endSearchBoxLatitude,
    endSearchBoxLongitude = endSearchBoxLongitude
)

data class BusStops(
    val id: Int? = null,
    val startSearchBoxLatitude: Double,
    val startSearchBoxLongitude: Double,
    val endSearchBoxLatitude: Double,
    val endSearchBoxLongitude: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusStops
        if (id == other.id) return true

        if (startSearchBoxLatitude != other.startSearchBoxLatitude) return false
        if (startSearchBoxLongitude != other.startSearchBoxLongitude) return false
        if (endSearchBoxLatitude != other.endSearchBoxLatitude) return false
        return endSearchBoxLongitude == other.endSearchBoxLongitude
    }

    override fun hashCode(): Int {
        var result = startSearchBoxLatitude.hashCode()
        result = 31 * result + startSearchBoxLongitude.hashCode()
        result = 31 * result + endSearchBoxLatitude.hashCode()
        result = 31 * result + endSearchBoxLongitude.hashCode()
        return result
    }
}