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

    protected var index = 0

    override fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int) {
        this.keys = keys
        this.map = map
        index = 0
    }

    override fun test(): Any? {
        index++
        if (index == keys.size) {
            index = 0
        }
        return null
    }
}

internal class MapGetTest : AbstractMapLongTest() {
    override fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int) {
        super.setUp(keys, map, oneFailOutOf)
        for (key in keys) {
            map.put(key + if (key % oneFailOutOf.toLong() == 0L) 1 else 0, key)
        }
    }

    override fun test(): Long? {
        super.test()
        return map.get(keys[index])
    }

    companion object {
        const val NAME = "get"
    }
}

internal class MapPutTest : AbstractMapLongTest() {
    override fun test(): Any? {
        super.test()
        val key = keys[index]
        return map.put(key, key)
    }

    companion object {
        const val NAME = "put"
    }
}

internal class MapRemoveTest : AbstractMapLongTest() {
    private var removeIndex = 0

    override fun setUp(keys: LongArray, map: TestingMap, oneFailOutOf: Int) {
        super.setUp(keys, map, oneFailOutOf)
        removeIndex = 0
    }

    override fun test(): Any? {
        super.test()
        val key = keys[index]
        map.put(key, key)
        if (index and 1 == 0) {
            map.remove(keys[removeIndex++])
            if (removeIndex == keys.size) {
                removeIndex = 0
            }
        }
        return map.size()
    }

    companion object {
        const val NAME = "remove"
    }
}
