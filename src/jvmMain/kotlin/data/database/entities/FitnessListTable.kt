package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object FitnessListTable : IntIdTable("fitness_list") {
    val busStopsId = reference("bus_stops_id", BusStopsTable.id)
}

class FitnessListEntity(id: EntityID<Int>) : IntEntity(id) {
    constructor(id: Int) : this(EntityID(id, FitnessListTable))

    companion object : IntEntityClass<FitnessListEntity>(FitnessListTable)

    var busStops by BusStopsEntity referencedOn FitnessListTable.busStopsId
}