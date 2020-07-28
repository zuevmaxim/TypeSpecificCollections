package test.jmh.map

import example.createLinkedHashMap
import example.createLinkedOpenHashMap

inline fun <reified K> createImplementation(name: String): TestingMap<K> = when (name) {
    StdCollectionsMap.NAME -> StdCollectionsMap(CAPACITY, LOAD_FACTOR)
    MyMap.NAME -> MyMap(createLinkedHashMap(CAPACITY, LOAD_FACTOR))
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

class StdCollectionsMap<K>(capacity: Int, loadFactor: Float) : AbstractTestingMap<K>() {
    override val map = LinkedHashMap<K, K>(capacity, loadFactor)

    companion object {
        const val NAME = "STD"
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
