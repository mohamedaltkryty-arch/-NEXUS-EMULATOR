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
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int REQUEST_IMPORT = 4101;
    private static final String PREFS = "nexus_library";
    private static final String KEY_GAMES = "games_json";

    private final ArrayList<GameEntry> games = new ArrayList<>();
    private LinearLayout gameList;
    private TextView gameCount;
    private String filter = "ALL";

    private static final class GameEntry {
        final String name;
        final String platform;
        final String uri;
        final String extension;

        GameEntry(String name, String platform, String uri, String extension) {
            this.name = name;
            this.platform = platform;
            this.uri = uri;
            this.extension = extension;
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(9, 11, 18));
        getWindow().setNavigationBarColor(Color.rgb(9, 11, 18));
        loadLibrary();
        renderScreen();
    }

    private void renderScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(20), dp(20), dp(30));
        page.setBackgroundColor(Color.rgb(9, 11, 18));
        scroll.addView(page);

        LinearLayout brand = new LinearLayout(this);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label("NEXUS", 27, "#FFFFFF", true);
        TextView suffix = label("  EMULATOR", 11, "#A99BFF", true);
        brand.addView(title);
        brand.addView(suffix);
        page.addView(brand);
        page.addView(label("YOUR GAMES. ONE PLACE.", 10, "#8790A2", true), margin(0, dp(7), 0, dp(22)));

        LinearLayout hero = panel();
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        hero.addView(label("UNIFIED GAME LIBRARY", 10, "#A99BFF", true));
        hero.addView(label("Play beyond limits.", 25, "#FFFFFF", true), margin(0, dp(8), 0, dp(5)));
        hero.addView(label("استورد ألعابك وأدر مكتبتك من مكان واحد.", 13, "#B7BDCB", false));
        Button importButton = button("+  IMPORT GAME FILES");
        importButton.setOnClickListener(v -> openPicker());
        hero.addView(importButton, margin(-1, dp(48), dp(17), 0));
        page.addView(hero, margin(-1, -2, 0, dp(18)));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout stat = panel();
        stat.setPadding(dp(14), dp(12), dp(14), dp(12));
        gameCount = label(Integer.toString(games.size()), 22, "#FFFFFF", true);
        stat.addView(gameCount);
        stat.addView(label("GAMES IN LIBRARY", 9, "#8992A5", true));
        stats.addView(stat, new LinearLayout.LayoutParams(0, dp(76), 1));
        LinearLayout status = panel();
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        status.addView(label("4", 22, "#FFFFFF", true));
        status.addView(label("SOURCE ENGINES", 9, "#8992A5", true));
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(0, dp(76), 1);
        statusParams.leftMargin = dp(10);
        stats.addView(status, statusParams);
        page.addView(stats, margin(-1, -2, 0, dp(22)));

        page.addView(label("GAME LIBRARY", 15, "#FFFFFF", true), margin(0, 0, 0, dp(10)));
        LinearLayout filters = new LinearLayout(this);
        filters.setOrientation(LinearLayout.HORIZONTAL);
        String[] names = {"ALL", "PC", "PSP", "PS2", "PS3"};
        for (String name : names) {
            TextView chip = label(name, 10, name.equals(filter) ? "#FFFFFF" : "#929BAD", true);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(13), dp(10), dp(13), dp(10));
            chip.setBackground(background(name.equals(filter) ? "#6048C8" : "#171B27", 18, "#00000000"));
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(-2, dp(36));
            chipParams.rightMargin = dp(7);
            filters.addView(chip, chipParams);
            chip.setOnClickListener(v -> {
                filter = name;
                renderLibrary();
            });
        }
        page.addView(filters, margin(-1, dp(40), 0, dp(12)));

        gameList = new LinearLayout(this);
        gameList.setOrientation(LinearLayout.VERTICAL);
        page.addView(gameList);
        renderLibrary();

        LinearLayout info = panel();
        info.setPadding(dp(14), dp(14), dp(14), dp(14));
        info.addView(label("ENGINE STATUS", 10, "#A99BFF", true));
        info.addView(label("مصادر المحركات موجودة في المستودع، لكن ربطها بالتطبيق لم يكتمل بعد. لن يبدأ تشغيل لعبة قبل دمج محركها واختباره.", 12, "#B7BDCB", false), margin(0, dp(8), 0, 0));
        page.addView(info, margin(-1, -2, 0, dp(18)));

        setContentView(scroll);
    }

    private void renderLibrary() {
        if (gameList == null) return;
        gameList.removeAllViews();
        int shown = 0;
        for (GameEntry game : games) {
            if (!"ALL".equals(filter) && !filter.equals(game.platform)) continue;
            shown++;

            LinearLayout card = panel();
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(13), dp(12), dp(13), dp(12));

            TextView icon = label(game.platform, 11, "#FFFFFF", true);
            icon.setGravity(Gravity.CENTER);
            icon.setBackground(background("#292344", 12, "#55458B"));
            card.addView(icon, new LinearLayout.LayoutParams(dp(54), dp(54)));

            LinearLayout details = new LinearLayout(this);
            details.setOrientation(LinearLayout.VERTICAL);
            details.setPadding(dp(12), 0, dp(5), 0);
            details.addView(label(game.name, 14, "#FFFFFF", true));
            details.addView(label(game.platform + "  •  " + game.extension.toUpperCase(Locale.ROOT), 10, "#8F98AA", false), margin(0, dp(5), 0, 0));
            card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
            card.setOnClickListener(v -> showGameInfo(game));
            gameList.addView(card, margin(-1, -2, 0, dp(9)));
        }

        if (shown == 0) {
            LinearLayout empty = panel();
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(20), dp(28), dp(20), dp(28));
            empty.addView(label("NO GAMES IN THIS FILTER", 12, "#FFFFFF", true));
            empty.addView(label("استورد ملفات ألعابك لتظهر هنا.", 12, "#8992A5", false), margin(0, dp(8), 0, 0));
            gameList.addView(empty, new LinearLayout.LayoutParams(-1, -2));
        }
        if (gameCount != null) gameCount.setText(Integer.toString(games.size()));
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_IMPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMPORT || resultCode != RESULT_OK || data == null) return;

        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                addImportedUri(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            addImportedUri(data.getData());
        }

        saveLibrary();
        renderLibrary();
    }

    private void addImportedUri(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
            // Some document providers return a transient grant only.
        }

        String name = queryDisplayName(uri);
        String extension = getExtension(name);
        String platform = detectPlatform(extension);

        for (GameEntry old : games) {
            if (old.uri.equals(uri.toString())) return;
        }

        if ("DISC".equals(platform)) {
            final String[] choices = {"PSP", "PS2", "PS3"};
            new AlertDialog.Builder(this)
                    .setTitle("Choose game platform")
                    .setItems(choices, (dialog, which) -> {
                        games.add(new GameEntry(name, choices[which], uri.toString(), extension));
                        saveLibrary();
                        renderLibrary();
                    })
                    .setOnCancelListener(dialog -> Toast.makeText(this, "لم تتم إضافة الملف؛ اختر المنصة لتصنيفه.", Toast.LENGTH_SHORT).show())
                    .show();
            return;
        }

        games.add(new GameEntry(name, platform, uri.toString(), extension.isEmpty() ? "FILE" : extension));
    }

    private String queryDisplayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) return cursor.getString(index);
            }
        } catch (Exception ignored) {
            // Fall back to the last URI segment.
        }
        String fallback = uri.getLastPathSegment();
        return fallback == null ? "Imported game" : fallback;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String detectPlatform(String extension) {
        switch (extension) {
            case "exe":
            case "msi":
                return "PC";
            case "cso":
            case "pbp":
                return "PSP";
            case "pkg":
                return "PS3";
            case "iso":
            case "bin":
            case "chd":
            case "zso":
                return "DISC";
            default:
                return "OTHER";
        }
    }

    private void showGameInfo(GameEntry game) {
        new AlertDialog.Builder(this)
                .setTitle(game.name)
                .setMessage("Platform: " + game.platform + "\nFile: " + game.extension
                        + "\n\nEngine status: source linked; runtime integration is pending.")
                .setPositiveButton("OK", null)
                .setNeutralButton("Remove", (dialog, which) -> {
                    games.remove(game);
                    saveLibrary();
                    renderLibrary();
                })
                .show();
    }

    private void loadLibrary() {
        String raw = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_GAMES, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                games.add(new GameEntry(item.optString("name"), item.optString("platform"),
                        item.optString("uri"), item.optString("extension")));
            }
        } catch (Exception ignored) {
            games.clear();
        }
    }

    private void saveLibrary() {
        JSONArray array = new JSONArray();
        try {
            for (GameEntry game : games) {
                JSONObject item = new JSONObject();
                item.put("name", game.name);
                item.put("platform", game.platform);
                item.put("uri", game.uri);
                item.put("extension", game.extension);
                array.put(item);
            }
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    .putString(KEY_GAMES, array.toString()).apply();
        } catch (Exception e) {
            Toast.makeText(this, "تعذر حفظ مكتبة الألعاب.", Toast.LENGTH_SHORT).show();
        }
    }

    private LinearLayout panel() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(background("#141925", 18, "#252C3B"));
        return layout;
    }

    private Button button(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(12);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setBackground(background("#654FE0", 14, "#654FE0"));
        return button;
    }

    private TextView label(String text, int size, String color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(Color.parseColor(color));
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable background(String color, int radius, String stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(color));
        drawable.setCornerRadius(dp(radius));
        if (!"#00000000".equals(stroke)) drawable.setStroke(dp(1), Color.parseColor(stroke));
        return drawable;
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
}
