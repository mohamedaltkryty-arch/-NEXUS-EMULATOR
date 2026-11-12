# NEXUS — Product specification

## Vision
A focused Android PSP library companion that makes a personally owned game collection easy to import, organize and launch without advertising unsupported platforms as playable.

## MVP capabilities
- Import PSP ISO, CSO, PBP and ZSO files through the Android Storage Access Framework.
- Multi-select import and persisted read access where the document provider grants it.
- Local SQLite metadata store.
- Search, all/favorites filters, game details, remove and launch handoff.
- Arabic RTL default, English, dark and light themes.
- Official PPSSPP Android app handoff.
- No account, telemetry, ads, remote API or broad storage permission.

## Excluded from active UI
PS2, PS3 and Windows game engines are not integrated. Legacy entries may remain in storage for forward compatibility but are hidden from the current PSP library and cannot be newly imported through the active UI.

## Architecture
- Android Native Java 17.
- Android SDK 36, minSdk 23, targetSdk 36.
- SQLiteOpenHelper for local catalogue.
- SharedPreferences for language/theme.
- Android Storage Access Framework for user-selected document URIs.
- Dedicated PSP format policy and PPSSPP launch adapter.
- GitHub Actions verifies JVM tests before APK build and emulator smoke test.

## Data model
Games rows contain id, name, platform, uri, extension, size_bytes, added_at and favorite.

## Security and privacy
- No INTERNET permission.
- No account/authentication surface.
- Persist only the read URI grant returned by the document provider.
- No file uploads or copying into app-managed public storage.
- Parameterized SQL for library mutations.
- App backup disabled.
- External PPSSPP process receives only the selected URI read grant.

## Known boundaries
The app does not embed emulator runtimes. PSP performance depends on the selected PPSSPP build, device SoC/GPU, Android version, thermal state and game-specific compatibility. No universal game/device compatibility claim is made.
