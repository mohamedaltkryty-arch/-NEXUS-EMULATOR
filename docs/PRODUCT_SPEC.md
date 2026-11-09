# NEXUS — Product specification

## Vision

NEXUS is a clean Android game library with a platform-aware launch layer. Its advantage is one consistent catalogue and import flow, not a claim to outperform specialist emulators.

## MVP

- Local SQLite game library.
- SAF file/folder import with persisted URI grants.
- Search, platform filters, favorites and game details.
- Arabic RTL by default, English, dark and light themes.
- PSP launch handoff to official PPSSPP (standard or Gold).
- Honest readiness: PS2, PS3 and Windows are catalogue-only until runtimes are integrated.
- No login, telemetry, ads, remote API or broad storage access.

## Roadmap

1. PSP launch bridge and device compatibility handling.
2. PS2 runtime integration with BIOS selection and device tests.
3. Winlator container/runtime integration for full Windows game folders.
4. Experimental PS3 runtime only after toolchain, storage and device-profile testing.
5. Release signing, database migrations, controller profiles and title compatibility reporting.

## Technical stack

- Android Java 17, Android SDK 35.
- Android 6.0+ (minSdk 23).
- SQLiteOpenHelper for the local catalogue.
- SharedPreferences for user settings.
- Storage Access Framework for user-authorized game files.
- Native engines isolated behind platform adapters.
- GitHub Actions for JVM tests, APK build and Android emulator smoke launch.

## Cross-device policy

The Java UI has no ABI restriction and can be installed on supported ARM32, ARM64 and x86/x86_64 Android builds. Native engines have independent CPU/GPU/API requirements. Performance is title- and device-dependent; universal compatibility is not a technically valid promise.

## Local data model

`games` table:
- `_id INTEGER PRIMARY KEY AUTOINCREMENT`
- `name TEXT NOT NULL`
- `platform TEXT NOT NULL`
- `uri TEXT NOT NULL UNIQUE`
- `extension TEXT NOT NULL`
- `size_bytes INTEGER NOT NULL DEFAULT 0`
- `added_at INTEGER NOT NULL`
- `favorite INTEGER NOT NULL DEFAULT 0`

Settings:
- `language_arabic BOOLEAN`
- `theme_dark BOOLEAN`

## Security and privacy

- No account required.
- No INTERNET permission.
- SAF grants are scoped to user-selected files.
- Database APIs use parameterized queries.
- Android backup is disabled.
- No WebView, telemetry or file upload.
- PPSSPP receives only a temporary read grant for the selected game URI.

## Performance and failure handling

- Library queries operate on local metadata.
- Only supported PSP game formats are routed to PPSSPP.
- Missing runtime, invalid URI, lost document grant or missing file produces a user-facing error.
- Heavy native engines are not loaded into the NEXUS process.

## Testing

CI runs JVM tests and a clean install/launch smoke test on x86_64 Android emulator. These validate the frontend and launch boundary, not compatibility for every game or external emulator.
