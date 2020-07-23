package example

import example.storages.createStorage

inline fun <reified K, reified V> createLinkedHashMap(size: Int, loadFactor: Float) = when {
    K::class == Long::class && V::class == Long::class -> LongLongLinkedHashMap(size, loadFactor)
    K::class == Int::class && V::class == Int::class -> IntIntLinkedHashMap(size, loadFactor)
    else -> error("Types ${K::class}, ${V::class} are not implemented.")
} as MutableMap<K, V>


inline fun <reified K, reified V> createLinkedOpenHashMap(
    capacity: Int = 8,
    loadFactor: Float = 0.75f
): MutableMap<K, V> {
    val createKeys = createStorage<K>()
    val createValues = createStorage<V>()
    return LinkedOpenHashMap(createKeys, createValues, capacity, loadFactor)
}
