package com.nexus.emulator.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nexus.emulator.model.GameEntry;
import com.nexus.emulator.util.AppStrings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class GameListAdapter extends BaseAdapter {
    public interface Listener {
        void onGameClick(GameEntry game);
        void onFavoriteClick(GameEntry game);
        void onPlayClick(GameEntry game);
    }

    private final Context context;
    private final NexusPalette palette;
    private final boolean arabic;
    private final Listener listener;
    private List<GameEntry> games = Collections.emptyList();

    public GameListAdapter(Context context, NexusPalette palette, boolean arabic, Listener listener) {
        this.context = context;
        this.palette = palette;
        this.arabic = arabic;
        this.listener = listener;
    }

    public void setGames(List<GameEntry> newGames) {
        games = newGames == null ? Collections.emptyList() : new ArrayList<>(newGames);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return games.size(); }
    @Override public GameEntry getItem(int position) { return games.get(position); }
    @Override public long getItemId(int position) { return getItem(position).getId(); }
    @Override public boolean hasStableIds() { return true; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = createRow();
            holder = (Holder) convertView.getTag();
        } else {
            holder = (Holder) convertView.getTag();
        }

        GameEntry game = getItem(position);
        String name = game.getName() == null || game.getName().trim().isEmpty()
                ? AppStrings.get("unknown_game", arabic) : game.getName().trim();
        String extension = game.getExtension() == null
                ? "" : game.getExtension().toUpperCase(Locale.ROOT);
        String metadata = extension;
        if (game.getSizeBytes() > 0) metadata += "  •  " + formatSize(game.getSizeBytes());

        holder.title.setText(name);
        holder.metadata.setText(metadata);
        holder.favorite.setText(game.isFavorite() ? "★" : "☆");
        holder.favorite.setContentDescription(AppStrings.get(
                game.isFavorite() ? "unfavorite" : "favorite", arabic));
        holder.play.setContentDescription(AppStrings.get("play", arabic));

        holder.card.setOnClickListener(view -> listener.onGameClick(game));
        holder.favorite.setOnClickListener(view -> listener.onFavoriteClick(game));
        holder.play.setOnClickListener(view -> listener.onPlayClick(game));
        return convertView;
    }

    private View createRow() {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setLayoutDirection(arabic ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setMinimumHeight(dp(84));
        card.setBackground(shape(palette.surface, 18, palette.border));
        card.setFocusable(true);
        card.setClickable(true);

        TextView mark = new TextView(context);
        mark.setText(AppStrings.get("psp", arabic));
        mark.setTextSize(12);
        mark.setTypeface(Typeface.DEFAULT_BOLD);
        mark.setTextColor(Color.WHITE);
        mark.setGravity(Gravity.CENTER);
        GradientDrawable markBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(139, 124, 255), Color.rgb(65, 79, 163)});
        markBackground.setCornerRadius(dp(16));
        mark.setBackground(markBackground);
        card.addView(mark, new LinearLayout.LayoutParams(dp(54), dp(58)));

        LinearLayout details = new LinearLayout(context);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, dp(6), 0);

        TextView title = new TextView(context);
        title.setTextColor(palette.primaryText);
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        title.setMaxLines(2);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        details.addView(title);

        TextView metadata = new TextView(context);
        metadata.setTextColor(palette.secondaryText);
        metadata.setTextSize(10);
        metadata.setSingleLine(true);
        metadata.setEllipsize(android.text.TextUtils.TruncateAt.END);
        metadata.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        details.addView(metadata, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(20)));

        card.addView(details, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button favorite = actionButton("☆", palette.accentSoft, palette.accent);
        card.addView(favorite, new LinearLayout.LayoutParams(dp(48), dp(48)));

        Button play = actionButton("▶", palette.accent, Color.WHITE);
        LinearLayout.LayoutParams playParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        if (arabic) playParams.rightMargin = dp(6); else playParams.leftMargin = dp(6);
        card.addView(play, playParams);

        Holder holder = new Holder(card, title, metadata, favorite, play);
        card.setTag(holder);
        return card;
    }

    private Button actionButton(String label, int background, int foreground) {
        Button button = new Button(context);
        button.setText(label);
        button.setTextColor(foreground);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(48));
        button.setMinimumWidth(dp(48));
        button.setPadding(0, 0, 0, 0);
        button.setBackground(shape(background, 14, background));
        return button;
    }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private int dp(int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024L) return bytes + " B";
        if (bytes < 1024L * 1024L) return String.format(Locale.US, "%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
        }
        return String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private static final class Holder {
        final LinearLayout card;
        final TextView title;
        final TextView metadata;
        final Button favorite;
        final Button play;

        Holder(LinearLayout card, TextView title, TextView metadata, Button favorite, Button play) {
            this.card = card;
            this.title = title;
            this.metadata = metadata;
            this.favorite = favorite;
            this.play = play;
        }
    }
}