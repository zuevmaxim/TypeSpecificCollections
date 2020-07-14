package example

class LongLongLinkedHashMap(capacity: Int) : MutableMap<Long, Long> {
    override var size: Int = 0
        private set

    private var capacity = roundToPowerOfTwo(capacity)

    private var _keys = LongArray(this.capacity)
    private var _values = LongArray(this.capacity)
    private var links = Links(this.capacity, FREE, FREE)
    private var mask = this.capacity - 1
    private var head = DEFAULT_LINK
    private var tail = DEFAULT_LINK
    private val upperLoadFactor = DEFAULT_UPPER_LOAD_FACTOR
    private val lowerLoadFactor = DEFAULT_LOWER_LOAD_FACTOR
    private val currentLoadFactor: Double
        get() = size.toDouble() / capacity

    init {
        require(capacity > 0) { "Capacity must be positive." }
    }

    override fun containsKey(key: Long): Boolean {
        val index = findIndex(key)
        return isPresent(index)
    }

    override fun containsValue(value: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: Long): Long? {
        val index = findIndex(key)
        if (!isPresent(index)) return null
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
        var index = head
        while (index != LAST) {
            val nextIndex = links.next(index)
            links.set(index, previousValue = FREE, nextValue = FREE)
            index = nextIndex
        }
        head = DEFAULT_LINK
        tail = DEFAULT_LINK
        size = 0
        checkRehash()
    }

    override fun put(key: Long, value: Long): Long? {
        val index = findIndex(key)
        if (isPresent(index)) {
            check(_keys[index] == key)
            return _values[index]
                .also { _values[index] = value }
        }
        size++
        if (isDeleted(index)) {
            check(_keys[index] == key)
        }
        checkRehash()
        _keys[index] = key
        _values[index] = value
        links.set(index, previousValue = tail, nextValue = LAST)
        if (tail != DEFAULT_LINK) {
            links.set(tail, nextValue = index)
        } else {
            head = index
        }
        tail = index
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
        if (!isPresent(index)) return null
        check(_keys[index] == key)
        val value = _values[index]
        size--
        val next = links.next(index)
        val prev = links.previous(index)
        if (next == LAST) {
            tail = prev
        }
        if (prev == FIRST) {
            head = next
        }
        if (prev != FIRST) {
            links.set(prev, nextValue = next)
        }
        if (next != LAST) {
            links.set(next, previousValue = prev)
        }
        links.set(index, previousValue = DELETED, nextValue = DELETED)
        checkRehash()
        return value
    }

    private fun findIndex(key: Long): Int {
        val default = defaultIndex(key)
        var index = default
        while (!isFree(index) && _keys[index] != key) {
            index = nextIndex(index)
            check(index != default)
        }
        return index
    }

    private fun defaultIndex(key: Long): Int {
        return key.hashCode() and mask
    }

    private fun nextIndex(index: Int): Int {
        return (index + 1) and mask
    }

    private fun isFree(index: Int): Boolean {
        val next = links.next(index)
        return next == FREE
    }

    private fun isDeleted(index: Int): Boolean {
        val next = links.next(index)
        return next == DELETED
    }

    private fun isPresent(index: Int): Boolean {
        val next = links.next(index)
        return next >= 0 || next == LAST
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

private const val FREE = -1
private const val DELETED = -2
private const val DEFAULT_LINK = -3
private const val LAST = -3
private const val FIRST = -3
private const val DEFAULT_UPPER_LOAD_FACTOR = 0.75
private const val DEFAULT_LOWER_LOAD_FACTOR = 0.25
