package example.storages

import example.Storage

inline fun <reified T> createStorage() = when (T::class) {
    Long::class -> createLongStorage
    Int::class -> createIntStorage
    else -> TODO("NOT IMPLEMENTED")
} as (Int) -> Storage<T>

val createLongStorage = { capacity: Int -> LongStorage(capacity) }
val createIntStorage = { capacity: Int -> IntStorage(capacity) }
