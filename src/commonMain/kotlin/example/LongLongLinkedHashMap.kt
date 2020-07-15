package example

class LongLongLinkedHashMap(initialCapacity: Int, private val loadFactor: Float) : AbstractMutableMap<Long, Long>() {
    override var size: Int = 0
        private set

    /** Number of cells marked as deleted. */
    private var deletedNumber = 0

    private var capacity = roundToPowerOfTwo(initialCapacity)

    private var _keys = LongArray(this.capacity)
    private var _values = LongArray(this.capacity)
    private var links = Links(this.capacity)
    private var mask = this.capacity - 1
    private var maxDeletedNumber = capacity * loadFactor * DELETED_FACTOR
    private val currentLoadFactor: Double
        get() = (size + deletedNumber).toDouble() / capacity

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
            deletedNumber--
            check(deletedNumber >= 0)
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
        deletedNumber++
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

    private fun shouldRehash(): Boolean {
        if (deletedNumber >= maxDeletedNumber) return true
        if (currentLoadFactor >= loadFactor) return true
        return false
    }

    private fun checkRehash() {
        if (!shouldRehash()) return
        val map = LongLongLinkedHashMap(this, loadFactor = loadFactor)
        this.deletedNumber = map.deletedNumber
        this.capacity = map.capacity
        this.links = map.links
        this._keys = map._keys
        this._values = map._values
        this.mask = map.mask
        this.maxDeletedNumber = map.maxDeletedNumber
    }

    private inner class LongLongEntrySet : AbstractMutableSet<MutableMap.MutableEntry<Long, Long>>() {
        override val size: Int
            get() = this@LongLongLinkedHashMap.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            return LongLongIterator()
        }

        override fun add(element: MutableMap.MutableEntry<Long, Long>): Boolean = throw UnsupportedOperationException()

        override fun clear() = this@LongLongLinkedHashMap.clear()

        override fun contains(element: MutableMap.MutableEntry<Long, Long>): Boolean {
            val actualElement = element as Map.Entry<Any?, Any?>
            if (actualElement.key !is Long || actualElement.value !is Long) return false
            return this@LongLongLinkedHashMap[element.key] == element.value
        }

        override fun remove(element: MutableMap.MutableEntry<Long, Long>): Boolean {
            if (!contains(element)) return false
            check(this@LongLongLinkedHashMap.remove(element.key) == element.value)
            return true
        }

        /** Expected to throw [NullPointerException] if [elements] is null, but throws [IllegalArgumentException]. */
        override fun removeAll(elements: Collection<MutableMap.MutableEntry<Long, Long>>): Boolean =
            elements.fold(false) { modified, element -> modified or remove(element) }

        private inner class LongLongIterator : MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            val iterator = links.iterator()
            private var lastReturned: LongLongEntry? = null

            override fun hasNext() = iterator.hasNext()

            override fun next(): MutableMap.MutableEntry<Long, Long> {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val currentIndex = iterator.next()
                return LongLongEntry(_keys[currentIndex]).also {
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
private const val DELETED_FACTOR = 0.5

private fun chooseCapacityBySize(size: Int, loadFactor: Float): Int {
    return roundToPowerOfTwo((2.0 * size / loadFactor + 1).toInt())
}
