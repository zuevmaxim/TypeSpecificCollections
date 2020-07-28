package test.jmh.map

interface Storage<T> {
    fun get(index: Int): T
    val size: Int
}

expect class LongStorage(keys: LongArray) : Storage<Long>
class CommonLongStorage(private val keys: LongArray) : Storage<Long> {
    override fun get(index: Int) = keys[index]
    override val size: Int
        get() = keys.size
}

expect class IntStorage(keys: IntArray) : Storage<Int>
class CommonIntStorage(private val keys: IntArray) : Storage<Int> {
    override fun get(index: Int) = keys[index]
    override val size: Int
        get() = keys.size
}
