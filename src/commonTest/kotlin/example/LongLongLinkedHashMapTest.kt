package example

import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val MAX_VALUE = 1000L
private const val TESTS_COUNT = 1e4.toInt()

class MapTest {
    @Test
    fun invalidCapacityTest() {
        assertFailsWith(IllegalArgumentException::class, "Capacity must be positive.")
        { LongLongLinkedHashMap(-1) }
    }

    @Test
    fun longLongMapTest() {
        LinkedHashMapTest(createLinkedHashMap<Long, Long>(), Long::class, Long::class).correctnessTest()
    }

    @Test
    fun intIntMapTest() {
        LinkedHashMapTest(createLinkedHashMap<Int, Int>(), Int::class, Int::class).correctnessTest()
    }

    @Test
    fun genericMapTest() {
        LinkedHashMapTest(createLinkedOpenHashMap<Long, Long>(), Long::class, Long::class).correctnessTest()
        LinkedHashMapTest(createLinkedOpenHashMap<Long, Int>(), Long::class, Int::class).correctnessTest()
        LinkedHashMapTest(createLinkedOpenHashMap<Int, Long>(), Int::class, Long::class).correctnessTest()
        LinkedHashMapTest(createLinkedOpenHashMap<Int, Int>(), Int::class, Int::class).correctnessTest()
    }
}

class LinkedHashMapTest<K, V>(
    private val actualHashMap: MutableMap<K, V>,
    private val keyClass: KClass<*>,
    private val valueClass: KClass<*>
) {
    private val expectedHashMap = linkedMapOf<K, V>()
    private val random = Random(42)

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
            assertEquals<Map<K, V>>(expectedHashMap, actualHashMap)
            assertEquals(expectedHashMap.keys, actualHashMap.keys)
        }
    }

    private fun random(clazz: KClass<*>): Any = when (clazz) {
        Long::class -> random.nextLong(-MAX_VALUE, MAX_VALUE)
        Int::class -> random.nextInt(-MAX_VALUE.toInt(), MAX_VALUE.toInt())
        else -> error("Not implemented")
    }

    @Suppress("UNCHECKED_CAST")
    private fun createKey() = random(keyClass) as K

    @Suppress("UNCHECKED_CAST")
    private fun createValue() = random(valueClass) as V

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
