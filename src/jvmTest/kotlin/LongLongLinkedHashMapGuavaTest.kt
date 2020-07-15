import com.google.common.collect.testing.MapTestSuiteBuilder
import com.google.common.collect.testing.features.CollectionFeature
import com.google.common.collect.testing.features.CollectionSize
import com.google.common.collect.testing.features.MapFeature
import com.google.common.collect.testing.testers.CollectionRemoveAllTester
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
