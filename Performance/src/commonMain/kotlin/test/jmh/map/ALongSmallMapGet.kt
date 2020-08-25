package test.jmh.map

import example.createChainedLinkedHashMap
import example.createLinkedHashMap
import kotlinx.benchmark.*
import kotlin.random.Random


@State(Scope.Benchmark)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
open class ALongSmallMapGet {

    @Param("10", "31", "100", "316", "1000", "3162")
    open var size = 0

    open lateinit var keys: Array<Long>

    open var index = 0

    open val stdMap: MutableMap<Long, Long> = linkedMapOf()
    open val myMap: MutableMap<Long, Long> = createLinkedHashMap()
    open val chainedMap: MutableMap<Long, Long> = createChainedLinkedHashMap()

    @Benchmark
    fun _1_Std() = stdMap[keys[increment()]] ?: 42

    @Benchmark
    fun _2_OpenAddressing() = myMap[keys[increment()]] ?: 42

    @Benchmark
    fun _3_Chained() = chainedMap[keys[increment()]] ?: 42

    @Setup
    fun setUp() {
        index = 0
        keys = generateLongKeys(size)
        val random = Random(42)
        val maps = listOf(stdMap, myMap, chainedMap)
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

    private fun generateLongKeys(newSize: Int): Array<Long> {
        val random = Random(newSize)
        return LongArray(newSize) { random.nextLong() }.toTypedArray()
    }
}

private const val ONE_FAIL_OUT_OF = 2
