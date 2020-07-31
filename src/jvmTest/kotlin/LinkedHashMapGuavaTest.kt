@file:Suppress("UNCHECKED_CAST")

import com.google.common.collect.testing.Helpers
import com.google.common.collect.testing.MapTestSuiteBuilder
import com.google.common.collect.testing.SampleElements
import com.google.common.collect.testing.TestMapGenerator
import com.google.common.collect.testing.features.CollectionFeature
import com.google.common.collect.testing.features.CollectionSize
import com.google.common.collect.testing.features.MapFeature
import com.google.common.collect.testing.testers.CollectionRemoveAllTester
import example.LongLongChainedLinkedHashMap
import example.createLinkedHashMap
import example.createLinkedOpenHashMap
import junit.framework.TestSuite
import org.junit.runner.RunWith
import org.junit.runners.AllTests

@RunWith(AllTests::class)
object LongLongLinkedHashMapGuavaTest {
    @JvmStatic
    fun suite() = LinkedHashMapGuavaTest()
        .suite(LongLongHashMapTestGenerator { createLinkedHashMap<Long, Long>() as MutableMap<Long?, Long?> })
}

@RunWith(AllTests::class)
object IntIntLinkedHashMapGuavaTest {
    @JvmStatic
    fun suite() = LinkedHashMapGuavaTest()
        .suite(IntIntHashMapTestGenerator { createLinkedHashMap<Int, Int>() as MutableMap<Int?, Int?> })
}

@RunWith(AllTests::class)
object GenericLongLongLinkedHashMapGuavaTest {
    @JvmStatic
    fun suite() = LinkedHashMapGuavaTest()
        .suite(LongLongHashMapTestGenerator { createLinkedOpenHashMap<Long, Long>() as MutableMap<Long?, Long?> })
}

@RunWith(AllTests::class)
object GenericIntIntLinkedHashMapGuavaTest {
    @JvmStatic
    fun suite() = LinkedHashMapGuavaTest()
        .suite(IntIntHashMapTestGenerator { createLinkedOpenHashMap<Int, Int>() as MutableMap<Int?, Int?> })
}

@RunWith(AllTests::class)
object LongLongChainedLinkedHashMapGuavaTest {
    @JvmStatic
    fun suite() = LinkedHashMapGuavaTest()
        .suite(LongLongHashMapTestGenerator { LongLongChainedLinkedHashMap() as MutableMap<Long?, Long?> })
}

class LinkedHashMapGuavaTest {
    fun <K, V> suite(generator: TestMapGenerator<K?, V?>): TestSuite = MapTestSuiteBuilder
        .using(generator)
        .named("HashMap test suite")
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.SUPPORTS_REMOVE,
            CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
            CollectionFeature.KNOWN_ORDER,
            MapFeature.GENERAL_PURPOSE
        )
        .suppressing(ignoredTests)
        .createTestSuite()


    private val ignoredTests = listOf(
        getCollectionRemoveAllTesterTest("testRemoveAll_nullCollectionReferenceEmptySubject"),
        getCollectionRemoveAllTesterTest("testRemoveAll_nullCollectionReferenceNonEmptySubject")
    )

    private fun getCollectionRemoveAllTesterTest(methodName: String) =
        getTestMethodByName(CollectionRemoveAllTester::class.java, methodName)

    private fun getTestMethodByName(clazz: Class<*>, methodName: String) =
        clazz.getDeclaredMethod(methodName)
}

private abstract class HashMapTestGenerator<K, V>(private val createMap: () -> MutableMap<K?, V?>) :
    TestMapGenerator<K?, V?> {
    override fun createArray(length: Int) = arrayOfNulls<Map.Entry<K?, V?>>(length)
    override fun order(insertionOrder: List<Map.Entry<K?, V?>>) = insertionOrder
    override fun create(vararg entries: Any): Map<K?, V?> {
        val map = createMap()
        for (o in entries) {
            val entry = o as Map.Entry<K?, V?>?
            map[entry!!.key] = entry.value
        }
        return map
    }
}

private class LongLongHashMapTestGenerator(createMap: () -> MutableMap<Long?, Long?>) :
    HashMapTestGenerator<Long, Long>(createMap) {
    override fun createKeyArray(length: Int) = arrayOfNulls<Long?>(length)
    override fun createValueArray(length: Int) = arrayOfNulls<Long?>(length)
    override fun samples(): SampleElements<Map.Entry<Long?, Long?>> = SampleElements(
        Helpers.mapEntry(1L, 123L),
        Helpers.mapEntry(2L, 234L),
        Helpers.mapEntry(3L, 345L),
        Helpers.mapEntry(345L, 6L),
        Helpers.mapEntry(777L, 666L)
    )
}

private class IntIntHashMapTestGenerator(createMap: () -> MutableMap<Int?, Int?>) :
    HashMapTestGenerator<Int, Int>(createMap) {
    override fun createKeyArray(length: Int) = arrayOfNulls<Int?>(length)
    override fun createValueArray(length: Int) = arrayOfNulls<Int?>(length)
    override fun samples(): SampleElements<Map.Entry<Int?, Int?>> = SampleElements(
        Helpers.mapEntry(1, 123),
        Helpers.mapEntry(2, 234),
        Helpers.mapEntry(3, 345),
        Helpers.mapEntry(345, 6),
        Helpers.mapEntry(777, 666)
    )
}
