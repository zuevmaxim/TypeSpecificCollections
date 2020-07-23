package example

import kotlin.math.max

class LinkedOpenHashMap<K, V>(
    private val createKeys: (Int) -> Storage<K>,
    private val createValues: (Int) -> Storage<V>,
    initialSize: Int = DEFAULT_CAPACITY,
    private val loadFactor: Float = DEFAULT_LOAD_FACTOR
) : AbstractMutableMap<K, V>() {
    private var _size: Int = 0
    private var capacity: Int
    private var power: Int
    private var mask: Int
    private var _keys: Storage<K>
    private var _values: Storage<V>
    private var links: Links

    override val size: Int
        get() = _size + if (_values.containsSpecial()) 1 else 0

    private val currentLoadFactor: Double
        get() = _size.toDouble() / capacity

    init {
        require(initialSize > 0) { "Capacity must be positive." }
        require(0 < loadFactor && loadFactor < 1) { "Load factor is out of bounds (0, 1)." }
        val roundResult = chooseCapacityBySize(initialSize, loadFactor)
        capacity = roundResult.first
        power = roundResult.second
        mask = capacity - 1
        _keys = createKeys(capacity)
        _values = createValues(capacity)
        links = Links(capacity + 1) // +1 for special key
    }

    private constructor(original: LinkedOpenHashMap<K, V>) :
            this(original.createKeys, original.createValues, original.size, original.loadFactor) {
        for (index in original.links) {
            if (index == original.capacity) {
                _values.addSpecial(original._values.getSpecialOrNull()!!)
                links.add(capacity)
                continue
            }
            val key = original._keys.get(index)
            val value = original._values.get(index)
            val newIndex = findIndex(key)
            _size++
            _keys.set(newIndex, key)
            _values.set(newIndex, value)
            links.add(newIndex)
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = LinkedOpenEntrySet()

    override fun containsKey(key: K): Boolean {
        if (_keys.isSpecial(key)) return _values.containsSpecial()
        val index = findIndex(key)
        return isPresent(index)
    }

    override fun get(key: K): V? {
        if (_keys.isSpecial(key)) return _values.getSpecialOrNull()
        val index = findIndex(key)
        if (!isPresent(index)) return null
        check(_keys.equalsByIndex(index, key))
        return _values.get(index)
    }

    override fun clear() {
        for (index in links) {
            if (index != capacity) {
                _keys.markSpecial(index)
            }
        }
        _values.removeSpecial()
        links.clear()
        _size = 0
        checkRehash()
    }

    override fun put(key: K, value: V): V? {
        if (_keys.isSpecial(key)) {
            return _values.getSpecialOrNull().also {
                if (it == null) {
                    links.add(capacity)
                }
                _values.addSpecial(value)
            }
        }
        val index = findIndex(key)
        if (isPresent(index)) {
            check(_keys.equalsByIndex(index, key))
            return _values.get(index)
                .also { _values.set(index, value) }
        }
        _size++
        _keys.set(index, key)
        _values.set(index, value)
        links.add(index)
        checkRehash()
        return null
    }

    override fun remove(key: K): V? {
        if (_keys.isSpecial(key)) {
            return _values.getSpecialOrNull().also {
                if (it != null) {
                    links.remove(capacity)
                }
                _values.removeSpecial()
            }
        }
        val index = findIndex(key)
        if (!isPresent(index)) return null
        check(_keys.equalsByIndex(index, key))
        val value = _values.get(index)
        _keys.markSpecial(index)
        _size--
        links.remove(index)
        shiftKeys(index)
        checkRehash()
        return value
    }

    private fun isPresent(index: Int) = !isFree(index)
    private fun isFree(index: Int) = _keys.isSpecialByIndex(index)

    private fun findIndex(key: K): Int {
        val default = defaultIndex(key)
        var index = default
        while (!isFree(index) && !_keys.equalsByIndex(index, key)) {
            index = nextIndex(index)
            check(index != default) { "Search cycle occurred. Rehash should be done." }
        }
        return index
    }

    private fun hash(x: Int): Int {
        return ((x * PHI) ushr (32 - power)).toInt()
    }

    private fun defaultIndex(key: K) = defaultIndex(_keys.hashCode(key))
    private fun defaultIndex(hashCode: Int) = hash(hashCode) and mask

    private fun nextIndex(index: Int): Int {
        return (index + 1) and mask
    }

    private fun shouldRehash() = currentLoadFactor >= loadFactor

    private fun checkRehash() {
        if (!shouldRehash()) return
        val map = LinkedOpenHashMap(this)
        this.capacity = map.capacity
        this.power = map.power
        this.mask = map.mask
        this._keys = map._keys
        this._values = map._values
        this.links = map.links
    }

    /** Relies on linear [nextIndex]. */
    private tailrec fun shiftKeys(index: Int) {
        fun isIndependent(newIndex: Int): Boolean {
            val default = defaultIndex(_keys.hashCodeByIndex(newIndex))
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
            _keys.swapValues(oldIndex = currIndex, newIndex = index)
            _values.swapValues(oldIndex = currIndex, newIndex = index)
            links.move(currIndex, index)
            shiftKeys(currIndex)
        }
    }

    private fun keyByIndex(index: Int) = if (index == capacity) {
        _keys.special()
    } else {
        _keys.get(index)
    }

    private inner class LinkedOpenEntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        override val size: Int
            get() = this@LinkedOpenHashMap.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = LinkedOpenIterator()

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean = throw UnsupportedOperationException()

        override fun clear() = this@LinkedOpenHashMap.clear()

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
            val actualElement = element as Map.Entry<Any?, Any?>
            if (!_keys.isSameType(actualElement.key) || !_values.isSameType(actualElement.value)) return false
            return this@LinkedOpenHashMap[element.key] == element.value
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            if (!contains(element)) return false
            check(this@LinkedOpenHashMap.remove(element.key) == element.value)
            return true
        }

        /** Expected to throw [NullPointerException] if [elements] is null, but throws [IllegalArgumentException]. */
        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            elements.fold(false) { modified, element -> modified or remove(element) }

        private inner class LinkedOpenIterator : MutableIterator<MutableMap.MutableEntry<K, V>> {
            val iterator = links.iterator()
            private var lastReturned: LinkedOpenEntry? = null

            override fun hasNext() = iterator.hasNext()

            override fun next(): MutableMap.MutableEntry<K, V> {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val currentIndex = iterator.next()
                val key = keyByIndex(currentIndex)
                return LinkedOpenEntry(key).also {
                    lastReturned = it
                }
            }

            override fun remove() {
                val last = lastReturned
                check(last != null) { "Next method has not yet been called, or the remove method has already been called after the last call to the next method" }
                lastReturned = null
                this@LinkedOpenHashMap.remove(last.key)
            }
        }
    }

    private inner class LinkedOpenEntry(override val key: K) : MutableMap.MutableEntry<K, V> {
        override val value: V
            get() = this@LinkedOpenHashMap[key]!!

        override fun setValue(newValue: V): V {
            return this@LinkedOpenHashMap.put(key, value)!!
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

private fun roundToPowerOfTwo(x: Int): Pair<Int, Int> {
    require(x > 0)
    var result = 1
    var power = 0
    while (result < x) {
        result *= 2
        power++
    }
    return result to power
}

private const val DEFAULT_LOAD_FACTOR = 0.75f
private const val DEFAULT_CAPACITY = 8
private const val PHI = 2654435761

private fun chooseCapacityBySize(size: Int, loadFactor: Float): Pair<Int, Int> {
    val (capacity, power) = roundToPowerOfTwo(max(size / loadFactor.toDouble(), 1.0).toInt())
    return 2 * capacity to power + 1
}
