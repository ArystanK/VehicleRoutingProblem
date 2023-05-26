import kotlinx.coroutines.runBlocking
import linear_programming.solveVRPLinearProgramming
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.test.assertEquals

class VRPLPTest {
    @Test
    fun testOneRouteSolution(): Unit = runBlocking {
        assertEquals(
            expected = arrayOf(
                arrayOf(
                    booleanArrayOf(false, true, false, false),
                    booleanArrayOf(false, false, false, true),
                    booleanArrayOf(true, false, false, false),
                    booleanArrayOf(false, false, true, false),
                )
            ).joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } },
            actual = solveVRPLinearProgramming(
                numberOfRoutes = 1,
                distMatrix = arrayOf(
                    doubleArrayOf(0.0, 1.0, 1.0, sqrt(2.0)),
                    doubleArrayOf(1.0, 0.0, sqrt(2.0), 1.0),
                    doubleArrayOf(1.0, sqrt(2.0), 0.0, 1.0),
                    doubleArrayOf(sqrt(2.0), 1.0, 1.0, 0.0)
                )
            ).joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } },
        )
    }

    @Test
    fun testTwoRouteFourByFourSolution(): Unit = runBlocking {
        solveVRPLinearProgramming(
            numberOfRoutes = 2,
            distMatrix = arrayOf(
                doubleArrayOf(0.0, 1.0, 1.0, sqrt(2.0)),
                doubleArrayOf(1.0, 0.0, sqrt(2.0), 1.0),
                doubleArrayOf(1.0, sqrt(2.0), 0.0, 1.0),
                doubleArrayOf(sqrt(2.0), 1.0, 1.0, 0.0)
            )
        ).also {
            println(it.joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } })
        }
    }

    @Test
    fun testTwoRouteFiveByFiveSolution(): Unit = runBlocking {
        val d = sqrt(2 + cos(108 * PI / 180))
        solveVRPLinearProgramming(
            numberOfRoutes = 2,
            distMatrix = arrayOf(
                doubleArrayOf(0.0, 1.0, d, d, 1.0),
                doubleArrayOf(1.0, 0.0, 1.0, d, d),
                doubleArrayOf(d, 1.0, 0.0, 1.0, d),
                doubleArrayOf(d, d, 1.0, 0.0, 1.0),
                doubleArrayOf(1.0, d, d, 1.0, 0.0),
            )
        ).also {
            println(it.joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } })
        }
    }

    @Test
    fun testTwoRouteTenByTenSolution() {
        val d = sqrt(2 + cos(108 * PI / 180))
        solveVRPLinearProgramming(
            numberOfRoutes = 2,
            distMatrix = Array(10) { i ->
                DoubleArray(10) { j ->
                    when {
                        i == j -> 0.0
                        (10 + i - j) % 10 == 1 -> 1.0
                        else -> d
                    }
                }
            }
        ).also {
            println(it.joinToString("\n\n") { it.joinToString("\n") { it.joinToString() } })
        }
    }
}