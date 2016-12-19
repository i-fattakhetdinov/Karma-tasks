package Cache;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CacheTest {
    private final Cache<Integer, String> c;

    public CacheTest() {
        this.c = new Cache(2);
    }

    @Test
    public void testCacheStartsEmpty() {
        assertEquals(c.getIfPresent(2), null);
    }

    @Test
    public void testSetBelowCapacity() {
        c.put(1, "One");
        assertEquals(c.getIfPresent(1), "One");
        assertEquals(c.getIfPresent(2), null);
        c.put(2, "Two");
        assertEquals(c.getIfPresent(1), "One");
        assertEquals(c.getIfPresent(2), "Two");
    }

    @Test
    public void testCapacityReachedOldestRemoved() {
        c.put(1, "One");
        c.put(2, "Two");
        c.put(3, "Three");
        assertEquals(c.getIfPresent(1), null);
        assertEquals(c.getIfPresent(2), "Two");
        assertEquals(c.getIfPresent(3), "Three");
    }

    @Test
    public void testGetRenewsEntry() {
        c.put(1, "One");
        c.put(2, "Two");
        assertEquals(c.getIfPresent(1), "One");
        c.put(3, "Three");
        assertEquals(c.getIfPresent(1), "One");
        assertEquals(c.getIfPresent(2), null);
        assertEquals(c.getIfPresent(3), "Three");
    }

    @Test
    public void testRemoveDeleted() {
        c.put(1, "One");
        c.put(2, "Two");
        c.put(3, "Three");
        c.remove(1);
        assertEquals(c.getIfPresent(1), null);
        assertEquals(c.getIfPresent(2), "Two");
        assertEquals(c.getIfPresent(3), "Three");
    }

    @Test
    public void testClear() {
        c.put(1, "One");
        c.put(2, "Two");
        c.put(3, "Three");
        c.clear();
        assertEquals(c.getIfPresent(1), null);
        c.put(1, "Two");
        assertEquals(c.getIfPresent(1), "Two");
        assertEquals(c.getIfPresent(2), null);
        assertEquals(c.getIfPresent(3), null);
    }
}
