import com.google.common.collect.testing.Helpers
import com.google.common.collect.testing.MapTestSuiteBuilder
import com.google.common.collect.testing.SampleElements
import com.google.common.collect.testing.TestMapGenerator
import com.google.common.collect.testing.features.CollectionFeature
import com.google.common.collect.testing.features.CollectionSize
import com.google.common.collect.testing.features.MapFeature
import com.google.common.collect.testing.testers.CollectionRemoveAllTester
import example.createLinkedOpenHashMap
import junit.framework.TestSuite
import org.junit.runner.RunWith
import org.junit.runners.AllTests

@RunWith(AllTests::class)
class LongLongLinkedHashMapGuavaTest {
    companion object {
        @JvmStatic
        fun suite(): TestSuite = MapTestSuiteBuilder
            .using(LongLongHashMapTestGenerator())
            .named("Long Long HashMap test suite")
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
}

private class LongLongHashMapTestGenerator : TestMapGenerator<Long?, Long?> {
    override fun createKeyArray(length: Int) = arrayOfNulls<Long?>(length)
    override fun createValueArray(length: Int) = arrayOfNulls<Long?>(length)
    override fun createArray(length: Int) = arrayOfNulls<Map.Entry<Long?, Long?>>(length)
    override fun order(insertionOrder: List<Map.Entry<Long?, Long?>>) = insertionOrder

    override fun samples(): SampleElements<Map.Entry<Long?, Long?>> {
        return SampleElements(
            Helpers.mapEntry(1L, 123L),
            Helpers.mapEntry(2L, 234L),
            Helpers.mapEntry(3L, 345L),
            Helpers.mapEntry(345L, 6L),
            Helpers.mapEntry(777L, 666L)
        )
    }

    override fun create(vararg entries: Any): Map<Long?, Long?> {
        val map = createLinkedOpenHashMap<Long, Long>() as MutableMap<Long?, Long?>
        for (o in entries) {
            val entry = o as Map.Entry<Long?, Long?>?
            map[entry!!.key] = entry.value
        }
        return map
    }
}
