# NEXUS 0.8 — Product specification

## Vision

A focused Android PSP library companion that makes a personally owned game collection easy to import, organize and launch without advertising unsupported platforms as playable.

## Primary user journey

1. Open NEXUS.
2. Import one or multiple PSP files.
3. Find games through search or the All/Favorites filter.
4. Open details, toggle favorite, remove, or launch.
5. NEXUS hands a selected game URI to the official PPSSPP Android app.

## Active capabilities

- PSP ISO, CSO, PBP and ZSO import through Android Storage Access Framework.
- Multi-select import with persistable read-only access when the document provider grants it.
- Local SQLite metadata store with a non-destructive schema upgrade and query index.
- Search, All/Favorites filters, game details, remove, favorite and launch handoff.
- Recycled list rows for larger libraries.
- Arabic RTL default, English, dark and light themes.
- First-run empty state and separate no-results recovery state.
- A persistent import action.
- No account, telemetry, ads, remote API, network permission or broad storage permission.

## Deliberately excluded from the active UI

PS2, PS3 and Windows/PC game engines are not integrated. Their historical database rows are retained but hidden from the PSP library. Those formats cannot be imported into the current active library and must not be described as playable.

## UX and visual direction

- Mobile-first native Android interface.
- Compact NEXUS top bar and settings.
- Collection count, search and two filters only when games exist.
- Recycled list with platform mark, game name, file format/size, favorite and play actions.
- Fixed import action at the bottom.
- Empty library hides search and filters.
- Settings contains only language, theme and about.
- 48dp minimum touch targets and localized content descriptions.
- No decorative controls, disabled platform tabs or fake runtime status.

## Architecture

- Android Native Java 17.
- Android SDK 36, minSdk 23, targetSdk 36.
- SQLiteOpenHelper for local catalogue.
- SharedPreferences for language/theme.
- Android Storage Access Framework for user-selected document URIs.
- Pure Java PSP file policy, library filtering and URI grant policy.
- Native ListView with recycled adapter; no added UI runtime dependency.
- Dedicated PPSSPP launch adapter.

## Data model

Game rows contain id, name, platform, uri, extension, size_bytes, added_at and favorite. URI is unique. Existing rows are retained; the active query returns only PSP rows with the four supported extensions.

## Security and privacy

- No INTERNET permission or remote service.
- No account/authentication surface.
- Persist only the read URI grant returned by the document provider.
- Check for duplicates before acquiring another persisted grant.
- Release a newly acquired grant after a failed insert only when no library row references the URI.
- No file uploads or copying into app-managed public storage.
- Parameterized SQL for lookups and mutations.
- App backup disabled.
- External PPSSPP process receives only the selected URI read grant.

## Known boundaries

NEXUS does not embed emulator runtimes. PSP performance depends on the selected PPSSPP build, device SoC/GPU, Android version, thermal state and game-specific compatibility. Emulator smoke tests verify installation and app launch, not every game or physical device.
