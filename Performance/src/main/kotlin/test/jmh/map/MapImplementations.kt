package test.jmh.map

import example.createLinkedHashMap
import example.createLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap

inline fun <reified K> createImplementation(name: String): TestingMap<K> = when (name) {
    JavaCollectionsMap.NAME -> JavaCollectionsMap(CAPACITY, LOAD_FACTOR)
    MyMap.NAME -> MyMap(createLinkedHashMap(CAPACITY, LOAD_FACTOR))
    FastUtilMap.NAME -> FastUtilMap(createFastUtilMap(CAPACITY, LOAD_FACTOR))
    MyGenericMap.NAME -> MyGenericMap(createLinkedOpenHashMap(CAPACITY, LOAD_FACTOR))
    else -> error("Unexpected implementation name: $name.")
}

abstract class AbstractTestingMap<K> : TestingMap<K> {
    protected abstract val map: MutableMap<K, K>
    override fun size() = map.size
    override fun get(key: K) = map[key]
    override fun put(key: K, value: K) = map.put(key, value)
    override fun remove(key: K) = map.remove(key)
}

class JavaCollectionsMap<K>(capacity: Int, loadFactor: Float) : AbstractTestingMap<K>() {
    override val map = LinkedHashMap<K, K>(capacity, loadFactor)

    companion object {
        const val NAME = "JAVA"
    }
}

class MyMap<K>(override val map: MutableMap<K, K>) : AbstractTestingMap<K>() {
    companion object {
        const val NAME = "MY_MAP"
    }
}

class MyGenericMap<K>(override val map: MutableMap<K, K>) : AbstractTestingMap<K>() {
    companion object {
        const val NAME = "MY_GENERIC_MAP"
    }
}

class FastUtilMap<K>(override val map: MutableMap<K, K>) : AbstractTestingMap<K>() {
    companion object {
        const val NAME = "FastUtil"
    }
}

inline fun <reified T> createFastUtilMap(capacity: Int, loadFactor: Float): MutableMap<T, T> = when (T::class) {
    Long::class -> Long2LongLinkedOpenHashMap(capacity, loadFactor)
    Int::class -> Int2IntLinkedOpenHashMap(capacity, loadFactor)
    else -> error("Type is not implemented")
} as MutableMap<T, T>
