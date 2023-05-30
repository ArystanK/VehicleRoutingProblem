package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object FitnessTable : IntIdTable("fitness") {
    val fitnessListId: Column<Int> = integer("fitness_list_id")
        .references(FitnessListTable.id)
    val maxFitness: Column<Double> = double("max_fitness")
    val avgFitness: Column<Double> = double("avg_fitness")
}

class FitnessEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FitnessEntity>(FitnessTable)

    var fitnessListId by FitnessListEntity referencedOn FitnessTable.fitnessListId
    var maxFitness by FitnessTable.maxFitness
    var avgFitness by FitnessTable.avgFitness
}