package com.nexus.emulator.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class SettingsStore {
    private static final String PREFS = "nexus_settings";
    private static final String KEY_ARABIC = "language_arabic";
    private static final String KEY_DARK = "theme_dark";
    private final SharedPreferences preferences;

    public SettingsStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isArabic() { return preferences.getBoolean(KEY_ARABIC, true); }
    public boolean isDarkTheme() { return preferences.getBoolean(KEY_DARK, true); }
    public void setArabic(boolean value) { preferences.edit().putBoolean(KEY_ARABIC, value).apply(); }
    public void setDarkTheme(boolean value) { preferences.edit().putBoolean(KEY_DARK, value).apply(); }
}
