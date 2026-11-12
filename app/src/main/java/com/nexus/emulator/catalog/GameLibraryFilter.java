package com.nexus.emulator.catalog;

import com.nexus.emulator.model.GameEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class GameLibraryFilter {
    private GameLibraryFilter() { }

    public static List<GameEntry> filter(List<GameEntry> games, String query, boolean favoritesOnly) {
        if (games == null || games.isEmpty()) return Collections.emptyList();

        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<GameEntry> result = new ArrayList<>();

        for (GameEntry game : games) {
            if (game == null) continue;
            if (favoritesOnly && !game.isFavorite()) continue;

            String name = game.getName();
            if (!normalizedQuery.isEmpty()
                    && (name == null || !name.toLowerCase(Locale.ROOT).contains(normalizedQuery))) {
                continue;
            }
            result.add(game);
        }
        return result;
    }
}