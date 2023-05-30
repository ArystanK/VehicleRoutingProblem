package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object FitnessListTable : IntIdTable("fitness_list") {
    val busStopsId: Column<Int> = integer("bus_stops_id")
        .references(BusStopsTable.id)
}

class FitnessListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FitnessListEntity>(FitnessListTable)

    var busStops by BusStopsEntity referencedOn FitnessListTable.busStopsId
}