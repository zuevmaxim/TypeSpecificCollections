package example

class IntIntLinkedHashMap(initialSize: Int = DEFAULT_CAPACITY, loadFactor: Float = DEFAULT_LOAD_FACTOR) :
    AbstractMutableMap<Int, Int>() {
    private val map = LongLongLinkedHashMap(initialSize, loadFactor)

    override val size: Int
        get() = map.size

    override fun get(key: Int): Int? = map[key.toLong()]?.toInt()
    override fun containsKey(key: Int): Boolean = map.containsKey(key.toLong())
    override fun put(key: Int, value: Int): Int? = map.put(key.toLong(), value.toLong())?.toInt()
    override fun remove(key: Int): Int? = map.remove(key.toLong())?.toInt()
    override fun clear() = map.clear()

    override val entries: MutableSet<MutableMap.MutableEntry<Int, Int>> = IntIntEntrySet()


    private inner class IntIntEntrySet : AbstractMutableSet<MutableMap.MutableEntry<Int, Int>>() {
        override val size: Int
            get() = this@IntIntLinkedHashMap.size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Int, Int>> = IntIntIterator()
        override fun add(element: MutableMap.MutableEntry<Int, Int>): Boolean = throw UnsupportedOperationException()
        override fun clear() = this@IntIntLinkedHashMap.clear()

        /** Expected to throw [NullPointerException] if [elements] is null, but throws [IllegalArgumentException]. */
        override fun removeAll(elements: Collection<MutableMap.MutableEntry<Int, Int>>): Boolean =
            elements.fold(false) { modified, element -> modified or remove(element) }

        private inner class IntIntIterator : MutableIterator<MutableMap.MutableEntry<Int, Int>> {
            val iterator = map.entries.iterator()
            override fun hasNext() = iterator.hasNext()
            override fun next() = IntIntEntry(iterator.next())
            override fun remove() = iterator.remove()
        }
    }

    private inner class IntIntEntry(private val entry: MutableMap.MutableEntry<Long, Long>) :
        MutableMap.MutableEntry<Int, Int> {
        override val key: Int
            get() = entry.key.toInt()
        override val value: Int
            get() = entry.value.toInt()

        override fun setValue(newValue: Int) = entry.setValue(newValue.toLong()).toInt()
        override fun equals(other: Any?) = entry == other
        override fun hashCode() = entry.hashCode()
        override fun toString() = entry.toString()
    }
}
