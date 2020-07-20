package example

internal class Links(capacity: Int) : Iterable<Int> {
    private val next: IntArray
    private val previous: IntArray
    private var head = NULL_LINK
    private var tail = NULL_LINK

    init {
        require(capacity > 0) { "Capacity must be positive." }
        next = IntArray(capacity)
        previous = IntArray(capacity)
    }

    fun add(index: Int) {
        checkIndex(index)
        if (tail != NULL_LINK) {
            setNext(tail, nextValue = index)
        } else {
            head = index
        }
        previous[index] = tail
        next[index] = NULL_LINK
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

    override fun iterator(): Iterator<Int> = LinksIterator()

    private fun next(index: Int): Int {
        checkIndex(index)
        return next[index]
    }

    private fun previous(index: Int): Int {
        checkIndex(index)
        return previous[index]
    }

    private fun setNext(index: Int, nextValue: Int) {
        checkIndex(index)
        next[index] = nextValue
    }

    private fun setPrevious(index: Int, previousValue: Int) {
        checkIndex(index)
        previous[index] = previousValue
    }

    private fun checkIndex(index: Int) {
        require(0 <= index && index < next.size) { "Index $index is out of bounds [0, ${next.size}]" }
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
