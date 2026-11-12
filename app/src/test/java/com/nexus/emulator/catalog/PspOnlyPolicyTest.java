package com.nexus.emulator.catalog;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class PspOnlyPolicyTest {
    @Test
    public void acceptsAllSupportedPspDiscFormats() {
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("iso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("cso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("pbp"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("zso"));
    }

    @Test
    public void rejectsFormatsForPlatformsNotAvailableInTheApp() {
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("exe"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("msi"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("chd"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pkg"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("bin"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pdf"));
    }

    @Test
    public void onlyPspCanBeStoredInTheActiveLibrary() {
        assertTrue(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PSP));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PC));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS2));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS3));
    }
}
