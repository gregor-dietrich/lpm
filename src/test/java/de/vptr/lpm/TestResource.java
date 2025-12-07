package de.vptr.lpm;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Test resource for database initialization.
 */
@QuarkusTestResource(TestResource.DatabaseResource.class)
public class TestResource {

    /**
     * Database lifecycle manager for tests.
     */
    public static class DatabaseResource implements QuarkusTestResourceLifecycleManager {

        @Override
        public Map<String, String> start() {
            return Map.of();
        }

        @Override
        public void stop() {
        }
    }
}
