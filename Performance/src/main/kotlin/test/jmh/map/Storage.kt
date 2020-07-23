package test.jmh.map

interface Storage<T> {
    fun get(index: Int): T
    val size: Int
}

class LongStorage(private val data: LongArray) : Storage<Long> {
    override val size = data.size
    override fun get(index: Int) = data[index]
}

class IntStorage(private val data: IntArray) : Storage<Int> {
    override val size = data.size
    override fun get(index: Int) = data[index]
}
