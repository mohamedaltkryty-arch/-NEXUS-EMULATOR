package com.nexus.emulator.catalog;

import com.nexus.emulator.model.GameEntry;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class GameLibraryFilterTest {
    @Test
    public void filtersFavoritesAndSearchTogetherWithoutChangingSourceList() {
        GameEntry godOfWar = game(1, "God of War", true);
        GameEntry tekken = game(2, "Tekken 6", false);
        GameEntry gta = game(3, "Grand Theft Auto", true);
        List<GameEntry> source = Arrays.asList(godOfWar, tekken, gta);

        List<GameEntry> favorites = GameLibraryFilter.filter(source, null, true);
        List<GameEntry> search = GameLibraryFilter.filter(source, "GOD", false);

        assertEquals(Arrays.asList(godOfWar, gta), favorites);
        assertEquals(Collections.singletonList(godOfWar), search);
        assertEquals(3, source.size());
    }

    @Test
    public void emptyQueryReturnsAllNonNullGamesAndNullNamesDoNotMatchSearch() {
        GameEntry unnamed = new GameEntry(4, null, "PSP", "content://games/4", "iso", 0, 1, false);
        GameEntry named = game(5, "Persona 3", false);

        assertEquals(Arrays.asList(unnamed, named), GameLibraryFilter.filter(Arrays.asList(unnamed, named), "  ", false));
        assertEquals(Collections.singletonList(named), GameLibraryFilter.filter(Arrays.asList(unnamed, named), "persona", false));
    }

    @Test
    public void nullAndEmptyCollectionsReturnAnEmptyResult() {
        assertTrue(GameLibraryFilter.filter(null, "", false).isEmpty());
        assertTrue(GameLibraryFilter.filter(Collections.<GameEntry>emptyList(), "game", false).isEmpty());
    }

    private static GameEntry game(long id, String name, boolean favorite) {
        return new GameEntry(id, name, "PSP", "content://games/" + id, "iso", 1024, id, favorite);
    }
}