package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Entity for the routes table
object RoutesList : IntIdTable("routes") {
    val sourceColumn = varchar("source", 50)
    val routesId = integer("routes_id")
}

class RoutesListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoutesListEntity>(RoutesList)

    var source by RoutesList.sourceColumn
    var routesId by RoutesList.routesId
}