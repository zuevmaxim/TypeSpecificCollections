import com.google.common.collect.testing.MapTestSuiteBuilder
import com.google.common.collect.testing.features.CollectionFeature
import com.google.common.collect.testing.features.CollectionSize
import com.google.common.collect.testing.features.MapFeature
import junit.framework.TestSuite
import org.junit.runner.RunWith
import org.junit.runners.AllTests

@RunWith(AllTests::class)
class LongLongLinkedHashMapGuavaTest {
    companion object {
        @JvmStatic
        fun suite(): TestSuite? {
            return MapTestSuiteBuilder
                .using(LongLongHashMapTestGenerator())
                .named("Long Long HashMap test suite")
                .withFeatures(
                    CollectionSize.ANY,
                    CollectionFeature.SUPPORTS_REMOVE,
                    CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                    CollectionFeature.KNOWN_ORDER,
                    MapFeature.GENERAL_PURPOSE
                )
                .createTestSuite()
        }
    }
}
