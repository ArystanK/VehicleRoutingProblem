package domain.genetic_algorithm

import data.database.VRPDatabase
import org.jetbrains.letsPlot.GGBunch
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot

fun main() {
    val fitness = VRPDatabase.getAllFitness().groupBy { it.fitnessList }
    val data = fitness.entries.map {
        mapOf(
            "average fitness" to it.value.map { it.avgFitness },
            "generation" to it.value.indices
        ) to mapOf(
            "max fitness" to it.value.map { it.maxFitness },
            "generation" to it.value.indices
        )
    }
    val plots = data.map {
        (letsPlot(it.first) + geomPoint(color = "red", alpha = 0.3) {
            x = "generation"; y = "average fitness"
        }) to (letsPlot(it.second) + geomPoint(color = "green", alpha = .3) { x = "generation"; y = "max fitness" })
    }
    plots.forEach {
        val bunch = GGBunch()
            .addPlot(it.first, 0, 0)
            .addPlot(it.second, 605, 0)
        bunch.show()
    }
}