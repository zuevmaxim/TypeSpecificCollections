package test.jmh.map

import kotlin.random.Random
import kotlin.reflect.KClass

fun <K : Any> createOperation(operation: String): MapTest<K> = when (operation) {
    MapGetTest.NAME -> MapGetTest()
    MapPutTest.NAME -> MapPutTest()
    MapRemoveTest.NAME -> MapRemoveTest()
    else -> error("Unexpected operation $operation.")
}

interface MapTest<K> {
    fun setUp(keys: Storage<K>, map: TestingMap<K>, oneFailOutOf: Int)
    fun test(): K?
}

internal abstract class AbstractMapTest<K> : MapTest<K> {
    lateinit var keys: Storage<K>

    lateinit var map: TestingMap<K>

    protected var index = 0

    override fun setUp(keys: Storage<K>, map: TestingMap<K>, oneFailOutOf: Int) {
        this.keys = keys
        this.map = map
        index = 0
    }

    fun increment() {
        index++
        if (index == keys.size) {
            index = 0
        }
    }
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
private fun <K> random(random: Random, clazz: KClass<*>) = when (clazz) {
    Long::class -> random.nextLong()
    Int::class -> random.nextInt()
    else -> error("Type $clazz not implemented.")
} as K

internal class MapGetTest<K : Any> : AbstractMapTest<K>() {
    override fun setUp(keys: Storage<K>, map: TestingMap<K>, oneFailOutOf: Int) {
        super.setUp(keys, map, oneFailOutOf)
        val random = Random(43)
        for (index in 0 until keys.size) {
            val key = keys.get(index)
            val k = if (index % oneFailOutOf == 0) {
                random(random, key::class)
            } else {
                key
            }
            map.put(k, key)
        }
    }

    override fun test(): K? {
        increment()
        return map.get(keys.get(index))
    }

    companion object {
        const val NAME = "get"
    }
}

internal class MapPutTest<K> : AbstractMapTest<K>() {
    override fun test(): K? {
        increment()
        val key = keys.get(index)
        return map.put(key, key)
    }

    companion object {
        const val NAME = "put"
    }
}

internal class MapRemoveTest<K> : AbstractMapTest<K>() {
    private var removeIndex = 0

    override fun setUp(keys: Storage<K>, map: TestingMap<K>, oneFailOutOf: Int) {
        super.setUp(keys, map, oneFailOutOf)
        removeIndex = 0
    }

    override fun test(): K? {
        increment()
        val key = keys.get(index)
        return if (index and 1 == 1) {
            if (removeIndex == keys.size) {
                removeIndex = 0
            }
            map.remove(keys.get(removeIndex++))
        } else {
            map.put(key, key)
        }
    }

    companion object {
        const val NAME = "remove"
    }
}
