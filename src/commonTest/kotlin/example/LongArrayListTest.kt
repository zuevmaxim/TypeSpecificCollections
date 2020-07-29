package example

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TESTS_COUNT = 1e5.toInt()


internal class LongArrayListTest {

    private val expected = mutableListOf<Long>()
    private val actual = LongArrayList()
    private val random = Random(42)

    @Test
    fun correctnessTest() {
        repeat(TESTS_COUNT) {
            when (random.nextInt(8)) {
                0 -> testClear()
                1 -> testAdd()
                2 -> testAddIndexed()
                3 -> testAddAllIndexed()
                4 -> testGet()
                5 -> testSet()
                6 -> testRemoveIndexed()
                7 -> testRemove()
            }
            testSize()
            assertEquals(expected, actual)
        }
    }

    private fun createKey() = random.nextLong()
    private fun createIndex() = random.nextInt(actual.size)
    private fun createIndexInclusive() = random.nextInt(actual.size + 1)

    private fun testSize() {
        assertEquals(actual.size, expected.size)
        assertEquals(actual.isEmpty(), actual.isEmpty())
    }

    private fun testClear() {
        expected.clear()
        actual.clear()
    }

    private fun testAdd() {
        val key = createKey()
        expected.add(key)
        actual.add(key)
    }

    private fun testAddIndexed() {
        val key = createKey()
        val index = createIndexInclusive()
        expected.add(index, key)
        actual.add(index, key)
    }

    private fun testAddAllIndexed() {
        val size = random.nextInt(50)
        val index = createIndexInclusive()
        val keys = List(size) { createKey() }
        assertEquals(expected.addAll(index, keys), actual.addAll(index, keys))
    }

    private fun testGet() {
        if (actual.isEmpty()) return
        val index = createIndex()
        assertEquals(expected[index], actual[index])
    }

    private fun testSet() {
        if (actual.isEmpty()) return
        val index = createIndex()
        val key = createKey()
        assertEquals(expected.set(index, key), actual.set(index, key))
    }

    private fun testRemoveIndexed() {
        if (actual.isEmpty()) return
        val index = createIndex()
        assertEquals(expected.removeAt(index), actual.removeAt(index))
    }

    private fun testRemove() {
        val key = createKey()
        assertEquals(expected.remove(key), actual.remove(key))
    }
}
