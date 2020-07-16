package test.jmh.map

import kotlin.random.Random

private var random = Random(42)
private var size = 0
private var keys: LongArray? = LongArray(size)

fun generateKeys(newSize: Int): LongArray {
    if (size != newSize) {
        size = newSize
        keys = null
        keys = LongArray(size) { random.nextLong() }
    }
    return keys!!
}
