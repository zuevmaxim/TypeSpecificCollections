package example

import kotlin.math.max

class LongLongLinkedHashMap(initialCapacity: Int, private val loadFactor: Float) : AbstractMutableMap<Long, Long>() {
    override val size: Int
        get() = _size + if (containsSpecialKey) 1 else 0

    private var _size: Int = 0

    private var capacity = roundToPowerOfTwo(max(initialCapacity, DEFAULT_CAPACITY))
    private var power = log2(capacity)

    private var _keys = LongArray(this.capacity) { SPECIAL_KEY }
    private var _values = LongArray(this.capacity)
    private var links = Links(capacity + 1) // +1 for special key
    private var mask = this.capacity - 1
    private val currentLoadFactor: Double
        get() = _size.toDouble() / capacity

    /** Value mapped to [SPECIAL_KEY]. */
    private var specialKeyValue: Long = 0
    private var containsSpecialKey = false

    init {
        require(initialCapacity > 0) { "Capacity must be positive." }
        require(0 < loadFactor && loadFactor < 1) { "Load factor is out of bounds (0, 1)." }
    }

    constructor() : this(DEFAULT_CAPACITY)
    constructor(initialCapacity: Int) : this(initialCapacity, DEFAULT_LOAD_FACTOR)
    constructor(original: Map<out Long, Long>) : this(original, DEFAULT_LOAD_FACTOR)
    private constructor(original: Map<out Long, Long>, loadFactor: Float, capacity: Int = original.size) :
            this(capacity, loadFactor) {
        putAll(original)
    }

    override fun containsKey(key: Long): Boolean {
        if (isSpecialKey(key)) return containsSpecialKey
        val index = findIndex(key)
        return isPresent(index)
    }

    override fun get(key: Long): Long? {
        if (isSpecialKey(key)) {
            return specialKeyValueOrNull()
        }
        val index = findIndex(key)
        if (!isPresent(index)) return null
        check(_keys[index] == key)
        return _values[index]
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Long, Long>> = LongLongEntrySet()

    override fun clear() {
        val it = links.fastIterator()
        while (it.hasNext()) {
            val index = it.next()
            if (index == capacity) continue
            _keys[index] = SPECIAL_KEY
        }
        containsSpecialKey = false
        links.clear()
        _size = 0
        checkRehash()
    }

    override fun put(key: Long, value: Long): Long? {
        if (isSpecialKey(key)) {
            return specialKeyValueOrNull().also {
                if (it == null) {
                    links.add(capacity)
                }
                containsSpecialKey = true
                specialKeyValue = value
            }
        }
        val index = findIndex(key)
        if (isPresent(index)) {
            check(_keys[index] == key)
            return _values[index]
                .also { _values[index] = value }
        }
        _size++
        _keys[index] = key
        _values[index] = value
        links.add(index)
        checkRehash()
        return null
    }

    override fun remove(key: Long): Long? {
        if (isSpecialKey(key)) {
            return specialKeyValueOrNull().also {
                if (it != null) {
                    links.remove(capacity)
                }
                containsSpecialKey = false
            }
        }
        val index = findIndex(key)
        if (!isPresent(index)) return null
        check(_keys[index] == key)
        val value = _values[index]
        _keys[index] = SPECIAL_KEY
        _size--
        links.remove(index)
        shiftKeys(index)
        checkRehash()
        return value
    }

    private fun specialKeyValueOrNull() = if (containsSpecialKey) specialKeyValue else null

    private fun isSpecialKey(key: Long) = key == SPECIAL_KEY
    private fun isPresent(index: Int) = !isSpecialKey(_keys[index])
    private fun isFree(index: Int) = !isPresent(index)

    private fun findIndex(key: Long): Int {
        val default = defaultIndex(key)
        var index = default
        while (!isFree(index) && _keys[index] != key) {
            index = nextIndex(index)
            check(index != default) { "Search cycle occurred. Rehash should be done." }
        }
        return index
    }

    private fun hash(x: Int): Int {
        return (x * PHI) ushr (32 - power)
    }

    private fun longHash(key: Long): Int = ((key ushr 32) xor key).toInt()

    private fun defaultIndex(key: Long): Int {
        return hash(longHash(key))
    }

    private fun nextIndex(index: Int): Int {
        return (index + 1) and mask
    }

    private fun shouldRehash() = currentLoadFactor >= loadFactor

    private fun checkRehash() {
        if (!shouldRehash()) return
        val map = LongLongLinkedHashMap(this, loadFactor = loadFactor, capacity = 2 * capacity)
        this.capacity = map.capacity
        this.power = map.power
        this.links = map.links
        this._keys = map._keys
        this._values = map._values
        this.mask = map.mask
    }

    /** Relies on linear [nextIndex]. */
    private tailrec fun shiftKeys(index: Int) {
        fun isIndependent(newIndex: Int): Boolean {
            val default = defaultIndex(_keys[newIndex])
            return if (newIndex >= index) {
                default in (index + 1)..newIndex
            } else {
                default !in (newIndex + 1)..index
            }
        }
        check(isFree(index))
        var currIndex = nextIndex(index)
        while (!isFree(currIndex) && isIndependent(currIndex)) {
            check(currIndex != index)
            currIndex = nextIndex(currIndex)
        }
        if (!isFree(currIndex)) {
            _keys[index] = _keys[currIndex]
            _values[index] = _values[currIndex]
            _keys[currIndex] = SPECIAL_KEY
            links.move(currIndex, index)
            shiftKeys(currIndex)
        }
    }

    private fun keyByIndex(index: Int) = if (index == capacity) {
        SPECIAL_KEY
    } else {
        _keys[index]
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
            val iterator = links.fastIterator()
            private var lastReturned: LongLongEntry? = null

            override fun hasNext() = iterator.hasNext()

            override fun next(): MutableMap.MutableEntry<Long, Long> {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val currentIndex = iterator.next()
                return LongLongEntry(keyByIndex(currentIndex)).also {
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

        override fun hashCode(): Int = key.hashCode() xor value.hashCode()

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

private fun log2(x: Int): Int {
    require(x > 0)
    var result = 1
    var power = 0
    while (result < x) {
        power++
        result *= 2
    }
    return power
}

private const val SPECIAL_KEY = 0L
private const val PHI = -1640531527

private fun chooseCapacityBySize(size: Int, loadFactor: Float): Int {
    return 2 * roundToPowerOfTwo(max(size / loadFactor.toDouble(), 1.0).toInt())
}
