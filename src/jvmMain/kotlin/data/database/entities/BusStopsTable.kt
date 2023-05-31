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
        uniqueIndex(
            startSearchBoxLatitude,
            startSearchBoxLongitude,
            endSearchBoxLatitude,
            endSearchBoxLongitude,
        )
    }
}

class BusStopsEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<BusStopsEntity>(BusStopsTable)

    var startSearchBoxLatitude by BusStopsTable.startSearchBoxLatitude
    var startSearchBoxLongitude by BusStopsTable.startSearchBoxLongitude
    var endSearchBoxLatitude by BusStopsTable.endSearchBoxLatitude
    var endSearchBoxLongitude by BusStopsTable.endSearchBoxLongitude
}

