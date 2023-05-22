package database

import org.jetbrains.exposed.dao.id.IntIdTable

object Fitness : IntIdTable("fitness") {
    val maxFitness = double("max_fitness")
    val avgFitness = double("avg_fitness")
}