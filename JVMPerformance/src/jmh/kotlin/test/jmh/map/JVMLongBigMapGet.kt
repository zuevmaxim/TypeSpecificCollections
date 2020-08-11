package test.jmh.map

import example.DEFAULT_CAPACITY
import example.DEFAULT_LOAD_FACTOR
import example.createChainedLinkedHashMap
import example.createLinkedHashMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jol.info.GraphLayout
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private const val N = 1000

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@OperationsPerInvocation(N)
open class JVMLongBigMapGet {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    open var size = 0

    open lateinit var keys: LongArray

    open var index = 0

    open val stdMap: MutableMap<Long, Long> = linkedMapOf()
    open val myMap: MutableMap<Long, Long> = createLinkedHashMap()
    open val chainedMap: MutableMap<Long, Long> = createChainedLinkedHashMap()
    open val fastUtilMap: MutableMap<Long, Long> = Long2LongLinkedOpenHashMap(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR)

    @Benchmark
    fun _1_Std(bh: Blackhole) = repeat(N) {
        bh.consume(stdMap[keys[increment()]])
    }

    @Benchmark
    fun _2_OpenAddressing(bh: Blackhole) = repeat(N) {
        bh.consume(myMap[keys[increment()]])
    }

    @Benchmark
    fun _3_FastUtil(bh: Blackhole) = repeat(N) {
        bh.consume(fastUtilMap[keys[increment()]])
    }

    @Benchmark
    fun _4_Chained(bh: Blackhole) = repeat(N) {
        bh.consume(chainedMap[keys[increment()]])
    }

    @Setup
    fun setUp() {
        index = 0
        keys = generateLongKeys(size)
        val random = Random(42)
        val maps = listOf(stdMap, myMap, chainedMap, fastUtilMap)
        for (map in maps) {
            map.clear()
        }
        keys.forEachIndexed { i, v ->
            val key = if (i % ONE_FAIL_OUT_OF == 0) random.nextLong() else v
            for (map in maps) {
                map[key] = v
            }
        }
        printMemorySize("Std", stdMap)
        printMemorySize("MyMap", myMap)
        printMemorySize("FastUtils", fastUtilMap)
        printMemorySize("Chained", chainedMap)
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

    private fun memorySize(o: Any) = GraphLayout.parseInstance(o).totalSize()
    private fun printMemorySize(name: String, o: Any) = println("$name ${memorySize(o)}")
}

private const val ONE_FAIL_OUT_OF = 2
