package com.nexus.emulator.catalog;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class GameFileClassifierTest {
    @Test
    public void recognizesEveryPlayablePspFormatCaseInsensitively() {
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("iso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("ISO"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("cso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("PBP"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("zSo"));
    }

    @Test
    public void rejectsFormatsForPlatformsWithoutAnIntegratedRuntime() {
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("exe"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("msi"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("chd"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pkg"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("bin"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pdf"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(""));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(null));
    }

    @Test
    public void extractsExtensionFromDisplayNameAndWindowsPath() {
        assertEquals("iso", GameFileClassifier.extensionOf("God of War.ISO"));
        assertEquals("exe", GameFileClassifier.extensionOf("C:\\Games\\game.exe"));
        assertEquals("", GameFileClassifier.extensionOf("game"));
        assertEquals("", GameFileClassifier.extensionOf(".hidden"));
        assertEquals("", GameFileClassifier.extensionOf("game."));
        assertEquals("", GameFileClassifier.extensionOf(null));
    }

    @Test
    public void onlyPspIsAnImportableAndPlayablePlatform() {
        assertTrue(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PSP));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS2));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PS3));
        assertFalse(GameFileClassifier.isSupportedPlatform(GameFileClassifier.PC));
        assertEquals(1, GameFileClassifier.supportedDiscPlatforms().size());
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.supportedDiscPlatforms().get(0));
    }
}
