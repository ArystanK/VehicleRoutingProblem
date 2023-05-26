import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphAlgorithmsTest {
    @Test
    fun removeFalseTest() {
        assertEquals(
            actual = arrayOf(
                intArrayOf(0, 0, 0, 0, 0).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
                intArrayOf(0, 1, 0, 0, 1).toBooleanArray(),
                intArrayOf(0, 0, 0, 0, 0).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
            ).filterFalse(),
            expected = mapOf(
                1 to mapOf(1 to false, 2 to true, 4 to false),
                2 to mapOf(1 to true, 2 to false, 4 to true),
                4 to mapOf(1 to false, 2 to true, 4 to false)
            )
        )
    }

    @Test
    fun getConnectedComponentsOneComponentTest() {
        assertEquals(
            expected = listOf(listOf(1, 2, 4)),
            actual = arrayOf(
                intArrayOf(0, 0, 0, 0, 0).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
                intArrayOf(0, 1, 0, 0, 1).toBooleanArray(),
                intArrayOf(0, 0, 0, 0, 0).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
            ).filterFalse().getConnectedComponents()
        )
    }

    @Test
    fun getConnectedComponentsTwoComponentTest() {
        assert(
            arrayOf(
                intArrayOf(0, 0, 0, 0, 1).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
                intArrayOf(0, 1, 0, 1, 0).toBooleanArray(),
                intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
                intArrayOf(1, 0, 0, 0, 0).toBooleanArray(),
            ).filterFalse().getConnectedComponents().let {
                it == listOf(1 to 3, 0 to 4)
                        || it == listOf(0 to 4, 1 to 3)
            }
        )
    }

    @Test
    fun toRouteTest() {
        arrayOf(
            intArrayOf(0, 1, 0).toBooleanArray(),
            intArrayOf(1, 0, 1).toBooleanArray(),
            intArrayOf(0, 1, 0).toBooleanArray(),
        ).toRoute(false).let {
            assertEquals(
                actual = it,
                expected = listOf(0, 1, 2)
            )
        }
    }

    @Test
    fun connectTest() {
        listOf(listOf(1, 2, 3), listOf(0, 4)).connect(
            arrayOf(
                doubleArrayOf(Double.POSITIVE_INFINITY, 2.0, 2 * sqrt(2.0), sqrt(2.0), 1.0),
                doubleArrayOf(2.0, Double.POSITIVE_INFINITY, 1.0, sqrt(2.0), 2 * sqrt(2.0)),
                doubleArrayOf(2 * sqrt(2.0), 1.0, Double.POSITIVE_INFINITY, 1.0, 2.0),
                doubleArrayOf(sqrt(2.0), sqrt(2.0), 1.0, Double.POSITIVE_INFINITY, 1.0),
                doubleArrayOf(1.0, 2 * sqrt(2.0), 2.0, 1.0, Double.POSITIVE_INFINITY)
            )
        ).let {
            assertEquals(
                actual = it,
                expected = listOf(1, 2, 3, 4, 0)
            )
        }
    }

    @Test
    fun toAdjacencyMatrixTest() {
        assertEquals(
            actual = listOf(1, 2, 3, 4, 0).toAdjacencyMatrix().toDiagonalMatrix().joinToString("\n") { it.joinToString { it.toInt().toString() } },
            expected = arrayOf(
                intArrayOf(0, 0, 0, 0, 1),
                intArrayOf(0, 0, 1, 0, 0),
                intArrayOf(0, 1, 0, 1, 0),
                intArrayOf(0, 0, 1, 0, 1),
                intArrayOf(1, 0, 0, 1, 0)
            ).joinToString("\n") { it.joinToString() }
        )
    }

    @Test
    fun connectComponentsTest() {
        arrayOf(
            intArrayOf(0, 0, 0, 0, 1).toBooleanArray(),
            intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
            intArrayOf(0, 1, 0, 1, 0).toBooleanArray(),
            intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
            intArrayOf(1, 0, 0, 0, 0).toBooleanArray(),
        ).filterFalse().connectComponents(
            arrayOf(
                doubleArrayOf(Double.POSITIVE_INFINITY, 2.0, 2 * sqrt(2.0), sqrt(2.0), 1.0),
                doubleArrayOf(2.0, Double.POSITIVE_INFINITY, 1.0, sqrt(2.0), 2 * sqrt(2.0)),
                doubleArrayOf(2 * sqrt(2.0), 1.0, Double.POSITIVE_INFINITY, 1.0, 2.0),
                doubleArrayOf(sqrt(2.0), sqrt(2.0), 1.0, Double.POSITIVE_INFINITY, 1.0),
                doubleArrayOf(1.0, 2 * sqrt(2.0), 2.0, 1.0, Double.POSITIVE_INFINITY)
            )
        ).let {
            assertEquals(
                actual = it.toDiagonalMatrix().joinToString("\n") { it.joinToString { it.toInt().toString() } },
                expected = arrayOf(
                    intArrayOf(0, 0, 0, 0, 1).toBooleanArray(),
                    intArrayOf(0, 0, 1, 0, 0).toBooleanArray(),
                    intArrayOf(0, 1, 0, 1, 0).toBooleanArray(),
                    intArrayOf(0, 0, 1, 0, 1).toBooleanArray(),
                    intArrayOf(1, 0, 0, 0, 0).toBooleanArray(),
                ).toDiagonalMatrix().joinToString("\n") { it.joinToString { it.toInt().toString() } }
            )
        }
    }

}