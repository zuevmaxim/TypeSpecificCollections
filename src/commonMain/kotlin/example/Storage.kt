package example

interface Storage<T> {
    fun get(index: Int): T
    fun set(index: Int, value: T)
    fun swapValues(oldIndex: Int, newIndex: Int)

    fun hashCodeByIndex(index: Int): Int
    fun hashCode(value: T): Int
    fun equalsByIndex(index: Int, value: T): Boolean

    fun isSpecial(value: T): Boolean
    fun isSpecialByIndex(index: Int): Boolean
    fun containsSpecial(): Boolean
    fun getSpecialOrNull(): T?
    fun markSpecial(index: Int)
    fun removeSpecial()
    fun addSpecial(value: T)
    fun special(): T

    fun isSameType(value: Any?): Boolean
}
