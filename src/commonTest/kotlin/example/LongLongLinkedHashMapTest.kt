package example

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val MAX_VALUE = 1000L
private const val TESTS_COUNT = 1e6.toInt()

class LongLongLinkedHashMapTest {
    private val expectedHashMap = linkedMapOf<Long, Long>()
    private val actualHashMap = LongLongLinkedHashMap()
    private val random = Random

    @Test
    fun invalidCapacityTest() {
        assertFailsWith(IllegalArgumentException::class, "Capacity must be positive.")
        { LongLongLinkedHashMap(-1) }
    }


    @Test
    fun correctnessTest() {
        repeat(TESTS_COUNT) {
            when (random.nextInt(9)) {
                0 -> testClear()
                1 -> testPut()
                2 -> testGet()
                3 -> testRemove()
                4 -> testSize()
                5 -> testContainsKey()
                6 -> testIsEmpty()
                7 -> testPutAll()
                8 -> testContainsValue()
            }
            assertEquals<Map<Long, Long>>(expectedHashMap, actualHashMap)
            assertEquals(expectedHashMap.entries, actualHashMap.entries)
            assertEquals(expectedHashMap.keys, actualHashMap.keys)
        }
    }

    private fun createKey() = random.nextLong(-MAX_VALUE, MAX_VALUE)
    private fun createValue() = random.nextLong(-MAX_VALUE, MAX_VALUE)

    private fun testClear() {
        expectedHashMap.clear()
        actualHashMap.clear()
    }

    private fun testPut() {
        val key = createKey()
        val value = createValue()
        assertEquals(expectedHashMap.put(key, value), actualHashMap.put(key, value))
    }

    private fun testGet() {
        val key = createKey()
        assertEquals(expectedHashMap[key], actualHashMap[key])
    }

    private fun testRemove() {
        val key = createKey()
        assertEquals(expectedHashMap.remove(key), actualHashMap.remove(key))
    }

    private fun testSize() {
        assertEquals(expectedHashMap.size, actualHashMap.size)
    }

    private fun testContainsKey() {
        val key = createKey()
        assertEquals(expectedHashMap.containsKey(key), actualHashMap.containsKey(key))
    }

    private fun testIsEmpty() {
        assertEquals(expectedHashMap.isEmpty(), actualHashMap.isEmpty())
    }

    private fun testPutAll() {
        val count = random.nextInt(10)
        val map = generateSequence { createKey() to createValue() }.take(count).toMap()
        expectedHashMap.putAll(map)
        actualHashMap.putAll(map)
    }

    private fun testContainsValue() {
        val value = createValue()
        assertEquals(expectedHashMap.containsValue(value), actualHashMap.containsValue(value))
    }
}
