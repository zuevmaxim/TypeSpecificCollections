package test.jmh.map

import example.*
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
import org.openjdk.jmh.annotations.*
import kotlin.random.Random

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
open class JVMMap {

    @Param(
        "10", "31", "100", "316", "1000", "3162",
        "10000", "31623", "100000", "316228", "1000000", "3162278", "10000000"
    )
    open var size = 0

    open lateinit var keys: LongArray

    open var index = 0

    open val stdMap = linkedMapOf<Long, Long>()
    open val myMap = createLinkedHashMap<Long, Long>()
    open val myGenericMap = createLinkedOpenHashMap<Long, Long>()
    open val chainedMap = createChainedLinkedHashMap<Long, Long>()
    open val fastUtilMap: MutableMap<Long, Long> = Long2LongLinkedOpenHashMap(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR)

    @Benchmark
    fun std() = stdMap[keys[increment()]]

    @Benchmark
    fun myMap() = myMap[keys[increment()]]

    @Benchmark
    fun generic() = myGenericMap[keys[increment()]]

    @Benchmark
    fun chained() = chainedMap[keys[increment()]]

    @Benchmark
    fun fastUtil() = fastUtilMap[keys[increment()]]

    @Setup
    fun setUp() {
        index = 0
        keys = generateLongKeys(size)
        val random = Random(42)
        val maps = listOf(stdMap, myMap, myGenericMap, chainedMap, fastUtilMap)
        for (map in maps) {
            map.clear()
        }
        keys.forEachIndexed { i, v ->
            val key = if (i % ONE_FAIL_OUT_OF == 0) random.nextLong() else v
            for (map in maps) {
                map[key] = v
            }
        }
    }

    private fun increment(): Int {
        if (++index == size) {
            index = 0
        }
        return index
    }

    private fun generateLongKeys(newSize: Int): LongArray {
        val random = Random(newSize)
        return LongArray(newSize) { random.nextLong() }
    }
}
