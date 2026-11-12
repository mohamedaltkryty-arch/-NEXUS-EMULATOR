package com.nexus.emulator.util;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class AppStringsTest {
    private static final String[] VISIBLE_KEYS = {
            "about", "about_body", "all", "arabic", "cancel", "clear_search",
            "dark", "details", "empty_body", "empty_title", "english", "favorites",
            "favorite", "unfavorite", "file_picker_error", "file_type",
            "games_in_library", "hero_body", "hero_title", "import", "import_summary",
            "importing", "install_engine", "language", "launch_error", "library",
            "light", "local_library", "no_files_selected", "no_matches_body",
            "no_matches_title", "ok", "platform", "play", "psp_engine_missing_body",
            "psp_engine_missing_title", "psp_ready_body", "remove", "search",
            "settings", "size", "theme", "unknown_game"
    };

    @Test
    public void everyVisibleStringHasArabicAndEnglishTranslation() {
        for (String key : VISIBLE_KEYS) {
            String arabic = AppStrings.get(key, true);
            String english = AppStrings.get(key, false);
            assertNotNull("Arabic translation missing: " + key, arabic);
            assertNotNull("English translation missing: " + key, english);
            assertNotEquals("Arabic falls back to key: " + key, key, arabic);
            assertNotEquals("English falls back to key: " + key, key, english);
            assertTrue("Arabic translation is blank: " + key, !arabic.trim().isEmpty());
            assertTrue("English translation is blank: " + key, !english.trim().isEmpty());
        }
    }

    @Test
    public void importSummaryFormatsInBothLanguages() {
        String arabic = String.format(new Locale("ar"),
                AppStrings.get("import_summary", true), 2, 1, 3);
        String english = String.format(Locale.ENGLISH,
                AppStrings.get("import_summary", false), 2, 1, 3);

        assertTrue(arabic.contains("2"));
        assertTrue(english.contains("2"));
        assertTrue(english.contains("3"));
    }
}
