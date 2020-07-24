package test.jmh.map

import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx30G"])
internal abstract class MapBenchmark<T : Any> {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    protected open var aSize = 0

    @Param("get")
    protected open var bOperation = ""

    @Param("JAVA", "MY_MAP", "FastUtil", "MY_GENERIC_MAP")
    protected open var cMapName = ""

    protected lateinit var mapTest: MapTest<T>

    @Setup
    abstract fun setUp()
}

internal open class LongMap : MapBenchmark<Long>() {
    @Setup
    override fun setUp() {
        mapTest = createAndSetUpMapTest(aSize, bOperation, cMapName)
    }

    @Benchmark
    fun test() = mapTest.test()
}

internal open class IntMap : MapBenchmark<Int>() {
    @Setup
    override fun setUp() {
        mapTest = createAndSetUpMapTest(aSize, bOperation, cMapName)
    }

    @Benchmark
    fun test() = mapTest.test()
}

inline fun <reified T : Any> createAndSetUpMapTest(size: Int, operation: String, mapName: String) =
    createOperation<T>(operation).apply {
        setUp(generateStorage(size), createImplementation(mapName), ONE_FAIL_OUT_OF)
    }
