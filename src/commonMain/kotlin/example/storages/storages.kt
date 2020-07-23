package example.storages

import example.Storage

inline fun <reified T> createStorage(): (Int) -> Storage<T> = when (T::class) {
    Long::class -> createLongStorage
    else -> TODO("NOT IMPLEMENTED")
} as (Int) -> Storage<T>

val createLongStorage = { capacity: Int -> LongStorage(capacity) }
