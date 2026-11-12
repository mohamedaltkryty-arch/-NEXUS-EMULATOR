package com.nexus.emulator.catalog;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class GameFileClassifierTest {
    @Test
    public void extractsExtensionFromFileName() {
        assertEquals("iso", GameFileClassifier.extensionOf("God of War.ISO"));
        assertEquals("exe", GameFileClassifier.extensionOf("C:\\Games\\game.exe"));
        assertEquals("", GameFileClassifier.extensionOf("No extension"));
        assertEquals("", GameFileClassifier.extensionOf("archive."));
    }

    @Test
    public void recognizesOnlyPspFormats() {
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("ISO"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("cso"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("pbp"));
        assertEquals(GameFileClassifier.PSP, GameFileClassifier.classify("zSo"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("exe"));
        assertEquals(GameFileClassifier.UNSUPPORTED, GameFileClassifier.classify("chd"));
    }

    @Test
    public void exposesImmutablePspAllowList() {
        assertEquals(4, GameFileClassifier.supportedPspExtensions().size());
        assertTrue(GameFileClassifier.supportedPspExtensions().contains("iso"));
    }
}
