package test.jmh.map

actual class LongStorage actual constructor(keys: LongArray) : Storage<Long> {
    private val keys = keys.toTypedArray()
    override fun get(index: Int) = keys[index]

    override val size: Int
        get() = keys.size
}

actual class IntStorage actual constructor(keys: IntArray) : Storage<Int> {
    private val keys = keys.toTypedArray()
    override fun get(index: Int) = keys[index]

    override val size: Int
        get() = keys.size
}
