package test.jmh.map

interface TestingMap<K> {
    fun size(): Int
    fun get(key: K): K?
    fun put(key: K, value: K): K?
    fun remove(key: K): K?
}
