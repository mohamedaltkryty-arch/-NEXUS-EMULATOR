package com.nexus.emulator.catalog;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class GameFileClassifierTest {
    @Test public void extractsExtensionFromFileName() {
        assertEquals("iso", GameFileClassifier.extensionOf("God of War.ISO"));
        assertEquals("exe", GameFileClassifier.extensionOf("C:\\Games\\game.exe"));
    }

    @Test public void detectsWindowsInstallers() {
        assertEquals(GameFileClassifier.PC, GameFileClassifier.classify("exe"));
        assertEquals(GameFileClassifier.PC, GameFileClassifier.classify("MSI"));
    }

    @Test public void detectsPspFormats() {
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("pbp"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("zso"));
    }

    @Test public void keepsAmbiguousDiscFormatsForManualPlatformSelection() {
        assertEquals(GameFileClassifier.AMBIGUOUS_DISC, GameFileClassifier.classify("iso"));
        assertEquals(GameFileClassifier.AMBIGUOUS_DISC, GameFileClassifier.classify("bin"));
        assertEquals(GameFileClassifier.AMBIGUOUS_DISC, GameFileClassifier.classify("cso"));
        assertTrue(GameFileClassifier.supportedDiscPlatforms().contains(GameFileClassifier.PS2));
    }

    @Test public void rejectsUnknownExtensions() {
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("pdf"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(""));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify(null));
    }
}
