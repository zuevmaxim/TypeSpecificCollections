package test.jmh.map

fun createOperation(operation: String): MapTest = when (operation) {
    MapGetTest.NAME -> MapGetTest()
    MapPutTest.NAME -> MapPutTest()
    MapRemoveTest.NAME -> MapRemoveTest()
    else -> error("Unexpected operation $operation.")
}

interface MapTest {
    fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int)
    fun test(): Any?

}

internal abstract class AbstractMapLongTest : MapTest {
    lateinit var keys: LongArray

    lateinit var map: TestingMap
    override fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int) {
        this.keys = keys
        this.map = map
    }
}

internal class MapGetTest : AbstractMapLongTest() {
    override fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int) {
        super.setUp(keys, map, oneFailOutOf)
        for (key in keys) {
            map.put(key + if (key % oneFailOutOf.toLong() == 0L) 1 else 0, key)
        }
    }

    override fun test(): Int {
        var res = 0L
        for (key in keys) {
            res = res xor (map.get(key) ?: 0)
        }
        return res.toInt()
    }

    companion object {
        const val NAME = "get"
    }
}

internal class MapPutTest : AbstractMapLongTest() {
    override fun test(): Any? {
        repeat(2) {
            for (key in keys) {
                map.put(key, key)
            }
        }
        return map.size()
    }

    companion object {
        const val NAME = "put"
    }
}

internal class MapRemoveTest : AbstractMapLongTest() {
    override fun test(): Any? {
        var add = 0
        var remove = 0
        while (add < keys.size) {
            map.put(keys[add], keys[add])
            ++add
            map.put(keys[add], keys[add])
            ++add
            map.remove(keys[remove++])
        }
        return map.size()
    }

    companion object {
        const val NAME = "remove"
    }
}
