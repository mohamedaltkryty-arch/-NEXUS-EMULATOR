package com.nexus.emulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
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
    private LinearLayout statistics;
    private EditText searchField;
    private TextView runtimeDot;
    private TextView runtimeState;
    private TextView runtimeDescription;
    private Button runtimeAction;
    private Button allFilter;
    private Button favoritesFilter;
    private Button clearSearch;
    private boolean favoritesOnly;
    private Palette palette;
    private final List<Uri> pendingFiles = new ArrayList<>();

    private static final class Palette {
        final int background;
        final int surface;
        final int surfaceRaised;
        final int border;
        final int primaryText;
        final int secondaryText;
        final int accent;
        final int accentSoft;
        final int success;
        final int warning;
        final int field;
        final int heroStart;
        final int heroEnd;

        Palette(boolean dark) {
            if (dark) {
                background = Color.rgb(9, 14, 26);
                surface = Color.rgb(17, 26, 43);
                surfaceRaised = Color.rgb(23, 35, 56);
                border = Color.rgb(37, 51, 75);
                primaryText = Color.rgb(244, 247, 253);
                secondaryText = Color.rgb(153, 169, 194);
                accent = Color.rgb(126, 102, 255);
                accentSoft = Color.rgb(42, 39, 79);
                success = Color.rgb(63, 218, 178);
                warning = Color.rgb(255, 183, 92);
                field = Color.rgb(14, 22, 37);
                heroStart = Color.rgb(34, 39, 82);
                heroEnd = Color.rgb(18, 51, 76);
            } else {
                background = Color.rgb(243, 246, 252);
                surface = Color.WHITE;
                surfaceRaised = Color.rgb(249, 250, 255);
                border = Color.rgb(225, 231, 242);
                primaryText = Color.rgb(22, 32, 52);
                secondaryText = Color.rgb(98, 114, 139);
                accent = Color.rgb(102, 82, 231);
                accentSoft = Color.rgb(238, 235, 255);
                success = Color.rgb(0, 145, 119);
                warning = Color.rgb(179, 105, 20);
                field = Color.rgb(237, 241, 248);
                heroStart = Color.rgb(47, 57, 128);
                heroEnd = Color.rgb(24, 100, 129);
            }
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        boolean dark = getSharedPreferences("nexus_settings", MODE_PRIVATE)
                .getBoolean("theme_dark", true);
        setTheme(dark ? R.style.AppThemeDark : R.style.AppThemeLight);
        super.onCreate(state);
        settings = new SettingsStore(this);
        database = new GameDatabase(this);
        renderScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRuntimeCard();
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

    private void renderScreen() {
        palette = new Palette(settings.isDarkTheme());

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(view.getPaddingLeft(), insets.getSystemWindowInsetTop(),
                    view.getPaddingRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        scroll.setBackgroundColor(palette.background);

        page = column();
        statistics = null;
        page.setPadding(dp(20), dp(17), dp(20), dp(28));
        page.setBackgroundColor(palette.background);
        scroll.addView(page);

        renderHeader();
        renderHero();
        renderRuntime();
        renderStatistics();
        renderLibraryHeader();
        renderSearch();
        renderFilters();
        renderGameList();
        renderFooter();

        setContentView(scroll);
        applySystemBars();
    }

    private void renderHeader() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView mark = text("N", 21, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(gradientBackground(palette.accent, blend(palette.accent, Color.BLACK, 0.28f), 13));
        header.addView(mark, margin(dp(42), dp(42), 0, 0));

        LinearLayout brand = column();
        brand.setPadding(isArabic() ? dp(11) : dp(13), 0, isArabic() ? dp(13) : dp(11), 0);
        TextView name = text("NEXUS", 21, palette.primaryText, true);
        name.setLetterSpacing(0.12f);
        brand.addView(name);
        brand.addView(text(s("tagline"), 10, palette.secondaryText, false));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        Button settingsButton = iconButton("⚙", s("settings"));
        settingsButton.setOnClickListener(v -> showSettings());
        header.addView(settingsButton, lp(dp(48), dp(48)));
        page.addView(header, margin(-1, -2, 0, dp(18)));
    }

    private void renderHero() {
        LinearLayout hero = column();
        hero.setPadding(dp(20), dp(20), dp(20), dp(19));
        hero.setBackground(gradientBackground(palette.heroStart, palette.heroEnd, 24));

        TextView kicker = text(s("hero_kicker"), 10, Color.rgb(197, 193, 255), true);
        kicker.setLetterSpacing(0.08f);
        hero.addView(kicker);

        TextView headline = text(s("hero_title"), 25, Color.WHITE, true);
        headline.setMaxLines(2);
        headline.setLineSpacing(dp(2), 1.0f);
        hero.addView(headline, margin(-1, -2, dp(13), dp(7)));

        TextView description = text(s("hero_body"), 13, Color.rgb(220, 228, 245), false);
        description.setMaxLines(3);
        hero.addView(description);

        Button importButton = primaryButton("＋  " + s("import"));
        importButton.setOnClickListener(v -> openFilePicker());
        hero.addView(importButton, margin(-1, dp(52), dp(18), 0));
        page.addView(hero, margin(-1, -2, 0, dp(13)));
    }

    private void renderRuntime() {
        LinearLayout card = panel();
        card.setPadding(dp(14), dp(13), dp(12), dp(13));

        LinearLayout content = row();
        content.setGravity(Gravity.CENTER_VERTICAL);

        runtimeDot = text("●", 14, palette.success, true);
        runtimeDot.setGravity(Gravity.CENTER);
        content.addView(runtimeDot, lp(dp(26), dp(32)));

        LinearLayout labels = column();
        labels.addView(text(s("runtime_title"), 11, palette.secondaryText, true));
        runtimeState = text("", 13, palette.success, true);
        labels.addView(runtimeState, margin(-1, -2, dp(3), 0));
        content.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));

        runtimeAction = smallButton(s("download"), false);
        runtimeAction.setOnClickListener(v -> openPpssppDownload());
        content.addView(runtimeAction, lp(-2, dp(44)));

        card.addView(content);
        runtimeDescription = text(s("runtime_ready_body"), 11, palette.secondaryText, false);
        card.addView(runtimeDescription, margin(-1, -2, dp(7), 0));
        page.addView(card, margin(-1, -2, 0, dp(15)));
        updateRuntimeCard();
    }

    private void updateRuntimeCard() {
        if (runtimeState == null || runtimeDot == null || runtimeAction == null) return;
        boolean installed = PspEngine.isInstalled(this);
        runtimeState.setText(s(installed ? "runtime_ready" : "runtime_missing"));
        runtimeState.setTextColor(installed ? palette.success : palette.warning);
        runtimeDot.setTextColor(installed ? palette.success : palette.warning);
        runtimeDescription.setText(s(installed ? "runtime_ready_body" : "runtime_missing_body"));
        runtimeAction.setVisibility(installed ? View.GONE : View.VISIBLE);
    }

    private void renderStatistics() {
        List<GameEntry> games = database.getAll();
        int favorites = 0;
        for (GameEntry game : games) {
            if (game.isFavorite()) favorites++;
        }

        if (statistics != null) page.removeView(statistics);
        LinearLayout stats = row();
        statistics = stats;
        stats.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout totalCard = statCard(Integer.toString(games.size()), s("total_games"), "▣");
        LinearLayout favoriteCard = statCard(Integer.toString(favorites), s("favorite_games"), "★");
        stats.addView(totalCard, new LinearLayout.LayoutParams(0, dp(78), 1));
        LinearLayout.LayoutParams favoriteParams = new LinearLayout.LayoutParams(0, dp(78), 1);
        favoriteParams.leftMargin = dp(9);
        stats.addView(favoriteCard, favoriteParams);
        page.addView(stats, 3, margin(-1, -2, 0, dp(21)));
    }

    private LinearLayout statCard(String value, String caption, String glyph) {
        LinearLayout card = panel();
        card.setPadding(dp(14), dp(11), dp(14), dp(10));

        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView count = text(value, 24, palette.primaryText, true);
        top.addView(count, new LinearLayout.LayoutParams(0, -2, 1));
        TextView icon = text(glyph, 16, palette.accent, true);
        icon.setGravity(Gravity.CENTER);
        top.addView(icon, lp(dp(25), dp(28)));
        card.addView(top);
        card.addView(text(caption, 10, palette.secondaryText, true));
        return card;
    }

    private void renderLibraryHeader() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text(s("library"), 19, palette.primaryText, true);
        header.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView local = text(s("on_device"), 9, palette.secondaryText, true);
        local.setLetterSpacing(0.04f);
        header.addView(local);
        page.addView(header, margin(-1, -2, 0, dp(10)));
    }

    private void renderSearch() {
        LinearLayout search = row();
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setPadding(dp(13), 0, dp(8), 0);
        search.setBackground(background(palette.field, 15, palette.border));

        TextView searchIcon = text("⌕", 24, palette.secondaryText, false);
        search.addView(searchIcon, lp(dp(30), dp(50)));

        searchField = new EditText(this);
        searchField.setSingleLine(true);
        searchField.setTextSize(14);
        searchField.setTextColor(palette.primaryText);
        searchField.setHintTextColor(palette.secondaryText);
        searchField.setHint(s("search"));
        searchField.setPadding(dp(4), 0, dp(4), 0);
        searchField.setBackgroundColor(Color.TRANSPARENT);
        searchField.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        searchField.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        search.addView(searchField, new LinearLayout.LayoutParams(0, dp(52), 1));

        clearSearch = iconButton("×", s("clear_search"));
        clearSearch.setVisibility(View.GONE);
        clearSearch.setOnClickListener(v -> searchField.setText(""));
        search.addView(clearSearch, lp(dp(40), dp(42)));

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence value, int start, int before, int count) {
                if (clearSearch != null) {
                    clearSearch.setVisibility(value.length() == 0 ? View.GONE : View.VISIBLE);
                }
                renderLibraryItems();
            }
            @Override public void afterTextChanged(Editable value) { }
        });

        page.addView(search, margin(-1, dp(52), 0, dp(11)));
    }

    private void renderFilters() {
        LinearLayout filters = row();
        allFilter = filterButton(s("all"));
        favoritesFilter = filterButton("★  " + s("favorites_filter"));
        allFilter.setOnClickListener(v -> {
            favoritesOnly = false;
            updateFilterStyle();
            renderLibraryItems();
        });
        favoritesFilter.setOnClickListener(v -> {
            favoritesOnly = true;
            updateFilterStyle();
            renderLibraryItems();
        });
        filters.addView(allFilter, lp(-2, dp(43)));
        LinearLayout.LayoutParams favoriteParams = lp(-2, dp(43));
        favoriteParams.leftMargin = dp(8);
        filters.addView(favoritesFilter, favoriteParams);
        page.addView(filters, margin(-1, -2, 0, dp(12)));
        updateFilterStyle();
    }

    private Button filterButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(12);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(43));
        button.setPadding(dp(17), 0, dp(17), 0);
        return button;
    }

    private void updateFilterStyle() {
        if (allFilter == null || favoritesFilter == null) return;
        styleFilter(allFilter, !favoritesOnly);
        styleFilter(favoritesFilter, favoritesOnly);
    }

    private void styleFilter(Button button, boolean selected) {
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setBackground(background(selected ? palette.accent : palette.surface, 14,
                selected ? palette.accent : palette.border));
    }

    private void renderGameList() {
        gameList = column();
        page.addView(gameList);
        renderLibraryItems();
    }

    private void renderLibraryItems() {
        if (gameList == null) return;
        gameList.removeAllViews();

        String query = searchField == null || searchField.getText() == null
                ? "" : searchField.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<GameEntry> all = database.getAll();
        int shown = 0;

        for (GameEntry game : all) {
            if (favoritesOnly && !game.isFavorite()) continue;
            String name = game.getName() == null ? "" : game.getName();
            if (!query.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(query)) continue;
            shown++;
            gameList.addView(createGameCard(game), margin(-1, -2, 0, dp(9)));
        }

        if (shown == 0) {
            boolean firstRun = all.isEmpty();
            gameList.addView(createEmptyState(firstRun, query));
        }
    }

    private LinearLayout createEmptyState(boolean firstRun, String query) {
        LinearLayout empty = panel();
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(22), dp(24), dp(22), dp(25));

        TextView symbol = text(firstRun ? "＋" : "⌕", 29, palette.accent, true);
        symbol.setGravity(Gravity.CENTER);
        symbol.setBackground(background(palette.accentSoft, 18, palette.border));
        empty.addView(symbol, lp(dp(58), dp(58)));

        empty.addView(text(s(firstRun ? "empty_title" : "no_results_title"), 16,
                palette.primaryText, true), margin(-1, -2, dp(14), dp(7)));
        TextView description = text(s(firstRun ? "empty_body" : "no_results_body"),
                12, palette.secondaryText, false);
        description.setGravity(Gravity.CENTER);
        description.setMaxLines(3);
        empty.addView(description, margin(-1, -2, 0, dp(16)));

        Button action;
        if (firstRun) {
            action = primaryButton(s("import"));
            action.setOnClickListener(v -> openFilePicker());
        } else {
            action = smallButton(s(favoritesOnly ? "show_all" : "clear_search"), true);
            action.setOnClickListener(v -> {
                if (favoritesOnly) {
                    favoritesOnly = false;
                    updateFilterStyle();
                    renderLibraryItems();
                } else if (searchField != null) {
                    searchField.setText("");
                }
            });
        }
        empty.addView(action, margin(-1, dp(48), 0, 0));
        return empty;
    }

    private LinearLayout createGameCard(GameEntry game) {
        LinearLayout card = panel();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(11), dp(11), dp(11), dp(11));

        TextView cover = text("PSP", 11, palette.accent, true);
        cover.setGravity(Gravity.CENTER);
        cover.setBackground(gradientBackground(palette.accentSoft,
                blend(palette.accentSoft, palette.surface, 0.45f), 14));
        card.addView(cover, lp(dp(58), dp(62)));

        LinearLayout details = column();
        details.setPadding(dp(12), 0, dp(7), 0);
        TextView title = text(game.getName(), 13, palette.primaryText, true);
        title.setMaxLines(2);
        title.setEllipsize(TextUtils.TruncateAt.END);
        details.addView(title);
        String metadata = "PSP  •  " + game.getExtension().toUpperCase(Locale.ROOT);
        if (game.getSizeBytes() > 0) metadata += "  •  " + formatSize(game.getSizeBytes());
        details.addView(text(metadata, 10, palette.secondaryText, false), margin(-1, -2, dp(6), 0));
        card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));

        Button favorite = iconButton(game.isFavorite() ? "★" : "☆",
                s(game.isFavorite() ? "unfavorite" : "favorite"));
        favorite.setTextColor(game.isFavorite() ? palette.accent : palette.secondaryText);
        favorite.setOnClickListener(v -> {
            database.setFavorite(game.getId(), !game.isFavorite());
            refreshLibrary();
        });
        card.addView(favorite, lp(dp(42), dp(46)));

        Button play = primaryIconButton("▶", s("play"));
        play.setOnClickListener(v -> launchPspGame(game));
        card.addView(play, margin(dp(47), dp(46), 0, 0));

        card.setOnClickListener(v -> showGameDetails(game));
        return card;
    }

    private void refreshLibrary() {
        if (page == null) return;
        renderStatistics();
        renderLibraryItems();
    }

    private void renderFooter() {
        TextView footer = text(s("footer"), 9, palette.secondaryText, false);
        footer.setGravity(Gravity.CENTER);
        page.addView(footer, margin(-1, -2, dp(25), 0));
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(intent, REQUEST_IMPORT_FILES);
        } catch (ActivityNotFoundException error) {
            toast(s("import_error"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMPORT_FILES || resultCode != RESULT_OK || data == null) return;

        pendingFiles.clear();
        if (data.getClipData() != null) {
            for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                Uri uri = data.getClipData().getItemAt(index).getUri();
                if (uri != null) pendingFiles.add(uri);
            }
        } else if (data.getData() != null) {
            pendingFiles.add(data.getData());
        }

        processSelectedFiles(data.getFlags());
    }

    private void processSelectedFiles(int resultFlags) {
        int imported = 0;
        int duplicates = 0;
        int unsupported = 0;
        int permissionFailures = 0;

        for (Uri uri : pendingFiles) {
            String name = queryDisplayName(uri);
            String extension = GameFileClassifier.extensionOf(name);

            if (!GameFileClassifier.isSupportedPspExtension(extension)) {
                unsupported++;
                continue;
            }

            if (!persistReadPermission(uri, resultFlags)) {
                permissionFailures++;
                continue;
            }

            try {
                boolean inserted = database.insert(
                        name,
                        GameFileClassifier.PSP,
                        uri.toString(),
                        extension,
                        querySize(uri),
                        System.currentTimeMillis()
                );
                if (inserted) imported++;
                else duplicates++;
            } catch (RuntimeException error) {
                releaseReadPermission(uri);
                permissionFailures++;
            }
        }

        pendingFiles.clear();
        refreshLibrary();

        if (imported > 0) {
            if (imported == 1) {
                toast(s("imported"));
            } else {
                toast(s("imported_many") + " " + imported + " " + s("games_suffix"));
            }
        } else if (duplicates > 0) {
            toast(s("duplicate"));
        } else if (unsupported > 0) {
            toast(s("unsupported"));
        }

        if (permissionFailures > 0) toast(s("permission_error"));
    }

    private boolean persistReadPermission(Uri uri, int resultFlags) {
        int readFlag = resultFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (readFlag == 0 || uri == null) return false;
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return true;
        } catch (SecurityException error) {
            return false;
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    private void releaseReadPermission(Uri uri) {
        if (uri == null) return;
        try {
            getContentResolver().releasePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException | IllegalArgumentException ignored) {
            // The provider may have already revoked the grant.
        }
    }

    private String queryDisplayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(
                uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String value = cursor.getString(index);
                    if (value != null && !value.trim().isEmpty()) return value.trim();
                }
            }
        } catch (Exception ignored) {
            // Fall back to the URI's last path segment.
        }
        String fallback = uri == null ? null : uri.getLastPathSegment();
        return fallback == null || fallback.trim().isEmpty() ? "PSP game" : fallback;
    }

    private long querySize(Uri uri) {
        try (Cursor cursor = getContentResolver().query(
                uri, new String[]{OpenableColumns.SIZE}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (index >= 0 && !cursor.isNull(index)) {
                    return Math.max(0L, cursor.getLong(index));
                }
            }
        } catch (Exception ignored) {
            // File size is optional metadata.
        }
        return 0L;
    }

    private void showGameDetails(GameEntry game) {
        StringBuilder message = new StringBuilder();
        message.append(s("platform")).append(": PSP\n");
        message.append(s("file_type")).append(": ")
                .append(game.getExtension().toUpperCase(Locale.ROOT)).append('\n');
        if (game.getSizeBytes() > 0) {
            message.append(s("size")).append(": ").append(formatSize(game.getSizeBytes())).append('\n');
        }
        message.append('\n').append(s("runtime_ready_body"));

        new AlertDialog.Builder(this)
                .setTitle(s("details") + " — " + game.getName())
                .setMessage(message.toString())
                .setPositiveButton(s("play"), (dialog, which) -> launchPspGame(game))
                .setNeutralButton(s(game.isFavorite() ? "unfavorite" : "favorite"), (dialog, which) -> {
                    database.setFavorite(game.getId(), !game.isFavorite());
                    refreshLibrary();
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
                .setPositiveButton(s("download"), (dialog, which) -> openPpssppDownload())
                .setNegativeButton(s("cancel"), null)
                .show();
    }

    private void openPpssppDownload() {
        try {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.ppsspp.org/download/")
            ));
        } catch (ActivityNotFoundException error) {
            toast(s("launch_error"));
        }
    }

    private void confirmRemove(GameEntry game) {
        new AlertDialog.Builder(this)
                .setTitle(s("remove"))
                .setMessage(s("confirm_remove"))
                .setNegativeButton(s("cancel"), null)
                .setPositiveButton(s("remove"), (dialog, which) -> {
                    boolean deleted = database.delete(game.getId());
                    if (deleted) releaseReadPermission(Uri.parse(game.getUri()));
                    refreshLibrary();
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
        LinearLayout layout = column();
        layout.setBackground(background(palette.surface, 18, palette.border));
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return layout;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
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
        button.setPadding(dp(17), 0, dp(17), 0);
        button.setBackground(gradientBackground(palette.accent,
                blend(palette.accent, Color.BLACK, 0.18f), 15));
        return button;
    }

    private Button smallButton(String value, boolean selected) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setTextSize(11);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(44));
        button.setPadding(dp(13), 0, dp(13), 0);
        button.setBackground(background(selected ? palette.accent : palette.surface,
                13, selected ? palette.accent : palette.border));
        return button;
    }

    private Button iconButton(String glyph, String description) {
        Button button = new Button(this);
        button.setText(glyph);
        button.setTextColor(palette.secondaryText);
        button.setTextSize(18);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(44));
        button.setPadding(dp(7), 0, dp(7), 0);
        button.setBackground(background(palette.surfaceRaised, 14, palette.border));
        button.setContentDescription(description);
        return button;
    }

    private Button primaryIconButton(String glyph, String description) {
        Button button = new Button(this);
        button.setText(glyph);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setMinHeight(dp(44));
        button.setPadding(dp(6), 0, dp(6), 0);
        button.setBackground(background(palette.accent, 14, palette.accent));
        button.setContentDescription(description);
        return button;
    }

    private void applySystemBars() {
        Window window = getWindow();
        boolean dark = settings.isDarkTheme();
        window.setStatusBarColor(palette.background);
        window.setNavigationBarColor(palette.background);
        int flags = dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private GradientDrawable background(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private GradientDrawable gradientBackground(int start, int end, int radius) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{start, end}
        );
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int blend(int first, int second, float ratio) {
        float inverse = 1.0f - ratio;
        int red = Math.round(Color.red(first) * inverse + Color.red(second) * ratio);
        int green = Math.round(Color.green(first) * inverse + Color.green(second) * ratio);
        int blue = Math.round(Color.blue(first) * inverse + Color.blue(second) * ratio);
        return Color.rgb(red, green, blue);
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
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
        }
        return String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
