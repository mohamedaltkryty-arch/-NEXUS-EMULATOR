# NEXUS — Product specification

## Vision
NEXUS is an Android-first local PSP game library. The product brings a user's legally obtained PSP game files into one organized, private place and launches each title through the official PPSSPP app.

## Shipped scope
- Local SQLite game library.
- SAF import of one or multiple PSP files with persisted read grants.
- Only ISO, CSO, PBP and ZSO formats are accepted.
- Search, All/Favorites filtering, game details, duplicate prevention and removal.
- Arabic RTL by default, English, dark and light themes.
- Direct PPSSPP / PPSSPP Gold launch handoff.
- Accurate runtime-installed state and official download prompt.
- No login, telemetry, ads, remote API, game uploads or broad storage access.

## Explicitly not shipped
- No embedded PPSSPP native core.
- No playable PS2, PS3 or Windows integration.
- No folder import or catalogue-only unsupported game records.
- No game images, BIOS, firmware, keys or copyrighted game content bundled.

## Technical stack
- Android Java 17, Android SDK 35, minSdk 23, targetSdk 35.
- SQLiteOpenHelper for local catalogue.
- SharedPreferences for language and theme.
- Android Storage Access Framework for user-authorized files.
- Explicit Intent handoff to official PPSSPP.
- GitHub Actions for JVM tests, APK build and Android emulator smoke launch.

## Local data model
games table:
- _id INTEGER PRIMARY KEY AUTOINCREMENT
- name TEXT NOT NULL
- platform TEXT NOT NULL
- uri TEXT NOT NULL UNIQUE
- extension TEXT NOT NULL
- size_bytes INTEGER NOT NULL DEFAULT 0
- added_at INTEGER NOT NULL
- favorite INTEGER NOT NULL DEFAULT 0

Settings:
- language_arabic BOOLEAN
- theme_dark BOOLEAN

## Security and privacy
- No account required.
- No INTERNET permission.
- SAF grants are scoped to user-selected files.
- Database APIs use parameterized queries.
- Android backup is disabled.
- No WebView, telemetry or file upload.
- PPSSPP receives only the selected content URI and temporary read grant.

## Runtime and compatibility
Only PSP files are importable. PPSSPP remains a separate app and owns rendering, audio, controls, save states and title compatibility. Actual compatibility depends on PPSSPP version and the specific device/game.

## Testing
CI runs JVM unit tests and installs/launches a clean build on an x86_64 Android API 35 emulator. These validate the front end and launch boundary, not every ROM, PPSSPP release, graphics driver or physical device.
