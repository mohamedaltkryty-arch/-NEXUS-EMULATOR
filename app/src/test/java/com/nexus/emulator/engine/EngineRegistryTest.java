package com.nexus.emulator.engine;

import com.nexus.emulator.catalog.GameFileClassifier;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class EngineRegistryTest {
    @Test public void registersAllFourEngineSources() {
        assertEquals(4, EngineRegistry.all().size());
        assertNotNull(EngineRegistry.forPlatform(GameFileClassifier.PSP));
        assertNotNull(EngineRegistry.forPlatform(GameFileClassifier.PS2));
        assertNotNull(EngineRegistry.forPlatform(GameFileClassifier.PC));
        assertNotNull(EngineRegistry.forPlatform(GameFileClassifier.PS3));
    }

    @Test public void enginesAreNotMarkedLaunchReadyBeforeRuntimeIntegration() {
        for (EngineDescriptor engine : EngineRegistry.all()) {
            assertFalse(engine.isLaunchReady());
            assertTrue(engine.getIntegrationStatus().contains("PENDING"));
        }
    }

    @Test public void unknownPlatformHasNoEngine() {
        assertNull(EngineRegistry.forPlatform("UNKNOWN"));
    }
}
