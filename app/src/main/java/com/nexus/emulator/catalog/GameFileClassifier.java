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
        String ext = extension == null ? "" : extension.toLowerCase(Locale.ROOT);
        switch (ext) {
            case "exe":
            case "msi":
                return PC;
            case "pbp":
            case "zso":
                return PSP;
            case "pkg":
                return PS3;
            case "chd":
                return PS2;
            case "iso":
            case "bin":
            case "cso":
                return AMBIGUOUS_DISC;
            default:
                return UNSUPPORTED;
        }
    }

    public static List<String> supportedDiscPlatforms() {
        return Collections.unmodifiableList(Arrays.asList(PSP, PS2, PS3));
    }

    public static boolean isSupportedPlatform(String platform) {
        return PC.equals(platform) || PSP.equals(platform) || PS2.equals(platform) || PS3.equals(platform);
    }
}
