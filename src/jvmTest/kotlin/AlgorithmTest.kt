import org.junit.Test
import kotlin.test.assertEquals

class AlgorithmTest {
    @Test
    fun binarySearchTest() {
        val list = listOf(0.0, 0.23, 0.24, 0.27)
        assertEquals(0, list.myBinarySearch(0.22))
        assertEquals(0, list.myBinarySearch(0.1))
        assertEquals(1, list.myBinarySearch(0.23))
        assertEquals(1, list.myBinarySearch(0.233))
        assertEquals(2, list.myBinarySearch(0.24))
    }
}