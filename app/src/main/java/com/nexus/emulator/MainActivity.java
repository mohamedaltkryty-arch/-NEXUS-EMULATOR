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
import android.provider.DocumentsContract;
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
    private static final int REQUEST_IMPORT_FOLDER = 4102;

    private GameDatabase database;
    private SettingsStore settings;
    private LinearLayout page;
    private LinearLayout gameList;
    private EditText searchField;
    private TextView gameCount;
    private String activeFilter = "ALL";
    private final List<Uri> pendingFiles = new ArrayList<>();
    private int pendingFileIndex;

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
                background = Color.rgb(10, 12, 19);
                surface = Color.rgb(21, 26, 38);
                border = Color.rgb(42, 49, 65);
                primaryText = Color.rgb(245, 247, 252);
                secondaryText = Color.rgb(148, 158, 178);
                accent = Color.rgb(133, 112, 255);
                accentSoft = Color.rgb(43, 36, 77);
                field = Color.rgb(25, 31, 45);
            } else {
                background = Color.rgb(245, 247, 251);
                surface = Color.WHITE;
                border = Color.rgb(224, 229, 239);
                primaryText = Color.rgb(24, 29, 43);
                secondaryText = Color.rgb(101, 111, 132);
                accent = Color.rgb(94, 75, 220);
                accentSoft = Color.rgb(238, 234, 255);
                field = Color.rgb(235, 239, 247);
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
        TextView name = text("NEXUS", 25, palette.primaryText, true);
        name.setLetterSpacing(0.08f);
        branding.addView(name);
        branding.addView(text(s("tagline"), 10, palette.secondaryText, false), lp(-2, dp(18)));
        header.addView(branding, new LinearLayout.LayoutParams(0, -2, 1));

        Button settingsButton = smallButton("⚙", false);
        settingsButton.setContentDescription(s("settings"));
        settingsButton.setOnClickListener(v -> showSettings());
        header.addView(settingsButton, lp(dp(48), dp(48)));
        page.addView(header, margin(-1, -2, 0, dp(22)));
    }

    private void renderHero() {
        LinearLayout hero = panel();
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView badge = text("NEXUS  /  GAME CENTER", 10, palette.accent, true);
        hero.addView(badge);
        hero.addView(text(s("hero_title"), 24, palette.primaryText, true), margin(-1, -2, dp(9), dp(6)));
        hero.addView(text(s("hero_body"), 13, palette.secondaryText, false));
        Button importButton = primaryButton("＋  " + s("import"));
        importButton.setOnClickListener(v -> showImportOptions());
        hero.addView(importButton, margin(-1, dp(50), dp(18), 0));
        page.addView(hero, margin(-1, -2, 0, dp(16)));
    }

    private void renderStatistics() {
        LinearLayout stats = row();
        LinearLayout countCard = panel();
        countCard.setPadding(dp(16), dp(14), dp(16), dp(14));
        gameCount = text(Integer.toString(database.count()), 26, palette.primaryText, true);
        countCard.addView(gameCount);
        countCard.addView(text(isArabic() ? "لعبة في مكتبتك" : "GAMES IN YOUR LIBRARY",
                10, palette.secondaryText, true));
        stats.addView(countCard, new LinearLayout.LayoutParams(-1, dp(84)));
        page.addView(stats, margin(-1, -2, 0, dp(18)));
    }

    private void renderLibraryHeader() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(text(s("library"), 16, palette.primaryText, true), new LinearLayout.LayoutParams(0, -2, 1));
        header.addView(text(isArabic() ? "محلياً على جهازك" : "ON THIS DEVICE", 9, palette.secondaryText, true));
        page.addView(header, margin(-1, -2, 0, dp(10)));

        searchField = new EditText(this);
        searchField.setSingleLine(true);
        searchField.setTextSize(14);
        searchField.setTextColor(palette.primaryText);
        searchField.setHintTextColor(palette.secondaryText);
        searchField.setHint(s("search"));
        searchField.setPadding(dp(15), 0, dp(15), 0);
        searchField.setBackground(background(palette.field, 14, palette.border));
        searchField.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        page.addView(searchField, margin(-1, dp(50), 0, dp(12)));
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence value, int start, int before, int count) { renderLibraryItems(); }
            @Override public void afterTextChanged(Editable value) { }
        });
    }

    private void renderFilters() {
        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(false);
        LinearLayout chips = row();
        String[][] values = {
                {"ALL", s("all")}, {"PC", s("pc")}, {"PSP", s("psp")},
                {"PS2", s("ps2")}, {"PS3", s("ps3")}
        };

        for (String[] item : values) {
            final String platform = item[0];
            Button chip = smallButton(item[1], platform.equals(activeFilter));
            chip.setOnClickListener(v -> {
                activeFilter = platform;
                for (int i = 0; i < chips.getChildCount(); i++) {
                    View child = chips.getChildAt(i);
                    if (child instanceof Button) {
                        String tag = (String) child.getTag();
                        styleChip((Button) child, platform.equals(tag));
                    }
                }
                renderLibraryItems();
            });
            chip.setTag(platform);
            LinearLayout.LayoutParams params = lp(-2, dp(38));
            if (isArabic()) params.leftMargin = dp(7); else params.rightMargin = dp(7);
            chips.addView(chip, params);
        }
        horizontal.addView(chips);
        page.addView(horizontal, margin(-1, dp(42), 0, dp(10)));
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
        int shown = 0;

        for (GameEntry game : all) {
            if (!"ALL".equals(activeFilter) && !activeFilter.equals(game.getPlatform())) continue;
            if (!query.isEmpty() && !game.getName().toLowerCase(Locale.ROOT).contains(query)) continue;
            shown++;
            gameList.addView(createGameCard(game), margin(-1, -2, 0, dp(9)));
        }

        if (shown == 0) {
            LinearLayout empty = panel();
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(22), dp(30), dp(22), dp(30));
            empty.addView(text(s("empty_title"), 15, palette.primaryText, true));
            empty.addView(text(s("empty_body"), 12, palette.secondaryText, false), margin(-1, -2, dp(8), 0));
            Button importButton = smallButton(s("import"), true);
            importButton.setOnClickListener(v -> showImportOptions());
            empty.addView(importButton, margin(-2, dp(42), dp(14), 0));
            gameList.addView(empty);
        }

        if (gameCount != null) gameCount.setText(Integer.toString(all.size()));
    }

    private LinearLayout createGameCard(GameEntry game) {
        LinearLayout card = panel();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView icon = text(game.getPlatform(), 11, palette.accent, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(background(palette.accentSoft, 13, palette.border));
        card.addView(icon, lp(dp(54), dp(54)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, dp(5), 0);
        details.addView(text(game.getName(), 14, palette.primaryText, true));
        String meta = game.getPlatform() + "  •  " + game.getExtension().toUpperCase(Locale.ROOT);
        details.addView(text(meta, 10, palette.secondaryText, false), margin(-1, -2, dp(5), 0));
        card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));

        Button favorite = smallButton(game.isFavorite() ? "★" : "☆", false);
        favorite.setContentDescription(s(game.isFavorite() ? "unfavorite" : "favorite"));
        favorite.setOnClickListener(v -> {
            database.setFavorite(game.getId(), !game.isFavorite());
            renderLibraryItems();
        });
        card.addView(favorite, lp(dp(44), dp(44)));
        if (GameFileClassifier.PSP.equals(game.getPlatform())) {
            Button play = smallButton("▶", true);
            play.setContentDescription(s("play"));
            play.setOnClickListener(v -> launchPspGame(game));
            card.addView(play, margin(dp(44), dp(44), 0, 0));
        }
        card.setOnClickListener(v -> showGameDetails(game));
        return card;
    }

    private void renderFooter() {
        TextView footer = text("NEXUS EMULATOR  •  " + (isArabic() ? "الإصدار 0.3" : "VERSION 0.3"), 9,
                palette.secondaryText, true);
        footer.setGravity(Gravity.CENTER);
        page.addView(footer, margin(-1, dp(30), dp(12), 0));
    }

    private void showImportOptions() {
        String[] options = {s("import_file"), s("import_folder")};
        new AlertDialog.Builder(this)
                .setTitle(s("import"))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openFilePicker();
                    else openFolderPicker();
                })
                .setNegativeButton(s("cancel"), null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMPORT_FILES);
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMPORT_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_IMPORT_FILES) {
            pendingFiles.clear();
            pendingFileIndex = 0;
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    pendingFiles.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                pendingFiles.add(data.getData());
            }
            processNextImportedFile();
        } else if (requestCode == REQUEST_IMPORT_FOLDER && data.getData() != null) {
            importSelectedFolder(data.getData(), data.getFlags());
        }
    }

    private void processNextImportedFile() {
        if (pendingFileIndex >= pendingFiles.size()) {
            pendingFiles.clear();
            renderLibraryItems();
            return;
        }

        Uri uri = pendingFiles.get(pendingFileIndex++);
        if (!persistReadPermission(uri)) {
            toast(s("permission_error"));
            processNextImportedFile();
            return;
        }

        String name = queryDisplayName(uri);
        String extension = GameFileClassifier.extensionOf(name);
        String detected = GameFileClassifier.classify(extension);

        if (GameFileClassifier.UNSUPPORTED.equals(detected)) {
            toast(s("unsupported") + " " + name);
            processNextImportedFile();
        } else if (GameFileClassifier.AMBIGUOUS_DISC.equals(detected)) {
            String[] platforms = GameFileClassifier.supportedDiscPlatforms().toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle(s("choose_platform") + " — " + name)
                    .setItems(platforms, (dialog, which) -> {
                        storeGame(uri, name, platforms[which], extension, querySize(uri));
                        processNextImportedFile();
                    })
                    .setOnCancelListener(dialog -> processNextImportedFile())
                    .show();
        } else {
            storeGame(uri, name, detected, extension, querySize(uri));
            processNextImportedFile();
        }
    }

    private void importSelectedFolder(Uri uri, int resultFlags) {
        int grantFlags = resultFlags & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if ((grantFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION) == 0) {
            toast(s("permission_error"));
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException error) {
            toast(s("permission_error"));
            return;
        }

        String folderName = queryTreeName(uri);
        String[] options = {s("pc_folder"), s("ps3_folder")};
        new AlertDialog.Builder(this)
                .setTitle(s("choose_folder_type"))
                .setItems(options, (dialog, which) -> {
                    String platform = which == 0 ? GameFileClassifier.PC : GameFileClassifier.PS3;
                    String extension = which == 0 ? "FOLDER" : "PS3_FOLDER";
                    storeGame(uri, folderName, platform, extension, 0L);
                    renderLibraryItems();
                    if (GameFileClassifier.PC.equals(platform)) toast(s("pc_folder_tip"));
                })
                .setNegativeButton(s("cancel"), null)
                .show();
    }

    private boolean persistReadPermission(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return true;
        } catch (SecurityException error) {
            return false;
        }
    }

    private void storeGame(Uri uri, String name, String platform, String extension, long size) {
        if (!GameFileClassifier.isSupportedPlatform(platform)) return;
        boolean inserted = database.insert(name, platform, uri.toString(), extension,
                size, System.currentTimeMillis());
        toast(inserted ? s("imported") : s("duplicate"));
        if (gameCount != null) renderLibraryItems();
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

    private String queryTreeName(Uri uri) {
        try {
            String documentId = DocumentsContract.getTreeDocumentId(uri);
            int colon = documentId.indexOf(':');
            String path = colon >= 0 ? documentId.substring(colon + 1) : documentId;
            int slash = path.lastIndexOf('/');
            String name = slash >= 0 ? path.substring(slash + 1) : path;
            if (!name.trim().isEmpty()) return name;
        } catch (Exception ignored) {
            // Use the document provider's display name.
        }
        return queryDisplayName(uri);
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
        message.append(s("platform")).append(": ").append(game.getPlatform()).append('\n');
        message.append(s("file_type")).append(": ")
                .append(game.getExtension().toUpperCase(Locale.ROOT)).append('\n');
        if (game.getSizeBytes() > 0) {
            message.append(s("size")).append(": ").append(formatSize(game.getSizeBytes())).append('\n');
        }
        boolean psp = GameFileClassifier.PSP.equals(game.getPlatform());
        message.append('\n').append(psp ? s("psp_ready_body") : s("coming_soon"));
        if (GameFileClassifier.PC.equals(game.getPlatform())) {
            message.append("\n\n").append(s("pc_folder_tip"));
        }

        new AlertDialog.Builder(this)
                .setTitle(s("details") + " — " + game.getName())
                .setMessage(message.toString())
                .setPositiveButton(psp ? s("play") : s("ok"), (dialog, which) -> {
                    if (psp) launchPspGame(game);
                })
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
                    database.delete(game.getId());
                    renderLibraryItems();
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
