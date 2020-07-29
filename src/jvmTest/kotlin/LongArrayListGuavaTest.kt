import com.google.common.collect.testing.ListTestSuiteBuilder
import com.google.common.collect.testing.SampleElements
import com.google.common.collect.testing.TestListGenerator
import com.google.common.collect.testing.features.CollectionFeature
import com.google.common.collect.testing.features.CollectionSize
import com.google.common.collect.testing.features.ListFeature
import com.google.common.collect.testing.testers.ListAddAllAtIndexTester
import example.LongArrayList
import junit.framework.TestSuite
import org.junit.runner.RunWith
import org.junit.runners.AllTests

@RunWith(AllTests::class)
class LongArrayListGuavaTest {
    companion object {
        @JvmStatic
        fun suite(): TestSuite = ListTestSuiteBuilder
            .using(LongArrayListTestGenerator())
            .named("Long ArrayList test suite")
            .withFeatures(
                CollectionSize.ANY,
                CollectionFeature.KNOWN_ORDER,
                ListFeature.GENERAL_PURPOSE
            )
            .suppressing(ignoredTests)
            .createTestSuite()


        private val ignoredTests = listOf(
            getListAddAllAtIndexTester("testAddAllAtIndex_nullCollectionReference")
        )

        private fun getListAddAllAtIndexTester(methodName: String) =
            getTestMethodByName(ListAddAllAtIndexTester::class.java, methodName)

        private fun getTestMethodByName(clazz: Class<*>, methodName: String) =
            clazz.getDeclaredMethod(methodName)
    }
}

@Suppress("UNCHECKED_CAST")
private class LongArrayListTestGenerator : TestListGenerator<Long?> {
    override fun createArray(length: Int): Array<Long?> = arrayOfNulls(length)
    override fun samples(): SampleElements<Long?> = SampleElements(1, 0, -32, 43, 4564)
    override fun create(vararg elements: Any?): MutableList<Long?> {
        val list = LongArrayList()
        list.addAll(elements.toList().onEach { it!! }.filterIsInstance<Long>().toList())
        return list as MutableList<Long?>
    }

    override fun order(insertionOrder: MutableList<Long?>?) = insertionOrder
}
