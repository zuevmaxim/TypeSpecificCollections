package example.storages

import example.Storage

class ObjectStorage<T>(capacity: Int) : Storage<T> {
    private val data = Array<Any>(capacity) { SPECIAL }
    private var specialValue: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int) = data[index] as T
    override fun set(index: Int, value: T) {
        data[index] = value as Any
    }

    override fun swapValues(oldIndex: Int, newIndex: Int) {
        data[oldIndex] = data[newIndex]
            .also { data[newIndex] = data[oldIndex] }
    }

    override fun hashCodeByIndex(index: Int) = data[index].hashCode()
    override fun hashCode(value: T) = value.hashCode()
    override fun equalsByIndex(index: Int, value: T) = data[index] == value

    override fun isSpecial(value: T) = value == SPECIAL
    override fun isSpecialByIndex(index: Int) = data[index] == SPECIAL
    override fun containsSpecial() = specialValue != null
    override fun getSpecialOrNull() = specialValue
    override fun markSpecial(index: Int) {
        data[index] = SPECIAL
    }

    override fun removeSpecial() {
        specialValue = null
    }

    override fun addSpecial(value: T) {
        specialValue = value
    }

    override fun special(): T = error("Should not be needed. SPECIAL value is not equals to any other object.")

    override fun isSameType(value: Any?): Boolean {
        TODO("Not yet implemented")
    }
}

private val SPECIAL = Any()
