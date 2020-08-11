package test.jmh.map

import example.DEFAULT_CAPACITY
import example.DEFAULT_LOAD_FACTOR
import example.createChainedLinkedHashMap
import example.createLinkedHashMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
import org.openjdk.jmh.annotations.*
import org.openjdk.jol.info.GraphLayout
import kotlin.random.Random


@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
open class JVMLongBigMapForeach {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    open var size = 0

    open lateinit var keys: LongArray

    open val stdMap: MutableMap<Long, Long> = linkedMapOf()
    open val myMap: MutableMap<Long, Long> = createLinkedHashMap()
    open val chainedMap: MutableMap<Long, Long> = createChainedLinkedHashMap()
    open val fastUtilMap: MutableMap<Long, Long> = Long2LongLinkedOpenHashMap(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR)

    @Benchmark
    fun _1_Std() = stdMap.entries.sumBy { (it.key + it.value).toInt() }

    @Benchmark
    fun _2_OpenAddressing() = myMap.entries.sumBy { (it.key + it.value).toInt() }

    @Benchmark
    fun _3_FastUtil() = fastUtilMap.entries.sumBy { (it.key + it.value).toInt() }

    @Benchmark
    fun _4_Chained() = chainedMap.entries.sumBy { (it.key + it.value).toInt() }

    @Setup
    fun setUp() {
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

    private fun generateLongKeys(newSize: Int): LongArray {
        val random = Random(newSize)
        return LongArray(newSize) { random.nextLong() }
    }

    private fun memorySize(o: Any) = GraphLayout.parseInstance(o).totalSize()
    private fun printMemorySize(name: String, o: Any) = println("$name ${memorySize(o)}")
}

private const val ONE_FAIL_OUT_OF = 2
