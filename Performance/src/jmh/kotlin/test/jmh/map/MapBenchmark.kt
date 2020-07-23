package test.jmh.map

import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
@Fork(1, jvmArgsAppend = ["-Xmx30G"])
internal open class MapBenchmark {

    @Param("10000", "31623", "100000", "316228", "1000000", "3162278", "10000000")
    protected open var aSize = 0

    @Param("get")
    protected open var bOperation = ""

    @Param("JAVA", "MY_MAP", "FastUtil", "MY_GENERIC_MAP")
    protected open var cMapName = ""

    private lateinit var mapTest: MapTest

    @Setup
    fun setUp() {
        val map = createImplementation(cMapName)
        mapTest = createOperation(bOperation)
        mapTest.setUp(generateKeys(aSize), map, ONE_FAIL_OUT_OF)
    }

    @Benchmark
    fun test(): Any? {
        return mapTest.test()
    }
}
