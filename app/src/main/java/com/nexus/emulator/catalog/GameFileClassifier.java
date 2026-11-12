package com.nexus.emulator.catalog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class GameFileClassifier {
    public static final String PC = "PC";
    public static final String PSP = "PSP";
    public static final String PS2 = "PS2";
    public static final String PS3 = "PS3";
    public static final String AMBIGUOUS_DISC = "DISC";
    public static final String UNSUPPORTED = "UNSUPPORTED";

    private static final List<String> PSP_EXTENSIONS = Collections.unmodifiableList(
            Arrays.asList("iso", "cso", "pbp", "zso"));

    private GameFileClassifier() { }

    public static String extensionOf(String displayName) {
        if (displayName == null) return "";
        String name = displayName.trim();
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        String leaf = name.substring(slash + 1);
        int dot = leaf.lastIndexOf('.');
        if (dot <= 0 || dot == leaf.length() - 1) return "";
        return leaf.substring(dot + 1).trim().toLowerCase(Locale.ROOT);
    }

    public static String classify(String extension) {
        return isSupportedPspExtension(extension) ? PSP : UNSUPPORTED;
    }

    public static boolean isSupportedPspExtension(String extension) {
        if (extension == null) return false;
        String ext = extension.trim().toLowerCase(Locale.ROOT);
        return PSP_EXTENSIONS.contains(ext);
    }

    public static List<String> supportedPspExtensions() { return PSP_EXTENSIONS; }

    public static List<String> supportedDiscPlatforms() {
        return Collections.singletonList(PSP);
    }

    public static boolean isSupportedPlatform(String platform) {
        return PSP.equals(platform);
    }
}
