package example

import kotlin.math.max

class LongLongChainedLinkedHashMap(
    initialSize: Int = DEFAULT_CAPACITY,
    private val loadFactor: Float = DEFAULT_LOAD_FACTOR
) : AbstractMutableMap<Long, Long>() {
    override var size = 0
        private set

    private var capacity: Int
    private var power: Int
    private var mask: Int
    private var dataCapacity: Int

    private var pointers: IntArray
    private var _keys: LongArray
    private var _values: LongArray
    private var links: Links
    private var next: IntArray
    private var freeLinks: FreeLinks
    private var modificationCount = 0

    init {
        require(initialSize > 0) { "Capacity must be positive." }
        require(0 < loadFactor && loadFactor < 1) { "Load factor is out of bounds (0, 1)." }
        val roundResult = roundToPowerOfTwo(initialSize)
        capacity = roundResult.first
        power = roundResult.second
        mask = capacity - 1
        dataCapacity = max(1, (capacity * loadFactor).toInt())

        pointers = IntArray(capacity) { NULL_LINK }
        _keys = LongArray(dataCapacity)
        _values = LongArray(dataCapacity)
        links = Links(dataCapacity)
        next = IntArray(dataCapacity)
        freeLinks = FreeLinks(dataCapacity)
    }

    private constructor(original: Map<out Long, Long>, capacity: Int, loadFactor: Float) :
            this(capacity, loadFactor) {
        putAll(original)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Long, Long>> = LongLongEntrySet()

    override fun containsKey(key: Long) = findIndexOrNull(key) != NULL_LINK

    override fun get(key: Long): Long? {
        val dataIndex = findIndexOrNull(key)
        return if (dataIndex == NULL_LINK) null else _values[dataIndex]
    }

    override fun put(key: Long, value: Long): Long? {
        val pointerIndex = defaultIndex(key)
        var dataIndex = pointers[pointerIndex]
        if (dataIndex == NULL_LINK) {
            val newDataIndex = createNewNode(key, value)
            pointers[pointerIndex] = newDataIndex
            checkRehash()
            modificationCount++
            return null
        } else {
            var lastIndex: Int
            do {
                if (_keys[dataIndex] == key) return replaceValue(dataIndex, value)
                lastIndex = dataIndex
                dataIndex = next[dataIndex]
            } while (dataIndex != NULL_LINK)
            val newIndex = createNewNode(key, value)
            next[lastIndex] = newIndex
            checkRehash()
            modificationCount++
            return null
        }
    }

    override fun remove(key: Long): Long? {
        val pointerIndex = defaultIndex(key)
        val headIndex = pointers[pointerIndex]
        var foundIndex = NULL_LINK
        var prevIndex = NULL_LINK

        var dataIndex = headIndex
        var currPrevIndex = NULL_LINK
        while (dataIndex != NULL_LINK) {
            if (_keys[dataIndex] == key) {
                foundIndex = dataIndex
                prevIndex = currPrevIndex
            }
            currPrevIndex = dataIndex
            dataIndex = next[dataIndex]
        }

        if (foundIndex == NULL_LINK) return null
        if (prevIndex == NULL_LINK) {
            pointers[pointerIndex] = next[foundIndex]
        } else {
            next[prevIndex] = next[foundIndex]
        }
        freeLinks.release(foundIndex)
        links.remove(foundIndex)
        size--
        modificationCount++
        return _values[foundIndex]
    }

    override fun clear() {
        modificationCount++
        val map = LongLongChainedLinkedHashMap(loadFactor = loadFactor)
        swap(map)
    }

    private fun longHash(key: Long): Int = ((key ushr 32) xor key).toInt()
    private fun hash(x: Int) = (x * PHI) ushr (32 - power)
    private fun defaultIndex(key: Long) = hash(longHash(key))

    private fun replaceValue(dataIndex: Int, value: Long) = _values[dataIndex].also {
        _values[dataIndex] = value
    }

    /** Returns index in data arrays where node is stored. */
    private fun createNewNode(key: Long, value: Long): Int {
        size++
        val dataIndex = freeLinks.pull()
        _keys[dataIndex] = key
        _values[dataIndex] = value
        links.add(dataIndex)
        next[dataIndex] = NULL_LINK
        return dataIndex
    }

    private fun findIndexOrNull(key: Long): Int {
        val pointerIndex = defaultIndex(key)
        if (pointerIndex == NULL_LINK) return NULL_LINK
        var dataIndex = pointers[pointerIndex]
        while (dataIndex != NULL_LINK) {
            if (_keys[dataIndex] == key) return dataIndex
            dataIndex = next[dataIndex]
        }
        return NULL_LINK
    }

    private fun shouldRehash() = size + 1 >= dataCapacity
    private fun rehash() {
        val map = LongLongChainedLinkedHashMap(this, capacity * 2, loadFactor)
        swap(map)
    }

    private fun checkRehash() {
        if (shouldRehash()) {
            rehash()
        }
    }

    private fun swap(map: LongLongChainedLinkedHashMap) {
        this.size = map.size
        this.capacity = map.capacity
        this.power = map.power
        this.mask = map.mask
        this.dataCapacity = map.dataCapacity
        this.pointers = map.pointers
        this._keys = map._keys
        this._values = map._values
        this.links = map.links
        this.next = map.next
        this.freeLinks = map.freeLinks
    }


    private inner class LongLongEntrySet : AbstractMutableSet<MutableMap.MutableEntry<Long, Long>>() {
        override val size: Int
            get() = this@LongLongChainedLinkedHashMap.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            return LongLongIterator()
        }

        override fun add(element: MutableMap.MutableEntry<Long, Long>): Boolean = throw UnsupportedOperationException()

        override fun clear() = this@LongLongChainedLinkedHashMap.clear()

        override fun contains(element: MutableMap.MutableEntry<Long, Long>): Boolean {
            val actualElement = element as Map.Entry<Any?, Any?>
            if (actualElement.key !is Long || actualElement.value !is Long) return false
            return this@LongLongChainedLinkedHashMap[element.key] == element.value
        }

        override fun remove(element: MutableMap.MutableEntry<Long, Long>): Boolean {
            if (!contains(element)) return false
            check(this@LongLongChainedLinkedHashMap.remove(element.key) == element.value)
            return true
        }

        /** Expected to throw [NullPointerException] if [elements] is null, but throws [IllegalArgumentException]. */
        override fun removeAll(elements: Collection<MutableMap.MutableEntry<Long, Long>>): Boolean =
            elements.fold(false) { modified, element -> modified or remove(element) }

        private inner class LongLongIterator : MutableIterator<MutableMap.MutableEntry<Long, Long>> {
            private var localModificationCount = modificationCount
            val iterator = links.fastIterator()
            private var lastReturned: LongLongEntry? = null

            override fun hasNext() = iterator.hasNext()

            override fun next(): MutableMap.MutableEntry<Long, Long> {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                checkModification()
                val currentIndex = iterator.next()
                return LongLongEntry(currentIndex).also {
                    lastReturned = it
                }
            }

            override fun remove() {
                val last = lastReturned
                check(last != null) { "Next method has not yet been called, or the remove method has already been called after the last call to the next method" }
                lastReturned = null
                this@LongLongChainedLinkedHashMap.remove(last.key)
                localModificationCount = modificationCount
            }

            private fun checkModification() {
                if (localModificationCount != modificationCount) {
                    throw ConcurrentModificationException()
                }
            }
        }
    }

    private inner class LongLongEntry(private val index: Int) : MutableMap.MutableEntry<Long, Long> {
        override val key: Long get() = _keys[index]
        override val value: Long get() = _values[index]

        override fun setValue(newValue: Long): Long {
            return this@LongLongChainedLinkedHashMap.put(key, value)!!
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

private const val NULL_LINK = -3
private const val PHI = -1640531527

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
