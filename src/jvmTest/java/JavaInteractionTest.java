import com.google.common.collect.testing.Helpers;
import example.LongLongLinkedHashMap;
import kotlin.test.AssertionsKt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JavaInteractionTest {
    private final Map<Long, Long> map = (Map<Long, Long>) new LongLongLinkedHashMap();

    @Test
    public void containsLongStringEntryShouldReturnFalse() {
        Map.Entry<Long, String> entry = Helpers.mapEntry(1L, "2");
        Assert.assertFalse(map.entrySet().contains(entry));
    }

    @Test
    public void removeAllThrowsIllegalArgumentException() {
        try {
            map.entrySet().removeAll(null);
            AssertionsKt.fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ignored) {
        }
    }
}
