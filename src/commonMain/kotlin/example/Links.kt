package example

internal class Links(capacity: Int) {
    private val next: IntArray
    private val previous: IntArray
    var head = NULL_LINK
        private set
    var tail = NULL_LINK
        private set

    init {
        require(capacity > 0) { "Capacity must be positive." }
        next = IntArray(capacity) { FREE }
        previous = IntArray(capacity) { FREE }
    }

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

    private fun setFree(index: Int) {
        checkIndex(index)
        previous[index] = FREE
        next[index] = FREE
    }

    private fun setDeleted(index: Int) {
        checkIndex(index)
        previous[index] = DELETED
        next[index] = DELETED
    }

    private fun setLast(index: Int) {
        checkIndex(index)
        previous[index] = tail
        next[index] = NULL_LINK
        tail = index
    }

    fun add(index: Int) {
        checkIndex(index)
        if (tail != NULL_LINK) {
            setNext(tail, nextValue = index)
        } else {
            head = index
        }
        setLast(index)
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
        setDeleted(index)
    }

    private fun checkIndex(index: Int) {
        require(0 <= index && index < next.size) { "Index $index is out of bounds [0, ${next.size}]" }
    }

    fun isFree(index: Int): Boolean {
        val next = next(index)
        return next == FREE
    }

    fun isDeleted(index: Int): Boolean {
        val next = next(index)
        return next == DELETED
    }

    fun isPresent(index: Int): Boolean {
        val next = next(index)
        return next >= 0 || next == NULL_LINK
    }

    fun hasNext(index: Int): Boolean {
        val next = next(index)
        return next != NULL_LINK
    }

    fun hasPrevious(index: Int): Boolean {
        val prev = previous(index)
        return prev != NULL_LINK
    }

    fun clear() {
        var index = head
        while (index != NULL_LINK) {
            val nextIndex = next(index)
            setFree(index)
            index = nextIndex
        }
        head = NULL_LINK
        tail = NULL_LINK
    }
}

private const val FREE = -1
private const val DELETED = -2
private const val NULL_LINK = -3
