package example.storages

import example.Storage

class LongStorage(capacity: Int) : Storage<Long> {
    private val data = LongArray(capacity) { SPECIAL }
    private var containsSpecialValue = false
    private var specialValue = 0L

    override fun markSpecial(index: Int) = set(index, SPECIAL)
    override fun get(index: Int) = data[index]
    override fun set(index: Int, value: Long) {
        data[index] = value
    }

    override fun hashCodeByIndex(index: Int) = hashCode(data[index])
    override fun hashCode(value: Long) = ((value ushr 32) xor value).toInt()
    override fun equalsByIndex(index: Int, value: Long) = value == data[index]

    override fun swapValues(oldIndex: Int, newIndex: Int) {
        data[oldIndex] = data[newIndex]
            .also { data[newIndex] = data[oldIndex] }
    }

    override fun special() = SPECIAL
    override fun isSpecial(value: Long) = value == SPECIAL
    override fun isSpecialByIndex(index: Int) = isSpecial(data[index])

    override fun containsSpecial() = containsSpecialValue
    override fun getSpecialOrNull() = if (containsSpecialValue) specialValue else null
    override fun removeSpecial() {
        containsSpecialValue = false
    }

    override fun addSpecial(value: Long) {
        containsSpecialValue = true
        specialValue = value
    }

    override fun isSameType(value: Any?) = value is Long
}

private const val SPECIAL = 0L
