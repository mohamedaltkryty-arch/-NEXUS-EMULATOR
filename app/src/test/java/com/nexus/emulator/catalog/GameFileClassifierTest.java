package com.nexus.emulator.catalog;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public final class GameFileClassifierTest {
    @Test public void extractsExtensionFromFileName() {
        assertEquals("iso", GameFileClassifier.extensionOf("God of War.ISO"));
        assertEquals("exe", GameFileClassifier.extensionOf("C:\\Games\\game.exe"));
        assertEquals("", GameFileClassifier.extensionOf("game"));
        assertEquals("", GameFileClassifier.extensionOf(".hidden"));
    }

    @Test public void acceptsOnlyPspRuntimeFormats() {
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("iso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("CSO"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("pbp"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("ZSO"));
        assertTrue(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PSP));
    }

    @Test public void rejectsPlatformsWithoutIntegratedRuntime() {
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("exe"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("msi"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pkg"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("chd"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("bin"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pdf"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(""));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(null));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS2));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS3));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PC));
    }
}
