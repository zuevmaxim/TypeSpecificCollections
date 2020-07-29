package test.jmh.map

import kotlinx.benchmark.*


@State(Scope.Benchmark)
abstract class MapBenchmark<T : Any> {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    open var aSize = 0

    @Param("get")
    open var bOperation = ""

    @Param("STD", "MY_MAP", "MY_GENERIC_MAP")
    open var cMapName: String = ""

    lateinit var mapTest: MapTest<T>

    @Setup
    abstract fun setUp()
}

@State(Scope.Benchmark)
@Measurement(iterations = 5, time = 10, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class LongMap : MapBenchmark<Long>() {
    @Setup
    override fun setUp() {
        mapTest = createAndSetUpMapTest(aSize, bOperation, cMapName)
    }

    @Benchmark
    fun test() = mapTest.test()
}

@State(Scope.Benchmark)
@Measurement(iterations = 5, time = 10, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class IntMap : MapBenchmark<Int>() {
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

const val ONE_FAIL_OUT_OF = 2
