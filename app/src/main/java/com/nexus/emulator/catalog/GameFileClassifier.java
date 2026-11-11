package com.nexus.emulator.catalog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class GameFileClassifier {
    public static final String PSP = "PSP";
    public static final String PC = "PC";
    public static final String PS2 = "PS2";
    public static final String PS3 = "PS3";
    public static final String UNSUPPORTED = "UNSUPPORTED";

    private static final List<String> PSP_FORMATS =
            Collections.unmodifiableList(Arrays.asList("iso", "cso", "pbp", "zso"));

    private GameFileClassifier() { }

    public static String extensionOf(String displayName) {
        if (displayName == null) return "";
        int slash = Math.max(displayName.lastIndexOf('/'), displayName.lastIndexOf('\\'));
        String leaf = displayName.substring(slash + 1);
        int dot = leaf.lastIndexOf('.');
        if (dot <= 0 || dot == leaf.length() - 1) return "";
        return leaf.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    public static String classify(String extension) {
        String ext = extension == null ? "" : extension.trim().toLowerCase(Locale.ROOT);
        return PSP_FORMATS.contains(ext) ? PSP : UNSUPPORTED;
    }

    public static List<String> supportedDiscPlatforms() {
        return Collections.singletonList(PSP);
    }

    public static boolean isSupportedPlatform(String platform) {
        return PSP.equals(platform);
    }

    public static boolean isSupportedPspExtension(String extension) {
        return PSP_FORMATS.contains(extension == null ? "" : extension.trim().toLowerCase(Locale.ROOT));
    }
}
