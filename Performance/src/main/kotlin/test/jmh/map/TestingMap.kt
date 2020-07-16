package test.jmh.map

interface TestingMap {
    fun size(): Int
    fun get(key: Long): Long?
    fun put(key: Long, value: Long): Long?
    fun remove(key: Long): Long?
}
