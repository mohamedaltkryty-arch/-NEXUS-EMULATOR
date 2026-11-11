package com.nexus.emulator.engine;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class PspEngineTest {
    @Test
    public void recognizesSupportedPspFormatsCaseInsensitively() {
        assertTrue(PspEngine.supportsGame("PSP", "ISO"));
        assertTrue(PspEngine.supportsGame("PSP", "cso"));
        assertTrue(PspEngine.supportsGame("PSP", "PBP"));
        assertTrue(PspEngine.supportsGame("PSP", "zSo"));
    }

    @Test
    public void rejectsOtherPlatformsAndUnsupportedExtensions() {
        assertFalse(PspEngine.supportsGame("PS2", "iso"));
        assertFalse(PspEngine.supportsGame("PC", "exe"));
        assertFalse(PspEngine.supportsGame("PSP", "exe"));
        assertFalse(PspEngine.supportsGame(null, "iso"));
        assertFalse(PspEngine.supportsGame("PSP", null));
    }
}
