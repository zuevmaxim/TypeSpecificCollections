import com.google.common.collect.testing.Helpers
import com.google.common.collect.testing.SampleElements
import com.google.common.collect.testing.TestMapGenerator
import example.createLinkedOpenHashMap

class LongLongHashMapTestGenerator : TestMapGenerator<Long?, Long?> {
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