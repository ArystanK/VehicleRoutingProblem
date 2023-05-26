package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Entity for the fitness table
object Fitness : IntIdTable("fitness") {
    val routeId = reference("route_id", RoutesList)
    val maxFitness = double("max_fitness")
    val avgFitness = double("avg_fitness")
}

class FitnessEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FitnessEntity>(Fitness)

    var route by RoutesListEntity referencedOn Fitness.routeId
    var maxFitness by Fitness.maxFitness
    var avgFitness by Fitness.avgFitness
}