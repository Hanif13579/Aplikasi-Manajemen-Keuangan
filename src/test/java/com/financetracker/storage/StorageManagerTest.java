package com.financetracker.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StorageManagerTest {

    @Test
    void testGetInstance() {
        StorageManager instance1 = StorageManager.getInstance();
        StorageManager instance2 = StorageManager.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "StorageManager should be a Singleton");
    }
}
