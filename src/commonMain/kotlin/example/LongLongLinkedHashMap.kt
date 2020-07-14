package example

class LongLongLinkedHashMap(initialCapacity: Int, private val loadFactor: Float) : AbstractMutableMap<Long, Long>() {
    override var size: Int = 0
        private set

    private var capacity = roundToPowerOfTwo(initialCapacity)

    private var _keys = LongArray(this.capacity)
    private var _values = LongArray(this.capacity)
    private var links = Links(this.capacity)
    private var mask = this.capacity - 1
    private val currentLoadFactor: Double
        get() = size.toDouble() / capacity

    init {
        require(initialCapacity > 0) { "Capacity must be positive." }
        require(0 < loadFactor && loadFactor < 1) { "Load factor is out of bounds (0, 1)." }
    }

    constructor() : this(DEFAULT_CAPACITY)
    constructor(initialCapacity: Int) : this(initialCapacity, DEFAULT_LOAD_FACTOR)
    constructor(original: Map<out Long, Long>) : this(original, DEFAULT_LOAD_FACTOR)
    constructor(original: Map<out Long, Long>, loadFactor: Float) :
            this(chooseCapacityBySize(original.size, loadFactor)) {
        putAll(original)
    }

    override fun containsKey(key: Long): Boolean {
        val index = findIndex(key)
        return links.isPresent(index)
    }

    override fun get(key: Long): Long? {
        val index = findIndex(key)
        if (!links.isPresent(index)) return null
        check(_keys[index] == key)
        return _values[index]
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Long, Long>> = LongLongEntrySet()

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
        if (currentLoadFactor < loadFactor) return
        val map = LongLongLinkedHashMap(this)
        this.capacity = map.capacity
        this.links = map.links
        this._keys = map._keys
        this._values = map._values
        this.mask = map.mask
    }

    private inner class LongLongEntrySet : AbstractMutableSet<MutableMap.MutableEntry<Long, Long>>() {
        override val size: Int
            get() = this@LongLongLinkedHashMap.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            return LongLongIterator()
        }

        override fun add(element: MutableMap.MutableEntry<Long, Long>): Boolean = throw UnsupportedOperationException()

        private inner class LongLongIterator : MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            private var currentIndex = links.head
            private var lastReturned: LongLongEntry? = null

            override fun hasNext(): Boolean {
                return currentIndex != NULL_LINK
            }

            override fun next(): MutableMap.MutableEntry<Long, Long> {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                return LongLongEntry(_keys[currentIndex]).also {
                    currentIndex = links.next(currentIndex)
                    lastReturned = it
                }
            }

            override fun remove() {
                val last = lastReturned
                check(last != null) { "Next method has not yet been called, or the remove method has already been called after the last call to the next method" }
                lastReturned = null
                this@LongLongLinkedHashMap.remove(last.key)
            }
        }
    }

    private inner class LongLongEntry(override val key: Long) : MutableMap.MutableEntry<Long, Long> {
        override val value: Long
            get() = this@LongLongLinkedHashMap[key]!!

        override fun setValue(newValue: Long): Long {
            return this@LongLongLinkedHashMap.put(key, value)!!
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || other !is Map.Entry<*, *>) return false
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.toInt() xor value.hashCode()

        override fun toString(): String = "$key=$value"
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

private const val DEFAULT_LOAD_FACTOR = 0.75f
private const val DEFAULT_CAPACITY = 8

private fun chooseCapacityBySize(size: Int, loadFactor: Float): Int {
    return roundToPowerOfTwo((2.0 * size / loadFactor + 1).toInt())
}
