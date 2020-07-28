package test.jmh.map

import kotlin.random.Random

private var random = Random(42)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> generateStorage(capacity: Int) = when (T::class) {
    Long::class -> LongStorage(generateLongKeys(capacity))
    Int::class -> IntStorage(generateIntKeys(capacity))
    else -> error("Type is not implemented.")
} as Storage<T>

private var longKeys: LongArray? = null
fun generateLongKeys(newSize: Int): LongArray {
    if (longKeys == null || longKeys!!.size != newSize) {
        longKeys = null
        longKeys = LongArray(newSize) { random.nextLong() }
    }
    return longKeys!!
}

private var intKeys: IntArray? = null
fun generateIntKeys(newSize: Int): IntArray {
    if (intKeys == null || intKeys!!.size != newSize) {
        intKeys = null
        intKeys = IntArray(newSize) { random.nextInt() }
    }
    return intKeys!!
}
