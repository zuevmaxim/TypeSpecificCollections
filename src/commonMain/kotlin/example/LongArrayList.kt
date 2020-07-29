package example

class LongArrayList(private var capacity: Int = LIST_DEFAULT_CAPACITY) : AbstractMutableList<Long>() {
    private var data = LongArray(capacity)
    override var size = 0
        private set

    override fun get(index: Int): Long {
        checkIndex(index)
        return data[index]
    }

    override fun add(index: Int, element: Long) {
        checkIndexInclusive(index)
        moveRight(index, 1)
        size++
        data[index] = element
    }

    override fun addAll(index: Int, elements: Collection<Long>): Boolean {
        checkIndexInclusive(index)
        (elements as Collection<Long?>).forEach { it!! }
        moveRight(index, elements.size)
        size += elements.size
        elements.forEachIndexed { i, value ->
            data[i + index] = value
        }
        return elements.isNotEmpty()
    }

    override fun clear() {
        size = 0
    }

    override fun removeAt(index: Int) = get(index).also {
        moveLeft(index + 1, 1)
        size--
    }

    override fun set(index: Int, element: Long) = get(index).also {
        data[index] = element
    }

    private fun resize(expectedSize: Int = capacity) {
        val list = LongArrayList(expectedSize * 2)
        data.copyInto(list.data)
        data = list.data
        capacity = list.capacity
    }

    private fun checkResize(new: Int = 0) {
        if (size + new >= capacity) {
            resize(size + new)
        }
    }

    private fun moveRight(index: Int, step: Int) {
        checkResize(step)
        data.copyInto(data, index + step, index, size)
    }

    private fun moveLeft(index: Int, step: Int) {
        checkResize(step)
        data.copyInto(data, index - step, index, size)
    }

    private fun checkIndex(index: Int) {
        if (index !in 0 until size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds [0, $size].")
        }
    }

    private fun checkIndexInclusive(index: Int) {
        if (index !in 0..size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds [0, $size].")
        }
    }
}

private const val LIST_DEFAULT_CAPACITY = 1
