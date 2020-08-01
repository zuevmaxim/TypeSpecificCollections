package test.jmh.map

import example.createChainedLinkedHashMap
import example.createLinkedHashMap
import example.createLinkedOpenHashMap

inline fun <reified K> createImplementation(name: String): TestingMap<K> = when (name) {
    StdCollectionsMap.NAME -> StdCollectionsMap()
    MyMap.NAME -> MyMap(createLinkedHashMap())
    MyGenericMap.NAME -> MyGenericMap(createLinkedOpenHashMap())
    MyChainedMap.NAME -> MyChainedMap(createChainedLinkedHashMap())
    else -> error("Unexpected implementation name: $name.")
}

abstract class AbstractTestingMap<K> : TestingMap<K> {
    abstract val map: MutableMap<K, K>
    override fun size() = map.size
    override fun get(key: K) = map[key]
    override fun put(key: K, value: K) = map.put(key, value)
    override fun remove(key: K) = map.remove(key)
}

class StdCollectionsMap<K> : AbstractTestingMap<K>() {
    override val map = LinkedHashMap<K, K>()

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

class MyChainedMap<K>(override val map: MutableMap<K, K>) : AbstractTestingMap<K>() {
    companion object {
        const val NAME = "MY_CHAINED_MAP"
    }
}
