package example

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val MAX_VALUE = 1000000L
private const val TESTS_COUNT = 1e6.toInt()

class LongLongLinkedHashMapTest {
    private val expectedHashMap = linkedMapOf<Long, Long>()
    private val actualHashMap = createLinkedOpenHashMap<Long, Long>()
    private val random = Random(42)

    @Test
    fun invalidCapacityTest() {
        assertFailsWith(IllegalArgumentException::class, "Capacity must be positive.")
        { LongLongLinkedHashMap(-1) }
    }

    @Test
    fun correctnessTest() {
        repeat(TESTS_COUNT) {
            when (random.nextInt(7)) {
                0 -> testClear()
                1 -> testPut()
                2 -> testGet()
                3 -> testRemove()
                4 -> testContainsKey()
                5 -> testPutAll()
                6 -> testContainsValue()
            }
            testSize()
            testIsEmpty()
            assertEquals<Map<Long, Long>>(expectedHashMap, actualHashMap)
            assertEquals(expectedHashMap.entries, actualHashMap.entries)
            assertEquals(expectedHashMap.keys, actualHashMap.keys)
        }
    }

    private fun createKey() = random.nextLong(-MAX_VALUE, MAX_VALUE)
    private fun createValue() = random.nextLong(-MAX_VALUE, MAX_VALUE)

    private fun testClear() {
        when (random.nextInt(4)) {
            0 -> testMapClear()
            1 -> testEntrySetClear()
            2 -> testKeysClear()
            3 -> testValuesClear()
        }
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
        assertEquals(expectedHashMap.entries.size, actualHashMap.entries.size)
        assertEquals(expectedHashMap.keys.size, actualHashMap.keys.size)
        assertEquals(expectedHashMap.values.size, actualHashMap.values.size)
    }

    private fun testContainsKey() {
        val key = createKey()
        assertEquals(expectedHashMap.containsKey(key), actualHashMap.containsKey(key))
        assertEquals(expectedHashMap.keys.contains(key), actualHashMap.keys.contains(key))
    }

    private fun testIsEmpty() {
        assertEquals(expectedHashMap.isEmpty(), actualHashMap.isEmpty())
    }

    private fun testPutAll() {
        val count = random.nextInt(1000)
        val map = generateSequence { createKey() to createValue() }.take(count).toMap()
        expectedHashMap.putAll(map)
        actualHashMap.putAll(map)
    }

    private fun testContainsValue() {
        val value = createValue()
        assertEquals(expectedHashMap.containsValue(value), actualHashMap.containsValue(value))
        assertEquals(expectedHashMap.values.contains(value), actualHashMap.values.contains(value))
    }

    private fun testMapClear() {
        expectedHashMap.clear()
        actualHashMap.clear()
    }

    private fun testEntrySetClear() {
        expectedHashMap.entries.clear()
        actualHashMap.entries.clear()
    }

    private fun testKeysClear() {
        expectedHashMap.keys.clear()
        actualHashMap.keys.clear()
    }

    private fun testValuesClear() {
        expectedHashMap.values.clear()
        actualHashMap.values.clear()
    }
}
