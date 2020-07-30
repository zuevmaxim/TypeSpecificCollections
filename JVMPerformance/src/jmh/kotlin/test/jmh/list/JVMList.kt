package test.jmh.list

import it.unimi.dsi.fastutil.longs.LongArrayList
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jol.info.GraphLayout
import test.jmh.map.generateLongKeys

@Suppress("FunctionName")
@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
open class JVMList {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    open var size = 0

    // STD
    open val stdList = mutableListOf<Long>()

    @Setup
    fun stdListSetUp() {
        stdList.addAll(generateLongKeys(size).toList())
        printSize("STD", stdList)
    }

    @Benchmark
    fun _1StdListForeach(bh: Blackhole) = stdList.forEach { bh.consume(it) }


    // MY_LIST
    open val myList = example.LongArrayList()

    @Setup
    fun setUpMyList() {
        myList.addAll(generateLongKeys(size).toList())
        printSize("MY_LIST", myList)
    }

    @Benchmark
    fun _2MyListForeach(bh: Blackhole) = myList.forEach { bh.consume(it) }


    // FASTUTIL
    open val fastutilList = LongArrayList()

    @Setup
    fun fastutilListSetUp() {
        fastutilList.addAll(generateLongKeys(size).toList())
        printSize("FASTUTIL", fastutilList)
    }

    @Benchmark
    fun _3FastutilListForeach(bh: Blackhole) = fastutilList.forEach { bh.consume(it) }

    private fun printSize(name: String, o: Any) {
        val layoutSize = GraphLayout.parseInstance(o).totalSize()
        println("$name size = $size mem = $layoutSize")
    }
}
