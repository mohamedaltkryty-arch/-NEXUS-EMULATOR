# Database, local API and security

## SQLite schema

The authoritative schema is created by GameDatabase.onCreate:

- games table with unique URI, platform, extension, size, import date, and favorite state.
- indexes on platform and import date.
- SQLite write-ahead logging enabled.

## Local operations

- Insert: GameDatabase.insert(...); duplicate URIs are rejected by the UNIQUE constraint.
- List: GameDatabase.getAll(); favorites are ordered first.
- Favorite: GameDatabase.setFavorite(id, value).
- Delete: GameDatabase.delete(id).
- Count: GameDatabase.count().

No remote REST API is part of the offline MVP. Engine adapters are the internal execution API.

## Input validation

- Import accepts recognised game extensions or user-selected folders.
- Disc formats shared by multiple platforms prompt the user for a platform.
- Selected document URI comes from Android's document picker.
- SQL values are passed through ContentValues and selection arguments.
- Game contents are not executed or parsed by the launcher.

## Threat controls

- No broad storage permission.
- No network permission.
- No implicit external app launch for game files.
- No shell command construction from filenames.
- No WebView or downloaded executable code.
- Private app database; Android backup disabled.
- Persisted URI access is limited to documents/folders selected by the user.
- Native engine integration must validate input, bound memory allocations, use safe JNI ownership, and never pass file names into a shell.
