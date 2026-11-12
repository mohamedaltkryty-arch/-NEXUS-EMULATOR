package com.nexus.emulator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nexus.emulator.catalog.GameFileClassifier;
import com.nexus.emulator.model.GameEntry;

import java.util.ArrayList;
import java.util.List;

public final class GameDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nexus_library.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_GAMES = "games";
    private static final String COL_ID = "_id";
    private static final String COL_NAME = "name";
    private static final String COL_PLATFORM = "platform";
    private static final String COL_URI = "uri";
    private static final String COL_EXTENSION = "extension";
    private static final String COL_SIZE = "size_bytes";
    private static final String COL_ADDED = "added_at";
    private static final String COL_FAVORITE = "favorite";

    public GameDatabase(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_GAMES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PLATFORM + " TEXT NOT NULL, " +
                COL_URI + " TEXT NOT NULL UNIQUE, " +
                COL_EXTENSION + " TEXT NOT NULL, " +
                COL_SIZE + " INTEGER NOT NULL DEFAULT 0, " +
                COL_ADDED + " INTEGER NOT NULL, " +
                COL_FAVORITE + " INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE INDEX idx_games_platform ON " + TABLE_GAMES + "(" + COL_PLATFORM + ")");
        db.execSQL("CREATE INDEX idx_games_added ON " + TABLE_GAMES + "(" + COL_ADDED + " DESC)");
        db.execSQL("CREATE INDEX idx_games_platform_extension ON "
                + TABLE_GAMES + "(" + COL_PLATFORM + ", " + COL_EXTENSION + ")");
        createLibraryIndex(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) onCreate(db);
        if (oldVersion < 2) {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_games_platform_extension ON "
                    + TABLE_GAMES + "(" + COL_PLATFORM + ", " + COL_EXTENSION + ")");
        }
        if (oldVersion < 3) createLibraryIndex(db);
    }

    private void createLibraryIndex(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_games_psp_library ON " + TABLE_GAMES
                + "(" + COL_PLATFORM + ", " + COL_EXTENSION + ", "
                + COL_FAVORITE + " DESC, " + COL_ADDED + " DESC)");
    }

    public synchronized boolean insert(String name, String platform, String uri,
                                        String extension, long sizeBytes, long addedAt) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PLATFORM, platform);
        values.put(COL_URI, uri);
        values.put(COL_EXTENSION, extension);
        values.put(COL_SIZE, Math.max(0L, sizeBytes));
        values.put(COL_ADDED, addedAt);
        values.put(COL_FAVORITE, 0);
        try {
            return getWritableDatabase().insertOrThrow(TABLE_GAMES, null, values) > 0;
        } catch (SQLiteConstraintException duplicate) {
            return false;
        }
    }

    public synchronized boolean containsUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) return false;
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT 1 FROM " + TABLE_GAMES + " WHERE " + COL_URI + "=? LIMIT 1",
                new String[]{uri})) {
            return cursor.moveToFirst();
        }
    }

    public synchronized List<GameEntry> getAll() {
        List<GameEntry> result = new ArrayList<>();
        String order = COL_FAVORITE + " DESC, " + COL_ADDED + " DESC, " + COL_NAME + " COLLATE NOCASE ASC";
        try (Cursor cursor = getReadableDatabase().query(TABLE_GAMES, null, null, null, null, null, order)) {
            while (cursor.moveToNext()) result.add(read(cursor));
        }
        return result;
    }

    public synchronized List<GameEntry> getAllPsp() {
        List<GameEntry> result = new ArrayList<>();
        List<String> extensions = GameFileClassifier.supportedPspExtensions();
        StringBuilder selection = new StringBuilder(COL_PLATFORM).append("=? AND ")
                .append(COL_EXTENSION).append(" IN (");
        String[] selectionArgs = new String[extensions.size() + 1];
        selectionArgs[0] = GameFileClassifier.PSP;
        for (int i = 0; i < extensions.size(); i++) {
            if (i > 0) selection.append(',');
            selection.append('?');
            selectionArgs[i + 1] = extensions.get(i);
        }
        selection.append(')');

        String order = COL_FAVORITE + " DESC, " + COL_ADDED + " DESC, "
                + COL_NAME + " COLLATE NOCASE ASC";
        try (Cursor cursor = getReadableDatabase().query(TABLE_GAMES, null,
                selection.toString(), selectionArgs, null, null, order)) {
            while (cursor.moveToNext()) result.add(read(cursor));
        }
        return result;
    }

    public synchronized int countPsp() {
        List<String> extensions = GameFileClassifier.supportedPspExtensions();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ").append(TABLE_GAMES)
                .append(" WHERE ").append(COL_PLATFORM).append("=? AND ")
                .append(COL_EXTENSION).append(" IN (");
        String[] args = new String[extensions.size() + 1];
        args[0] = GameFileClassifier.PSP;
        for (int i = 0; i < extensions.size(); i++) {
            if (i > 0) sql.append(',');
            sql.append('?');
            args[i + 1] = extensions.get(i);
        }
        sql.append(')');
        try (Cursor cursor = getReadableDatabase().rawQuery(sql.toString(), args)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public synchronized boolean setFavorite(long id, boolean favorite) {
        ContentValues values = new ContentValues();
        values.put(COL_FAVORITE, favorite ? 1 : 0);
        return getWritableDatabase().update(TABLE_GAMES, values, COL_ID + "=?",
                new String[]{Long.toString(id)}) == 1;
    }

    public synchronized boolean delete(long id) {
        return getWritableDatabase().delete(TABLE_GAMES, COL_ID + "=?",
                new String[]{Long.toString(id)}) == 1;
    }

    public synchronized int count() {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_GAMES, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private GameEntry read(Cursor cursor) {
        return new GameEntry(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PLATFORM)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EXTENSION)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_SIZE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ADDED)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAVORITE)) == 1
        );
    }
}