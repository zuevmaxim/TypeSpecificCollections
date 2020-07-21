package example

internal class Links(private val capacity: Int) : Iterable<Int> {
    private val links = LongArray(capacity)
    private var head = NULL_LINK
    private var tail = NULL_LINK

    init {
        require(capacity > 0) { "Capacity must be positive." }
    }

    fun add(index: Int) {
        checkIndex(index)
        setPrevNext(index, tail, NULL_LINK)
        if (tail != NULL_LINK) {
            setNext(tail, index)
        } else {
            head = index
        }
        tail = index
    }

    fun remove(index: Int) {
        checkIndex(index)
        val next = next(index)
        val prev = previous(index)
        if (head == index) {
            head = next
        }
        if (tail == index) {
            tail = prev
        }
        if (next != NULL_LINK) {
            setPrevious(next, prev)
        }
        if (prev != NULL_LINK) {
            setNext(prev, next)
        }
    }

    fun clear() {
        head = NULL_LINK
        tail = NULL_LINK
    }

    fun move(oldIndex: Int, newIndex: Int) {
        val prev = previous(oldIndex)
        val next = next(oldIndex)
        setPrevNext(newIndex, prev, next)
        if (prev != NULL_LINK) {
            setNext(prev, newIndex)
        } else {
            head = newIndex
        }
        if (next != NULL_LINK) {
            setPrevious(next, newIndex)
        } else {
            tail = newIndex
        }
    }

    override fun iterator(): Iterator<Int> = LinksIterator()

    private fun next(index: Int): Int {
        checkIndex(index)
        return (links[index] ushr SHIFT).toInt()
    }

    private fun previous(index: Int): Int {
        checkIndex(index)
        return links[index].toInt()
    }

    private fun setPrevious(index: Int, prev: Int) {
        checkIndex(index)
        setPrevNext(index, prev, next(index))
    }

    private fun setNext(index: Int, next: Int) {
        checkIndex(index)
        links[index] = (links[index] and MASK) or (next.toLong() shl SHIFT)
    }

    private fun setPrevNext(index: Int, prev: Int, next: Int) {
        links[index] = (next.toLong() shl SHIFT) or (prev.toLong() and MASK)
    }

    private fun checkIndex(index: Int) {
        require(index in 0 until capacity) { "Index $index is out of bounds [0, $capacity]" }
    }

    private inner class LinksIterator : Iterator<Int> {
        private var current = head

        override fun hasNext() = current != NULL_LINK

        override fun next() = current.also {
            current = next(current)
        }
    }
}

private const val NULL_LINK = -1
private const val MASK = 0xffffffffL
private const val SHIFT = 32
