package test.jmh.map

import example.DEFAULT_CAPACITY
import example.DEFAULT_LOAD_FACTOR
import example.createLinkedHashMap
import example.createLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private const val N = 1000

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@OperationsPerInvocation(N)
open class JVMIntSmallMapGet {

    @Param("10", "31", "100", "316", "1000", "3162")
    open var size = 0

    open lateinit var keys: IntArray

    open var index = 0

    open val stdMap: MutableMap<Int, Int> = linkedMapOf()
    open val myMap: MutableMap<Int, Int> = createLinkedHashMap()
    open val genericMap: MutableMap<Int, Int> = createLinkedOpenHashMap()
    open val fastUtilMap: MutableMap<Int, Int> = Int2IntLinkedOpenHashMap(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR)

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
    fun _4_Generic(bh: Blackhole) = repeat(N) {
        bh.consume(genericMap[keys[increment()]])
    }

    @Setup
    fun setUp() {
        index = 0
        keys = generateIntKeys(size)
        val random = Random(42)
        val maps = listOf(stdMap, myMap, genericMap, fastUtilMap)
        for (map in maps) {
            map.clear()
        }
        keys.forEachIndexed { i, v ->
            val key = if (i % ONE_FAIL_OUT_OF == 0) random.nextInt() else v
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

    private fun generateIntKeys(newSize: Int): IntArray {
        val random = Random(newSize)
        return IntArray(newSize) { random.nextInt() }
    }

}

private const val ONE_FAIL_OUT_OF = 2
