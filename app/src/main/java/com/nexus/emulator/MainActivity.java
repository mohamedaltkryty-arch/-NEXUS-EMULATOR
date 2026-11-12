package com.nexus.emulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexus.emulator.catalog.GameFileClassifier;
import com.nexus.emulator.catalog.GameLibraryFilter;
import com.nexus.emulator.data.GameDatabase;
import com.nexus.emulator.data.SettingsStore;
import com.nexus.emulator.engine.PspEngine;
import com.nexus.emulator.model.GameEntry;
import com.nexus.emulator.security.UriGrantPolicy;
import com.nexus.emulator.ui.GameListAdapter;
import com.nexus.emulator.ui.NexusPalette;
import com.nexus.emulator.util.AppStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public final class MainActivity extends Activity {
    private static final int REQUEST_IMPORT_FILES = 4101;
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static volatile WeakReference<MainActivity> resumedActivity = new WeakReference<>(null);

    private GameDatabase database;
    private SettingsStore settings;
    private NexusPalette palette;
    private LinearLayout root;
    private ListView gameList;
    private GameListAdapter gameAdapter;
    private FrameLayout listFrame;
    private LinearLayout emptyState;
    private TextView emptyTitle;
    private TextView emptyBody;
    private Button emptyAction;
    private EditText searchField;
    private TextView gameCount;
    private Button allFilterButton;
    private Button favoritesFilterButton;
    private String activeFilter = "ALL";
    private String currentQuery = "";
    private final List<Uri> pendingFiles = new ArrayList<>();
    private final ExecutorService importExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "nexus-import-worker");
        thread.setDaemon(true);
        return thread;
    });
    private int pendingGrantFlags;

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
        resumedActivity = new WeakReference<>(this);
        if (database != null) {
            if (gameList != null) refreshLibraryItems();
            else renderScreen();
        }
    }

    @Override
    protected void onPause() {
        if (resumedActivity.get() == this) {
            resumedActivity = new WeakReference<>(null);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        importExecutor.shutdown();
        if (database != null) database.close();
        super.onDestroy();
    }

    private boolean isArabic() {
        return settings.isArabic();
    }

    private String s(String key) {
        return AppStrings.get(key, isArabic());
    }

    private Locale appLocale() {
        return isArabic() ? new Locale("ar") : Locale.ENGLISH;
    }

    @SuppressWarnings("deprecation")
    private void applySystemBars() {
        Window window = getWindow();
        boolean dark = settings.isDarkTheme();

        if (Build.VERSION.SDK_INT >= 35) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);

            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(dark ? 0 : lightBars, lightBars);
            }
            return;
        }

        int barColor = dark ? Color.rgb(11, 16, 32) : Color.rgb(244, 246, 251);
        window.setStatusBarColor(barColor);
        window.setNavigationBarColor(barColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(dark ? 0 : lightBars, lightBars);
            }
            return;
        }

        int flags = dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (!dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private void renderScreen() {
        palette = NexusPalette.from(settings.isDarkTheme());
        gameList = null;
        gameAdapter = null;
        listFrame = null;
        emptyState = null;
        emptyTitle = null;
        emptyBody = null;
        emptyAction = null;
        searchField = null;
        gameCount = null;
        allFilterButton = null;
        favoritesFilterButton = null;

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(isArabic() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        root.setBackgroundColor(palette.background);
        root.setPadding(dp(20), dp(12), dp(20), dp(12));

        if (Build.VERSION.SDK_INT >= 35) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                int types = WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout();
                android.graphics.Insets safe = insets.getInsets(types);
                view.setPadding(dp(20) + safe.left, dp(12) + safe.top,
                        dp(20) + safe.right, dp(12) + safe.bottom);
                return insets;
            });
        }

        renderHeader(root);
        List<GameEntry> games = database.getAllPsp();
        if (games.isEmpty()) {
            root.addView(createEmptyState(true),
                    new LinearLayout.LayoutParams(-1, 0, 1f));
        } else {
            renderCollectionSummary(root, games.size());
            renderSearch(root);
            renderFilters(root);
            renderListArea(root, games);
        }
        renderImportFooter(root);

        setContentView(root);
        applySystemBars();
    }

    private void renderHeader(LinearLayout container) {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout branding = new LinearLayout(this);
        branding.setOrientation(LinearLayout.VERTICAL);

        TextView name = text("NEXUS", 26, palette.primaryText, true);
        name.setLetterSpacing(0.12f);
        branding.addView(name);

        TextView caption = text(s("tagline"), 10, palette.secondaryText, true);
        caption.setLetterSpacing(0.04f);
        branding.addView(caption, lp(-2, dp(20)));
        header.addView(branding, new LinearLayout.LayoutParams(0, -2, 1f));

        Button settingsButton = smallButton("⚙", false);
        settingsButton.setContentDescription(s("settings"));
        settingsButton.setOnClickListener(view -> showSettings());
        header.addView(settingsButton, lp(dp(48), dp(48)));

        container.addView(header, margin(-1, -2, 0, dp(16)));
    }

    private void renderCollectionSummary(LinearLayout container, int count) {
        LinearLayout summary = panel();
        summary.setOrientation(LinearLayout.HORIZONTAL);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setPadding(dp(16), dp(12), dp(16), dp(12));

        LinearLayout countDetails = new LinearLayout(this);
        countDetails.setOrientation(LinearLayout.VERTICAL);

        gameCount = text(java.text.NumberFormat.getIntegerInstance(appLocale()).format(count),
                25, palette.primaryText, true);
        countDetails.addView(gameCount);
        countDetails.addView(text(s("games_in_library"), 10, palette.secondaryText, true));
        summary.addView(countDetails, new LinearLayout.LayoutParams(0, -2, 1f));

        TextView platform = text("PSP", 11, palette.accent, true);
        platform.setGravity(Gravity.CENTER);
        platform.setBackground(background(palette.accentSoft, 12, palette.border));
        summary.addView(platform, lp(dp(58), dp(40)));

        container.addView(summary, margin(-1, -2, 0, dp(16)));
    }

    private void renderSearch(LinearLayout container) {
        searchField = new EditText(this);
        searchField.setSingleLine(true);
        searchField.setTextSize(14);
        searchField.setTextColor(palette.primaryText);
        searchField.setHintTextColor(palette.secondaryText);
        searchField.setHint(s("search"));
        searchField.setPadding(dp(16), 0, dp(16), 0);
        searchField.setBackground(background(palette.field, 15, palette.border));
        searchField.setLayoutDirection(isArabic()
                ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        searchField.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchField.setText(currentQuery);
        container.addView(searchField, margin(-1, dp(52), 0, dp(12)));

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence value, int start, int before, int count) {
                currentQuery = value == null ? "" : value.toString();
                refreshLibraryItems();
            }
            @Override public void afterTextChanged(Editable value) { }
        });
    }

    private void renderFilters(LinearLayout container) {
        LinearLayout filters = row();
        allFilterButton = smallButton(s("all"), "ALL".equals(activeFilter));
        favoritesFilterButton = smallButton(s("favorites"), "FAVORITES".equals(activeFilter));

        allFilterButton.setOnClickListener(view -> {
            activeFilter = "ALL";
            styleFilterButtons();
            refreshLibraryItems();
        });
        favoritesFilterButton.setOnClickListener(view -> {
            activeFilter = "FAVORITES";
            styleFilterButtons();
            refreshLibraryItems();
        });

        LinearLayout.LayoutParams filterParams = new LinearLayout.LayoutParams(0, dp(46), 1f);
        filters.addView(allFilterButton, filterParams);
        LinearLayout.LayoutParams favoriteParams = new LinearLayout.LayoutParams(0, dp(46), 1f);
        if (isArabic()) favoriteParams.rightMargin = dp(8); else favoriteParams.leftMargin = dp(8);
        filters.addView(favoritesFilterButton, favoriteParams);
        container.addView(filters, margin(-1, -2, 0, dp(12)));
    }

    private void styleFilterButtons() {
        if (allFilterButton == null || favoritesFilterButton == null) return;
        styleFilter(allFilterButton, "ALL".equals(activeFilter));
        styleFilter(favoritesFilterButton, "FAVORITES".equals(activeFilter));
    }

    private void styleFilter(Button button, boolean selected) {
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setBackground(background(selected ? palette.accent : palette.surface,
                14, selected ? palette.accent : palette.border));
    }

    private void renderListArea(LinearLayout container, List<GameEntry> initialGames) {
        listFrame = new FrameLayout(this);
        gameList = new ListView(this);
        gameList.setDivider(new ColorDrawable(Color.TRANSPARENT));
        gameList.setDividerHeight(dp(10));
        gameList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gameList.setCacheColorHint(Color.TRANSPARENT);
        gameList.setClipToPadding(false);
        gameList.setPadding(0, 0, 0, dp(8));
        gameList.setBackgroundColor(Color.TRANSPARENT);

        gameAdapter = new GameListAdapter(this, palette, isArabic(),
                new GameListAdapter.Listener() {
                    @Override public void onGameClick(GameEntry game) {
                        showGameDetails(game);
                    }

                    @Override public void onFavoriteClick(GameEntry game) {
                        database.setFavorite(game.getId(), !game.isFavorite());
                        refreshLibraryItems();
                    }

                    @Override public void onPlayClick(GameEntry game) {
                        launchPspGame(game);
                    }
                });
        gameAdapter.setGames(initialGames);
        gameList.setAdapter(gameAdapter);
        gameList.setOnItemClickListener((parent, view, position, id) ->
                showGameDetails(gameAdapter.getItem(position)));
        listFrame.addView(gameList, new FrameLayout.LayoutParams(-1, -1));

        emptyState = createEmptyState(false);
        emptyState.setVisibility(View.GONE);
        listFrame.addView(emptyState, new FrameLayout.LayoutParams(-1, -1));

        container.addView(listFrame, new LinearLayout.LayoutParams(-1, 0, 1f));
        refreshLibraryItems();
    }

    private LinearLayout createEmptyState(boolean firstRun) {
        LinearLayout state = new LinearLayout(this);
        state.setOrientation(LinearLayout.VERTICAL);
        state.setGravity(Gravity.CENTER);
        state.setPadding(dp(24), dp(22), dp(24), dp(22));
        state.setBackgroundColor(Color.TRANSPARENT);

        TextView mark = text("PSP", 18, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        GradientDrawable markBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(139, 124, 255), Color.rgb(65, 79, 163)});
        markBackground.setCornerRadius(dp(24));
        mark.setBackground(markBackground);
        state.addView(mark, lp(dp(82), dp(82)));

        emptyTitle = text(firstRun ? s("empty_title") : s("no_matches_title"),
                18, palette.primaryText, true);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setMaxLines(2);
        state.addView(emptyTitle, margin(-1, -2, dp(20), dp(8)));

        emptyBody = text(firstRun ? s("empty_body") : s("no_matches_body"),
                12, palette.secondaryText, false);
        emptyBody.setGravity(Gravity.CENTER);
        emptyBody.setLineSpacing(dp(3), 1f);
        emptyBody.setMaxWidth(dp(320));
        state.addView(emptyBody, lp(-1, -2));

        emptyAction = smallButton(s("clear_search"), true);
        emptyAction.setOnClickListener(view -> {
            activeFilter = "ALL";
            currentQuery = "";
            if (searchField != null) searchField.setText("");
            styleFilterButtons();
            refreshLibraryItems();
        });
        if (firstRun) emptyAction.setVisibility(View.GONE);
        else state.addView(emptyAction, margin(-2, dp(48), dp(18), 0));

        return state;
    }

    private void renderImportFooter(LinearLayout container) {
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setPadding(0, dp(12), 0, 0);

        Button importButton = primaryButton("＋  " + s("import"));
        importButton.setOnClickListener(view -> showImportOptions());
        footer.addView(importButton, lp(-1, dp(56)));

        TextView formats = text(s("supported_formats"), 9, palette.secondaryText, true);
        formats.setGravity(Gravity.CENTER);
        formats.setLetterSpacing(0.06f);
        footer.addView(formats, margin(-1, dp(20), dp(5), 0));

        container.addView(footer, lp(-1, -2));
    }

    private void refreshLibraryItems() {
        if (gameList == null || gameAdapter == null || listFrame == null) return;

        List<GameEntry> all = database.getAllPsp();
        if (all.isEmpty()) {
            currentQuery = "";
            activeFilter = "ALL";
            renderScreen();
            return;
        }

        if (gameCount != null) {
            gameCount.setText(java.text.NumberFormat.getIntegerInstance(appLocale()).format(all.size()));
        }

        List<GameEntry> filtered = GameLibraryFilter.filter(
                all, currentQuery, "FAVORITES".equals(activeFilter));
        gameAdapter.setGames(filtered);

        boolean hasResults = !filtered.isEmpty();
        gameList.setVisibility(hasResults ? View.VISIBLE : View.GONE);
        emptyState.setVisibility(hasResults ? View.GONE : View.VISIBLE);

        if (!hasResults) {
            emptyTitle.setText(s("no_matches_title"));
            emptyBody.setText(s("no_matches_body"));
            emptyAction.setText(s("clear_search"));
            emptyAction.setVisibility(View.VISIBLE);
        }
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(intent, REQUEST_IMPORT_FILES);
        } catch (Exception error) {
            toast(s("file_picker_error"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resumedActivity = new WeakReference<>(this);
        if (requestCode != REQUEST_IMPORT_FILES || resultCode != RESULT_OK || data == null) return;

        pendingFiles.clear();
        pendingGrantFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
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
        if (pendingFiles.isEmpty()) return;

        final List<Uri> selectedFiles = new ArrayList<>(pendingFiles);
        final int grantFlags = pendingGrantFlags;
        final Locale resultLocale = appLocale();
        final String summaryPattern = s("import_summary");
        final String importingMessage = s("importing");

        pendingFiles.clear();
        pendingGrantFlags = 0;
        toast(importingMessage);

        final ContentResolver resolver = getApplicationContext().getContentResolver();
        final android.content.Context appContext = getApplicationContext();

        try {
            importExecutor.execute(() -> {
                int imported = 0;
                int duplicates = 0;
                int rejected = 0;
                int processed = 0;
                GameDatabase importDatabase = null;

                try {
                    importDatabase = new GameDatabase(appContext);
                    for (Uri uri : selectedFiles) {
                        processed++;
                        try {
                            String name = queryDisplayName(resolver, uri);
                            String extension = GameFileClassifier.extensionOf(name);

                            if (!GameFileClassifier.isSupportedPspExtension(extension)) {
                                rejected++;
                                continue;
                            }
                            if (importDatabase.containsUri(uri.toString())) {
                                duplicates++;
                                continue;
                            }
                            if (!persistReadPermission(resolver, uri, grantFlags)) {
                                rejected++;
                                continue;
                            }

                            int result = storeGame(importDatabase, uri, name,
                                    GameFileClassifier.PSP, extension, querySize(resolver, uri));
                            if (result > 0) {
                                imported++;
                            } else if (result == 0) {
                                duplicates++;
                            } else {
                                rejected++;
                                if (!importDatabase.containsUri(uri.toString())) {
                                    releaseReadPermission(resolver, uri);
                                }
                            }
                        } catch (RuntimeException error) {
                            rejected++;
                        }
                    }
                } catch (RuntimeException error) {
                    rejected += selectedFiles.size() - processed;
                } finally {
                    if (importDatabase != null) importDatabase.close();
                }

                final int importedCount = imported;
                final int duplicateCount = duplicates;
                final int rejectedCount = rejected;
                final String summary = String.format(resultLocale, summaryPattern,
                        importedCount, duplicateCount, rejectedCount);

                MAIN_HANDLER.post(() -> {
                    MainActivity active = resumedActivity.get();
                    if (active != null && !active.isFinishing() && !active.isDestroyed()) {
                        active.renderScreen();
                        active.toast(summary);
                    }
                });
            });
        } catch (RejectedExecutionException error) {
            toast(s("import_error"));
        }
    }

    private static boolean persistReadPermission(
            ContentResolver resolver, Uri uri, int grantFlags) {
        if (resolver == null || uri == null
                || !UriGrantPolicy.canPersistReadPermission(grantFlags, uri.getScheme())) {
            return false;
        }
        try {
            resolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return true;
        } catch (SecurityException | IllegalArgumentException error) {
            return false;
        }
    }

    private static void releaseReadPermission(ContentResolver resolver, Uri uri) {
        if (resolver == null || uri == null) return;
        try {
            resolver.releasePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException | IllegalArgumentException ignored) {
            // The provider may have already revoked the grant.
        }
    }

    private static int storeGame(GameDatabase targetDatabase, Uri uri, String name,
                                 String platform, String extension, long size) {
        if (targetDatabase == null || uri == null || !"content".equals(uri.getScheme())
                || !GameFileClassifier.PSP.equals(platform)
                || !GameFileClassifier.isSupportedPspExtension(extension)) {
            return -1;
        }
        String safeName = name == null || name.trim().isEmpty()
                ? "PSP game." + extension : name.trim();
        try {
            return targetDatabase.insert(safeName, GameFileClassifier.PSP, uri.toString(),
                    extension, size, System.currentTimeMillis()) ? 1 : 0;
        } catch (RuntimeException error) {
            return -1;
        }
    }

    private static String queryDisplayName(ContentResolver resolver, Uri uri) {
        if (resolver == null || uri == null) return "Imported game";
        try (Cursor cursor = resolver.query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String value = cursor.getString(index);
                    if (value != null && !value.trim().isEmpty()) return value;
                }
            }
        } catch (Exception ignored) {
            // Use the URI path as a display-name fallback.
        }
        String fallback = uri.getLastPathSegment();
        return fallback == null ? "Imported game" : Uri.decode(fallback);
    }

    private static long querySize(ContentResolver resolver, Uri uri) {
        if (resolver == null || uri == null) return 0L;
        try (Cursor cursor = resolver.query(uri,
                new String[]{OpenableColumns.SIZE}, null, null, null)) {
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
        if (game == null) return;
        StringBuilder message = new StringBuilder();
        message.append(s("platform")).append(": PSP").append('\n');
        message.append(s("file_type")).append(": ")
                .append(game.getExtension().toUpperCase(Locale.ROOT)).append('\n');
        if (game.getSizeBytes() > 0) {
            message.append(s("size")).append(": ").append(formatSize(game.getSizeBytes())).append('\n');
        }
        message.append('\n').append(s("psp_ready_body"));

        String title = game.getName() == null ? s("unknown_game") : game.getName();
        new AlertDialog.Builder(this)
                .setTitle(s("details") + " — " + title)
                .setMessage(message.toString())
                .setPositiveButton(s("play"), (dialog, which) -> launchPspGame(game))
                .setNeutralButton(s(game.isFavorite() ? "unfavorite" : "favorite"), (dialog, which) -> {
                    database.setFavorite(game.getId(), !game.isFavorite());
                    refreshLibraryItems();
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
                        releaseReadPermission(getContentResolver(), Uri.parse(game.getUri()));
                        if ("FAVORITES".equals(activeFilter)) refreshLibraryItems();
                        else renderScreen();
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
        layout.setElevation(dp(1));
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutDirection(isArabic()
                ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
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
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(56));
        button.setBackground(background(palette.accent, 16, palette.accent));
        return button;
    }

    private Button smallButton(String value, boolean selected) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(selected ? Color.WHITE : palette.secondaryText);
        button.setTextSize(12);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(48));
        button.setPadding(dp(12), 0, dp(12), 0);
        button.setBackground(background(selected ? palette.accent : palette.surface,
                14, selected ? palette.accent : palette.border));
        return button;
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
        if (bytes < 1024L * 1024L) {
            return String.format(Locale.US, "%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
        }
        return String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}