package test.jmh.map

import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
import org.openjdk.jmh.annotations.*
import org.openjdk.jol.info.GraphLayout

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx4G"])
abstract class JVMMap<T : Any> {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    protected open var aSize = 0

    @Param("get")
    protected open var bOperation = ""

    @Param("STD", "MY_MAP", "FastUtil", "MY_GENERIC_MAP")
    protected open var cMapName = ""

    protected lateinit var mapTest: MapTest<T>

    @Setup
    abstract fun setUp()
}

open class JVMLongMap : JVMMap<Long>() {
    @Setup
    override fun setUp() {
        mapTest = createAndSetUpMapTest(aSize, bOperation, cMapName)
    }

    @Benchmark
    fun test() = mapTest.test()
}

open class JVMIntMap : JVMMap<Int>() {
    @Setup
    override fun setUp() {
        mapTest = createAndSetUpMapTest(aSize, bOperation, cMapName)
    }

    @Benchmark
    fun test() = mapTest.test()
}

private inline fun <reified T : Any> createAndSetUpMapTest(size: Int, operation: String, mapName: String): MapTest<T> {
    val testingMap = createImplementationJVM<T>(mapName)
    return createOperation<T>(operation).apply {
        setUp(generateStorage(size), testingMap, ONE_FAIL_OUT_OF)
    }.also {
        val map = (testingMap as AbstractTestingMap).map
        val layoutSize = GraphLayout.parseInstance(map).totalSize()
        println("size = ${map.size} mem = $layoutSize")
    }
}

private inline fun <reified K> createImplementationJVM(name: String): TestingMap<K> = if (name == FastUtilMap.NAME) {
    FastUtilMap(createFastUtilMap())
} else {
    createImplementation(name)
}

private class FastUtilMap<K>(override val map: MutableMap<K, K>) : AbstractTestingMap<K>() {
    companion object {
        const val NAME = "FastUtil"
    }
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
private inline fun <reified T> createFastUtilMap() = when (T::class) {
    Long::class -> Long2LongLinkedOpenHashMap()
    Int::class -> Int2IntLinkedOpenHashMap()
    else -> error("Type is not implemented")
} as MutableMap<T, T>
