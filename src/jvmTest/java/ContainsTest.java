import com.google.common.collect.testing.Helpers;
import example.LongLongLinkedHashMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ContainsTest {
    @Test
    public void containsLongStringEntryShouldReturnFalse() {
        Map<Long, Long> map = (Map<Long, Long>)new LongLongLinkedHashMap();
        Map.Entry<Long, String> entry = Helpers.mapEntry(1L, "2");
        Assert.assertFalse(map.entrySet().contains(entry));
    }
}
