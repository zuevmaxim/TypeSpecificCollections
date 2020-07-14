package example

class Links(capacity: Int, defaultPrev: Int, defaultNext: Int) {
    private val next: IntArray
    private val previous: IntArray

    init {
        require(capacity > 0) { "Capacity must be positive." }
        next = IntArray(capacity) { defaultNext }
        previous = IntArray(capacity) { defaultPrev }
    }

    fun next(index: Int): Int {
        checkIndex(index)
        return next[index]
    }

    fun previous(index: Int): Int {
        checkIndex(index)
        return previous[index]
    }

    fun set(index: Int, previousValue: Int = previous(index), nextValue: Int = next(index)) {
        checkIndex(index)
        previous[index] = previousValue
        next[index] = nextValue
    }

    private fun checkIndex(index: Int) {
        require(0 <= index && index < next.size) { "Index $index is out of bounds [0, ${next.size}]" }
    }
}
