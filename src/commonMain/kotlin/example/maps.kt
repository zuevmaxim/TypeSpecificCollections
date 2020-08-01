package example

import example.storages.createStorage

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> createLinkedHashMap() = when {
    K::class == Long::class && V::class == Long::class -> LongLongLinkedHashMap()
    K::class == Int::class && V::class == Int::class -> IntIntLinkedHashMap()
    else -> error("Types ${K::class}, ${V::class} are not implemented.")
} as MutableMap<K, V>


inline fun <reified K, reified V> createLinkedOpenHashMap(
    capacity: Int = DEFAULT_CAPACITY,
    loadFactor: Float = DEFAULT_LOAD_FACTOR
): MutableMap<K, V> {
    val createKeys = createStorage<K>()
    val createValues = createStorage<V>()
    return LinkedOpenHashMap(createKeys, createValues, capacity, loadFactor)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> createChainedLinkedHashMap() = when {
    K::class == Long::class && V::class == Long::class -> LongLongChainedLinkedHashMap()
    else -> error("Types ${K::class}, ${V::class} are not implemented.")
} as MutableMap<K, V>
