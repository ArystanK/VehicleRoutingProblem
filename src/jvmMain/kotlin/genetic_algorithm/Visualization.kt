package genetic_algorithm

import database.Database
import org.jetbrains.letsPlot.GGBunch
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot

fun main() {
    val fitness = Database.getFitness()
    val data1 = mapOf(
        "average fitness" to fitness.map { it.first },
        "generation" to fitness.indices
    )

    val p1 =
        letsPlot(data1) + geomPoint(color = "red", alpha = .3) { x = "generation"; y = "average fitness" }
    val data2 = mapOf(
        "max fitness" to fitness.map { it.second },
        "generation" to fitness.indices
    )
    val p2 =
        letsPlot(data2) + geomPoint(color = "green", alpha = .3) { x = "generation"; y = "max fitness" }
    val bunch = GGBunch()
        .addPlot(p1, 0, 0)
        .addPlot(p2, 1000, 0)
    bunch.show()
}