package test.jmh.map

import example.LongLongLinkedHashMap
import example.createLinkedOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap

fun createImplementation(name: String): TestingMap = when (name) {
    JavaCollectionsMap.NAME -> JavaCollectionsMap(CAPACITY, LOAD_FACTOR)
    MyMap.NAME -> MyMap(CAPACITY, LOAD_FACTOR)
    FastUtilMap.NAME -> FastUtilMap(CAPACITY, LOAD_FACTOR)
    MyGenericMap.NAME -> MyGenericMap(CAPACITY, LOAD_FACTOR)
    else -> error("Unexpected implementation name: $name.")
}

internal abstract class AbstractTestingMap : TestingMap {
    protected abstract val map: MutableMap<Long, Long>
    override fun size() = map.size
    override fun get(key: Long) = map[key]
    override fun put(key: Long, value: Long) = map.put(key, value)
    override fun remove(key: Long) = map.remove(key)
}

internal class JavaCollectionsMap(capacity: Int, loadFactor: Float) : AbstractTestingMap() {
    override val map = LinkedHashMap<Long, Long>(capacity, loadFactor)

    companion object {
        const val NAME = "JAVA"
    }
}

internal class MyMap(capacity: Int, loadFactor: Float) : AbstractTestingMap() {
    override val map = LongLongLinkedHashMap(capacity, loadFactor)

    companion object {
        const val NAME = "MY_MAP"
    }
}

internal class MyGenericMap(capacity: Int, loadFactor: Float) : AbstractTestingMap() {
    override val map = createLinkedOpenHashMap<Long, Long>(capacity, loadFactor)

    companion object {
        const val NAME = "MY_GENERIC_MAP"
    }
}

internal class FastUtilMap(capacity: Int, loadFactor: Float) : AbstractTestingMap() {
    override val map = Long2LongLinkedOpenHashMap(capacity, loadFactor)

    companion object {
        const val NAME = "FastUtil"
    }
}
