package com.nexus.emulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexus.emulator.catalog.GameFileClassifier;
import com.nexus.emulator.data.GameDatabase;
import com.nexus.emulator.data.SettingsStore;
import com.nexus.emulator.engine.PspEngine;
import com.nexus.emulator.model.GameEntry;
import com.nexus.emulator.util.AppStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int REQUEST_IMPORT_FILES = 4101;

    private GameDatabase database;
    private SettingsStore settings;
    private LinearLayout page;
    private LinearLayout gameList;
    private EditText searchField;
    private TextView gameCount;
    private String activeFilter = "ALL";
    private final List<Uri> pendingFiles = new ArrayList<>();
    private int pendingFileIndex;
    private int pendingGrantFlags;

    private static final class Palette {
        final int background;
        final int surface;
        final int border;
        final int primaryText;
        final int secondaryText;
        final int accent;
        final int accentSoft;
        final int field;

        Palette(boolean dark) {
            if (dark) {
                background = Color.rgb(8, 12, 22);
                surface = Color.rgb(17, 25, 42);
                border = Color.rgb(36, 49, 72);
                primaryText = Color.rgb(244, 247, 255);
                secondaryText = Color.rgb(151, 164, 189);
                accent = Color.rgb(113, 96, 255);
                accentSoft = Color.rgb(42, 39, 82);
                field = Color.rgb(18, 29, 48);
            } else {
                background = Color.rgb(244, 247, 252);
                surface = Color.WHITE;
                border = Color.rgb(222, 229, 241);
                primaryText = Color.rgb(19, 27, 45);
                secondaryText = Color.rgb(100, 114, 139);
                accent = Color.rgb(91, 75, 224);
                accentSoft = Color.rgb(237, 234, 255);
                field = Color.rgb(235, 240, 248);
            }
        }
    }

    private Palette palette;

    @Override
    protected void onCreate(Bundle state) {
        boolean dark = getSharedPreferences("nexus_settings", MODE_PRIVATE).getBoolean("theme_dark", true);
        setTheme(dark ? R.style.AppThemeDark : R.style.AppThemeLight);
        super.onCreate(state);

        settings = new SettingsStore(this);
        database = new GameDatabase(this);
        applySystemBars();
        renderScreen();
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }

    private boolean isArabic() {
        return settings.isArabic();
    }

    private String s(String key) {
        return AppStrings.get(key, isArabic());
    }

    private void applySystemBars() {
        Window window = getWindow();
        boolean dark = settings.isDarkTheme();
        window.setStatusBarColor(dark ? Color.rgb(10, 12, 19) : Color.rgb(245, 247, 251));
        window.setNavigationBarColor(dark ? Color.rgb(10, 12, 19) : Color.rgb(245, 247, 251));
        int flags = dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private void renderScreen() {
        palette = new Palette(settings.isDarkTheme());

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(palette.background);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(18), dp(20), dp(32));
        page.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        page.setBackgroundColor(palette.background);
        scroll.addView(page);

        renderHeader();
        renderHero();
        renderStatistics();
        renderLibraryHeader();
        renderFilters();
        renderGameList();
        renderFooter();

        setContentView(scroll);
        applySystemBars();
    }

    private void renderHeader() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout branding = new LinearLayout(this);
        branding.setOrientation(LinearLayout.VERTICAL);
        TextView name = text("NEXUS", 27, palette.primaryText, true);
        name.setLetterSpacing(0.12f);
        branding.addView(name);
        TextView caption = text(isArabic() ? "مكتبة ألعاب PSP الخاصة بك" : "YOUR PSP GAME LIBRARY",
                9, palette.secondaryText, true);
        caption.setLetterSpacing(0.10f);
        branding.addView(caption, lp(-2, dp(18)));
        header.addView(branding, new LinearLayout.LayoutParams(0, -2, 1));

        Button settingsButton = smallButton("⚙", false);
        settingsButton.setContentDescription(s("settings"));
        settingsButton.setOnClickListener(v -> showSettings());
        header.addView(settingsButton, lp(dp(46), dp(46)));
        page.addView(header, margin(-1, -2, 0, dp(18)));
    }

    private void renderHero() {
        LinearLayout hero = panel();
        GradientDrawable heroBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(44, 47, 112), Color.rgb(25, 35, 65)});
        heroBackground.setCornerRadius(dp(24));
        hero.setBackground(heroBackground);
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));

        TextView badge = text("NEXUS  /  PSP COLLECTION", 10, Color.rgb(205, 202, 255), true);
        badge.setLetterSpacing(0.10f);
        hero.addView(badge);
        hero.addView(text(s("hero_title"), 23, Color.WHITE, true), margin(-1, -2, dp(12), dp(7)));
        TextView body = text(s("hero_body"), 13, Color.rgb(211, 219, 242), false);
        body.setLineSpacing(dp(2), 1.0f);
        hero.addView(body);
        Button importButton = primaryButton("＋  " + s("import"));
        importButton.setTextColor(Color.rgb(28, 31, 69));
        importButton.setBackground(background(Color.WHITE, 14, Color.WHITE));
        importButton.setOnClickListener(v -> showImportOptions());
        hero.addView(importButton, margin(-1, dp(50), dp(18), 0));
        page.addView(hero, margin(-1, -2, 0, dp(18)));
    }

    private void renderStatistics() {
        LinearLayout stats = panel();
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER_VERTICAL);
        stats.setPadding(dp(17), dp(13), dp(17), dp(13));

        LinearLayout countDetails = new LinearLayout(this);
        countDetails.setOrientation(LinearLayout.VERTICAL);
        gameCount = text("0", 25, palette.primaryText, true);
        countDetails.addView(gameCount);
        countDetails.addView(text(s("games_in_library"), 10, palette.secondaryText, true));
        stats.addView(countDetails, new LinearLayout.LayoutParams(0, -2, 1));

        TextView platform = text("PSP", 12, palette.accent, true);
        platform.setGravity(Gravity.CENTER);
        platform.setBackground(background(palette.accentSoft, 12, palette.border));
        stats.addView(platform, lp(dp(62), dp(42)));
        page.addView(stats, margin(-1, -2, 0, dp(22)));
    }

    private void renderLibraryHeader() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(text(s("library"), 18, palette.primaryText, true),
                new LinearLayout.LayoutParams(0, -2, 1));
        header.addView(text(s("local_library"), 9, palette.secondaryText, true));
        page.addView(header, margin(-1, -2, 0, dp(11)));

        searchField = new EditText(this);
        searchField.setSingleLine(true);
        searchField.setTextSize(14);
        searchField.setTextColor(palette.primaryText);
        searchField.setHintTextColor(palette.secondaryText);
        searchField.setHint(s("search"));
        searchField.setPadding(dp(16), 0, dp(16), 0);
        searchField.setBackground(background(palette.field, 15, palette.border));
        searchField.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        searchField.setCompoundDrawablePadding(dp(8));
        page.addView(searchField, margin(-1, dp(52), 0, dp(13)));
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence value, int start, int before, int count) { renderLibraryItems(); }
            @Override public void afterTextChanged(Editable value) { }
        });
    }

    private void renderFilters() {
        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(false);
        horizontal.setFillViewport(false);
        LinearLayout chips = row();
        String[][] values = {
                {"ALL", s("all")},
                {"FAVORITES", s("favorites")}
        };

        for (String[] item : values) {
            final String filter = item[0];
            Button chip = smallButton(item[1], filter.equals(activeFilter));
            chip.setOnClickListener(v -> {
                activeFilter = filter;
                for (int i = 0; i < chips.getChildCount(); i++) {
                    View child = chips.getChildAt(i);
                    if (child instanceof Button) {
                        styleChip((Button) child, filter.equals(child.getTag()));
                    }
                }
                renderLibraryItems();
            });
            chip.setTag(filter);
            LinearLayout.LayoutParams params = lp(-2, dp(38));
            if (isArabic()) params.leftMargin = dp(8); else params.rightMargin = dp(8);
            chips.addView(chip, params);
        }
        horizontal.addView(chips);
        page.addView(horizontal, margin(-1, dp(40), 0, dp(12)));
    }

    private void renderGameList() {
        gameList = new LinearLayout(this);
        gameList.setOrientation(LinearLayout.VERTICAL);
        page.addView(gameList);
        renderLibraryItems();
    }

    private void renderLibraryItems() {
        if (gameList == null) return;
        gameList.removeAllViews();

        String query = searchField == null || searchField.getText() == null
                ? "" : searchField.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<GameEntry> all = database.getAll();
        int totalPsp = 0;
        int shown = 0;

        for (GameEntry game : all) {
            if (!GameFileClassifier.PSP.equals(game.getPlatform())
                    || !GameFileClassifier.isSupportedPspExtension(game.getExtension())) {
                continue;
            }
            totalPsp++;
            if ("FAVORITES".equals(activeFilter) && !game.isFavorite()) continue;
            String name = game.getName() == null ? "" : game.getName();
            if (!query.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(query)) continue;
            shown++;
            gameList.addView(createGameCard(game), margin(-1, -2, 0, dp(10)));
        }

        if (gameCount != null) gameCount.setText(Integer.toString(totalPsp));
        if (shown == 0) {
            LinearLayout empty = panel();
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(22), dp(28), dp(22), dp(28));
            String title = totalPsp == 0 ? s("empty_title") : s("no_matches_title");
            String body = totalPsp == 0 ? s("empty_body") : s("no_matches_body");
            empty.addView(text(title, 16, palette.primaryText, true));
            TextView explanation = text(body, 12, palette.secondaryText, false);
            explanation.setGravity(Gravity.CENTER);
            explanation.setLineSpacing(dp(3), 1.0f);
            empty.addView(explanation, margin(-1, -2, dp(9), 0));
            final boolean libraryIsEmpty = totalPsp == 0;
            Button importButton = smallButton(libraryIsEmpty ? s("import") : s("clear_search"), true);
            importButton.setOnClickListener(v -> {
                if (libraryIsEmpty) showImportOptions();
                else {
                    activeFilter = "ALL";
                    if (searchField != null) searchField.setText("");
                    renderScreen();
                }
            });
            empty.addView(importButton, margin(-2, dp(42), dp(12), 0));
            gameList.addView(empty);
        }
    }

    private LinearLayout createGameCard(GameEntry game) {
        LinearLayout card = panel();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(13), dp(12), dp(13));

        TextView icon = text("PSP", 12, Color.WHITE, true);
        icon.setGravity(Gravity.CENTER);
        GradientDrawable iconBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(105, 91, 248), Color.rgb(52, 59, 142)});
        iconBackground.setCornerRadius(dp(16));
        icon.setBackground(iconBackground);
        card.addView(icon, lp(dp(54), dp(58)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, dp(6), 0);
        String displayName = game.getName() == null || game.getName().trim().isEmpty()
                ? s("unknown_game") : game.getName();
        TextView title = text(displayName, 14, palette.primaryText, true);
        title.setMaxLines(2);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        details.addView(title);
        String meta = game.getExtension().toUpperCase(Locale.ROOT)
                + (game.getSizeBytes() > 0 ? "  •  " + formatSize(game.getSizeBytes()) : "");
        details.addView(text(meta, 10, palette.secondaryText, false), margin(-1, -2, dp(5), 0));
        card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));

        Button favorite = smallButton(game.isFavorite() ? "★" : "☆", false);
        favorite.setContentDescription(s(game.isFavorite() ? "unfavorite" : "favorite"));
        favorite.setOnClickListener(v -> {
            database.setFavorite(game.getId(), !game.isFavorite());
            renderLibraryItems();
        });
        card.addView(favorite, lp(dp(40), dp(42)));

        Button play = smallButton("▶", true);
        play.setContentDescription(s("play"));
        play.setOnClickListener(v -> launchPspGame(game));
        card.addView(play, margin(dp(42), dp(42), 0, 0));
        card.setOnClickListener(v -> showGameDetails(game));
        card.setFocusable(true);
        return card;
    }

    private void renderFooter() {
        TextView footer = text("NEXUS  •  " + (isArabic() ? "مكتبتك محلياً" : "YOUR LOCAL COLLECTION"),
                9, palette.secondaryText, true);
        footer.setGravity(Gravity.CENTER);
        footer.setLetterSpacing(0.08f);
        page.addView(footer, margin(-1, dp(28), dp(8), 0));
    }

    private void showImportOptions() {
        openFilePicker();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(intent, REQUEST_IMPORT_FILES);
        } catch (Exception error) {
            toast(s("file_picker_error"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMPORT_FILES || resultCode != RESULT_OK || data == null) return;

        pendingFiles.clear();
        pendingFileIndex = 0;
        pendingGrantFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                if (uri != null) pendingFiles.add(uri);
            }
        } else if (data.getData() != null) {
            pendingFiles.add(data.getData());
        }
        if (pendingFiles.isEmpty()) {
            toast(s("no_files_selected"));
            return;
        }
        processNextImportedFile();
    }

    private void processNextImportedFile() {
        int imported = 0;
        int duplicates = 0;
        int rejected = 0;

        while (pendingFileIndex < pendingFiles.size()) {
            Uri uri = pendingFiles.get(pendingFileIndex++);
            String name = queryDisplayName(uri);
            String extension = GameFileClassifier.extensionOf(name);

            if (!GameFileClassifier.isSupportedPspExtension(extension)) {
                rejected++;
                continue;
            }
            if (!persistReadPermission(uri, pendingGrantFlags)) {
                rejected++;
                continue;
            }

            int result = storeGame(uri, name, GameFileClassifier.PSP, extension, querySize(uri));
            if (result > 0) imported++;
            else if (result == 0) duplicates++;
            else rejected++;
        }

        pendingFiles.clear();
        pendingFileIndex = 0;
        pendingGrantFlags = 0;
        renderLibraryItems();

        if (imported + duplicates + rejected > 0) {
            toast(String.format(Locale.getDefault(), s("import_summary"),
                    imported, duplicates, rejected));
        }
    }

    private boolean persistReadPermission(Uri uri, int grantFlags) {
        int required = Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
        if (uri == null || !"content".equals(uri.getScheme()) || (grantFlags & required) != required) {
            return false;
        }
        try {
            getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return true;
        } catch (SecurityException | IllegalArgumentException error) {
            return false;
        }
    }

    private int storeGame(Uri uri, String name, String platform, String extension, long size) {
        if (uri == null || !"content".equals(uri.getScheme())
                || !GameFileClassifier.PSP.equals(platform)
                || !GameFileClassifier.isSupportedPspExtension(extension)) {
            return -1;
        }
        String safeName = name == null || name.trim().isEmpty() ? "PSP game." + extension : name.trim();
        try {
            return database.insert(safeName, GameFileClassifier.PSP, uri.toString(),
                    extension, size, System.currentTimeMillis()) ? 1 : 0;
        } catch (RuntimeException error) {
            return -1;
        }
    }

    private String queryDisplayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String value = cursor.getString(index);
                    if (value != null && !value.trim().isEmpty()) return value;
                }
            }
        } catch (Exception ignored) {
            // Use the URI as a display-name fallback.
        }
        String fallback = uri.getLastPathSegment();
        return fallback == null ? "Imported game" : fallback;
    }

    private long querySize(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.SIZE}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (index >= 0 && !cursor.isNull(index)) return Math.max(0L, cursor.getLong(index));
            }
        } catch (Exception ignored) {
            // Size is optional metadata.
        }
        return 0L;
    }

    private void showGameDetails(GameEntry game) {
        StringBuilder message = new StringBuilder();
        message.append(s("platform")).append(": PSP").append('\n');
        message.append(s("file_type")).append(": ")
                .append(game.getExtension().toUpperCase(Locale.ROOT)).append('\n');
        if (game.getSizeBytes() > 0) {
            message.append(s("size")).append(": ").append(formatSize(game.getSizeBytes())).append('\n');
        }
        message.append('\n').append(s("psp_ready_body"));
        new AlertDialog.Builder(this)
                .setTitle(s("details") + " — " + game.getName())
                .setMessage(message.toString())
                .setPositiveButton(s("play"), (dialog, which) -> launchPspGame(game))
                .setNeutralButton(s(game.isFavorite() ? "unfavorite" : "favorite"), (dialog, which) -> {
                    database.setFavorite(game.getId(), !game.isFavorite());
                    renderLibraryItems();
                })
                .setNegativeButton(s("remove"), (dialog, which) -> confirmRemove(game))
                .show();
    }

    private void launchPspGame(GameEntry game) {
        try {
            PspEngine.launch(this, Uri.parse(game.getUri()), game.getName());
        } catch (PspEngine.EngineNotInstalledException error) {
            showPspInstallDialog();
        } catch (Exception error) {
            toast(s("launch_error"));
        }
    }

    private void showPspInstallDialog() {
        new AlertDialog.Builder(this)
                .setTitle(s("psp_engine_missing_title"))
                .setMessage(s("psp_engine_missing_body"))
                .setPositiveButton(s("install_engine"), (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.ppsspp.org/download/")));
                    } catch (Exception ignored) {
                        toast(s("launch_error"));
                    }
                })
                .setNegativeButton(s("cancel"), null)
                .show();
    }

    private void confirmRemove(GameEntry game) {
        new AlertDialog.Builder(this)
                .setTitle(s("remove"))
                .setMessage(game.getName())
                .setNegativeButton(s("cancel"), null)
                .setPositiveButton(s("remove"), (dialog, which) -> {
                    if (database.delete(game.getId())) {
                        try {
                            getContentResolver().releasePersistableUriPermission(
                                    Uri.parse(game.getUri()), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException | IllegalArgumentException ignored) {
                            // The provider may have already revoked the grant.
                        }
                        renderLibraryItems();
                    }
                })
                .show();
    }

    private void showSettings() {
        String[] options = {
                s("language") + ": " + s(isArabic() ? "arabic" : "english"),
                s("theme") + ": " + s(settings.isDarkTheme() ? "dark" : "light"),
                s("about")
        };
        new AlertDialog.Builder(this)
                .setTitle(s("settings"))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        settings.setArabic(!settings.isArabic());
                        renderScreen();
                    } else if (which == 1) {
                        settings.setDarkTheme(!settings.isDarkTheme());
                        recreate();
                    } else if (which == 2) {
                        showAbout();
                    }
                })
                .setNegativeButton(s("cancel"), null)
                .show();
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle(s("about"))
                .setMessage(s("about_body"))
                .setPositiveButton(s("ok"), null)
                .show();
    }

    private LinearLayout panel() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(background(palette.surface, 18, palette.border));
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return layout;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        view.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private Button primaryButton(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(48));
        button.setBackground(background(palette.accent, 14, palette.accent));
        return button;
    }

    private Button smallButton(String value, boolean selected) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setTextSize(11);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(40));
        button.setPadding(dp(12), 0, dp(12), 0);
        button.setBackground(background(selected ? palette.accent : palette.surface, 13, palette.border));
        return button;
    }

    private void styleChip(Button button, boolean selected) {
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setBackground(background(selected ? palette.accent : palette.surface, 13, palette.border));
    }

    private GradientDrawable background(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private LinearLayout.LayoutParams margin(int width, int height, int top, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.topMargin = top;
        params.bottomMargin = bottom;
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024L) return bytes + " B";
        if (bytes < 1024L * 1024L) return String.format(Locale.US, "%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024L * 1024L) return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
