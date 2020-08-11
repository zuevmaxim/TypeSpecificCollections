package test.jmh.list

import it.unimi.dsi.fastutil.longs.LongArrayList
import org.openjdk.jmh.annotations.*
import org.openjdk.jol.info.GraphLayout
import kotlin.random.Random

@Suppress("FunctionName")
@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
open class JVMList {

    @Param(
        "10", "31", "100", "316", "1000", "3162",
        "10000", "31623", "100000", "316228", "1000000", "3162278", "10000000"
    )
    open var size = 0

    // STD
    open val stdList: MutableList<Long> = mutableListOf()

    @Setup
    fun stdListSetUp() {
        stdList.addAll(generateLongKeys(size).toList())
        printSize("STD", stdList)
    }

    @Benchmark
    fun _1StdListForeach() = stdList.sum()


    // MY_LIST
    open val myList: MutableList<Long> = example.LongArrayList()

    @Setup
    fun setUpMyList() {
        myList.addAll(generateLongKeys(size).toList())
        printSize("MY_LIST", myList)
    }

    @Benchmark
    fun _2MyListForeach() = myList.sum()


    // FASTUTIL
    open val fastutilList: MutableList<Long> = LongArrayList()

    @Setup
    fun fastutilListSetUp() {
        fastutilList.addAll(generateLongKeys(size).toList())
        printSize("FASTUTIL", fastutilList)
    }

    @Benchmark
    fun _3FastutilListForeach() = fastutilList.sum()

    private fun printSize(name: String, o: Any) {
        val layoutSize = GraphLayout.parseInstance(o).totalSize()
        println("$name size = $size mem = $layoutSize")
    }

    private fun generateLongKeys(newSize: Int): LongArray {
        val random = Random(newSize)
        return LongArray(newSize) { random.nextLong() }
    }
}
