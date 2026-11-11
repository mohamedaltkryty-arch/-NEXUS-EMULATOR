package com.nexus.emulator.engine;

import com.nexus.emulator.catalog.GameFileClassifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class EngineRegistryTest {
    @Test
    public void exposesOnlyTheRuntimeThatTheAppCanActuallyLaunch() {
        assertEquals(1, EngineRegistry.all().size());
        EngineDescriptor psp = EngineRegistry.forPlatform(GameFileClassifier.PSP);
        assertNotNull(psp);
        assertEquals("PPSSPP", psp.getName());
        assertTrue(psp.isLaunchReady());
        assertEquals("EXTERNAL_RUNTIME_SUPPORTED", psp.getIntegrationStatus());
        assertNull(EngineRegistry.forPlatform(GameFileClassifier.PS2));
        assertNull(EngineRegistry.forPlatform(GameFileClassifier.PC));
        assertNull(EngineRegistry.forPlatform(GameFileClassifier.PS3));
    }

    @Test
    public void unknownPlatformHasNoEngine() {
        assertNull(EngineRegistry.forPlatform("UNKNOWN"));
    }
}
