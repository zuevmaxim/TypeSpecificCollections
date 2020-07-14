package example

class LongLongLinkedHashMap(capacity: Int) : MutableMap<Long, Long> {
    override var size: Int = 0
        private set

    private var capacity = roundToPowerOfTwo(capacity)

    private var _keys = LongArray(this.capacity)
    private var _values = LongArray(this.capacity)
    private var links = Links(this.capacity)
    private var mask = this.capacity - 1
    private val upperLoadFactor = DEFAULT_UPPER_LOAD_FACTOR
    private val lowerLoadFactor = DEFAULT_LOWER_LOAD_FACTOR
    private val currentLoadFactor: Double
        get() = size.toDouble() / capacity

    init {
        require(capacity > 0) { "Capacity must be positive." }
    }

    override fun containsKey(key: Long): Boolean {
        val index = findIndex(key)
        return links.isPresent(index)
    }

    override fun containsValue(value: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: Long): Long? {
        val index = findIndex(key)
        if (!links.isPresent(index)) return null
        check(_keys[index] == key)
        return _values[index]
    }

    override fun isEmpty(): Boolean = size == 0

    override val entries: MutableSet<MutableMap.MutableEntry<Long, Long>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<Long>
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<Long>
        get() = TODO("Not yet implemented")

    override fun clear() {
        links.clear()
        size = 0
        checkRehash()
    }

    override fun put(key: Long, value: Long): Long? {
        val index = findIndex(key)
        if (links.isPresent(index)) {
            check(_keys[index] == key)
            return _values[index]
                .also { _values[index] = value }
        }
        size++
        if (links.isDeleted(index)) {
            check(_keys[index] == key)
        }
        _keys[index] = key
        _values[index] = value
        links.add(index)
        checkRehash()
        return null
    }

    override fun putAll(from: Map<out Long, Long>) {
        // TODO check for rehash before
        for ((key, value) in from) {
            put(key, value)
        }
    }

    override fun remove(key: Long): Long? {
        val index = findIndex(key)
        if (!links.isPresent(index)) return null
        check(_keys[index] == key)
        val value = _values[index]
        size--
        links.remove(index)
        checkRehash()
        return value
    }

    private fun findIndex(key: Long): Int {
        val default = defaultIndex(key)
        var index = default
        while (!links.isFree(index) && _keys[index] != key) {
            index = nextIndex(index)
            check(index != default) { "Search cycle occurred. Rehash should be done." }
        }
        return index
    }

    private fun defaultIndex(key: Long): Int {
        return key.hashCode() and mask
    }

    private fun nextIndex(index: Int): Int {
        return (index + 1) and mask
    }

    private fun checkRehash() {

    }

}

private fun roundToPowerOfTwo(x: Int): Int {
    require(x > 0)
    var result = 1
    while (result < x) {
        result *= 2
    }
    return result
}

private const val DEFAULT_UPPER_LOAD_FACTOR = 0.75
private const val DEFAULT_LOWER_LOAD_FACTOR = 0.25
