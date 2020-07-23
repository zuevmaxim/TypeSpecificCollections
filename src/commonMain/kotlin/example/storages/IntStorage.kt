package example.storages

import example.Storage

class IntStorage(capacity: Int) : Storage<Int> {
    private val data = IntArray(capacity) { SPECIAL }
    private var containsSpecialValue = false
    private var specialValue = 0

    override fun markSpecial(index: Int) = set(index, SPECIAL)
    override fun get(index: Int) = data[index]
    override fun set(index: Int, value: Int) {
        data[index] = value
    }

    override fun hashCodeByIndex(index: Int) = hashCode(data[index])
    override fun hashCode(value: Int) = value
    override fun equalsByIndex(index: Int, value: Int) = value == data[index]

    override fun swapValues(oldIndex: Int, newIndex: Int) {
        data[oldIndex] = data[newIndex]
            .also { data[newIndex] = data[oldIndex] }
    }

    override fun special() = SPECIAL
    override fun isSpecial(value: Int) = value == SPECIAL
    override fun isSpecialByIndex(index: Int) = isSpecial(data[index])

    override fun containsSpecial() = containsSpecialValue
    override fun getSpecialOrNull() = if (containsSpecialValue) specialValue else null
    override fun removeSpecial() {
        containsSpecialValue = false
    }

    override fun addSpecial(value: Int) {
        containsSpecialValue = true
        specialValue = value
    }

    override fun isSameType(value: Any?) = value is Int
}

private const val SPECIAL = 0
