package example

class FreeLinks(private val capacity: Int) {
    private var head = 0
    private val next = IntArray(capacity) { it + 1 }

    fun pull(): Int = head.also {
        check(head < capacity)
        head = next[head]
    }

    fun release(index: Int) {
        next[index] = head
        head = index
    }
}
