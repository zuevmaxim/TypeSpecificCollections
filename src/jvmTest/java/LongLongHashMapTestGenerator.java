import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import example.LongLongLinkedHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class LongLongHashMapTestGenerator implements TestMapGenerator<Long, Long> {
    public Long[] createKeyArray(final int length) {
        return new Long[length];
    }

    public Long[] createValueArray(final int length) {
        return new Long[length];
    }

    public SampleElements<Map.Entry<Long, Long>> samples() {
        return new SampleElements<>(
                Helpers.mapEntry(1L, 123L),
                Helpers.mapEntry(2L, 234L),
                Helpers.mapEntry(3L, 345L),
                Helpers.mapEntry(345L, 6L),
                Helpers.mapEntry(777L, 666L));
    }

    public Map<Long, Long> create(@NotNull final Object... entries) {
        Map<Long, Long> map = (Map<Long, Long>) new LongLongLinkedHashMap();
        for (Object o : entries) {
            Map.Entry<Long, Long> entry = (Map.Entry<Long, Long>) o;
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public Map.Entry<Long, Long>[] createArray(final int length) {
        return new Map.Entry[length];
    }

    public Iterable<Map.Entry<Long, Long>> order(final List<Map.Entry<Long, Long>> insertionOrder) {
        return insertionOrder;
    }
}
