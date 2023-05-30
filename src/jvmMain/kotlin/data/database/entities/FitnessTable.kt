package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object FitnessTable : IntIdTable("fitness") {
    val fitnessListId: Column<EntityID<Int>> = reference("fitness_list_id", FitnessListTable.id)
    val maxFitness: Column<Double> = double("max_fitness")
    val avgFitness: Column<Double> = double("avg_fitness")
}

class FitnessEntity(id: EntityID<Int>) : IntEntity(id) {
    constructor(id: Int) : this(EntityID(id, FitnessTable))

    companion object : IntEntityClass<FitnessEntity>(FitnessTable)

    var fitnessList by FitnessListEntity referencedOn FitnessTable.fitnessListId
    var maxFitness by FitnessTable.maxFitness
    var avgFitness by FitnessTable.avgFitness
}