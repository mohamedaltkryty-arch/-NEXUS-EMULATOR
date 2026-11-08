# NEXUS EMULATOR — Product and technical specification

## Vision and differentiation

NEXUS is an Android-first, offline-capable game collection manager designed around a single library for several emulation families. It does not rewrite mature emulation cores. Instead, it provides a consistent catalogue, document-provider access, per-title metadata, engine capability discovery, and a controlled launch boundary. The initial hardware target is POCO X4 GT (Dimensity 8100, Mali-G610 MC6, 12 GB RAM and 256 GB storage as reported by the owner).

The market already contains dedicated tools such as PPSSPP, Winlator, and platform-specific Android frontends. NEXUS's differentiator is the unified library and a device-profile-aware engine layer, not a claim that it emulates more accurately than upstream cores. Core compatibility remains determined by each engine and each title.

## MVP

- Local game library backed by SQLite.
- SAF single/multi-file import and folder import for installed Windows games or PS3 disc folders.
- Platform classification with manual selection for ambiguous disc images.
- Search, platform filters, favorites, details and removal.
- Arabic RTL by default, English option, dark and light themes.
- Explicit engine availability. No launch action is shown until a real adapter is integrated.
- No account, no server, no analytics, no network permission, and no background tracking.

## Roadmap

1. Current milestone: launcher/library, local database, SAF import, settings, engine registry, automated build and unit/smoke tests.
2. PSP: build PPSSPP Android arm64-v8a core, integrate UI/runtime bridge, test saves/audio/controller/display on target device.
3. PS2: integrate ARMSX2 native Android app/core with user-supplied PS2 BIOS.
4. Windows: embed a supported Winlator runtime/container workflow; import full game folders.
5. PS3: experimental ARMSX3 integration only after toolchain and device benchmarks. Upstream core build requirements and hardware demands are high.
6. Release hardening: signed release builds, migration tests, compatibility database, controller profiles, crash diagnostics with opt-in and no game-file uploads.

## Competitors and positioning

- PPSSPP is a mature dedicated PSP emulator; NEXUS should reuse and credit it, not fork its user experience blindly.
- Winlator targets Windows applications through Wine and Box86/Box64; a Windows title needs its installed files and runtime, not just an EXE copied to the library.
- ARMSX2 is an Android-focused PS2 project; BIOS and game compatibility vary.
- ARMSX3 is an ARM64 Android port of RPCS3 with substantial build and runtime requirements.
- Dedicated platform frontends have deeper specialist controls. NEXUS trades some platform-specific depth for one catalogue and consistent import/settings.

## Architecture

- Android application: Java 17, Android SDK 35, native Android Views.
- Local persistence: SQLiteOpenHelper, WAL mode, parameterized ContentValues/query APIs.
- User settings: SharedPreferences.
- File access: Storage Access Framework (ACTION_OPEN_DOCUMENT / ACTION_OPEN_DOCUMENT_TREE) with persisted URI grants. No broad storage permission.
- Engine boundary: GameEngine interface and EngineRegistry; native cores must be implemented behind per-platform adapters.
- CI: GitHub Actions builds the debug APK, executes JVM unit tests and launches it in an Android emulator.
- No remote API is required for the current product: the app is local-only. A REST backend would add account and privacy risk without helping emulator execution.

## Data model

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

No game binary, BIOS, firmware, keys, save files or personal content is uploaded by NEXUS.

## Local API boundary

GameEngine:
- getEngineId(): String
- canLaunch(Context, Uri): boolean
- launch(Context, Uri): void throws EngineLaunchException

Adapters must validate URI access, platform, file type and runtime availability before launch. The UI must display actionable errors and never report success before a native engine confirms startup.

GameDatabase:
- insert(...)
- getAll()
- setFavorite(id, value)
- delete(id)
- count()

## UX and accessibility

- Mobile-first, single-column library with touch targets of at least 48dp for primary actions.
- Arabic is the default and uses Android RTL layout direction. English switches to LTR.
- Dark and light palettes.
- Search is local and immediate; platform filters do not require network.
- Engine status is explicit in the UI.
- Text scales with Android font settings; no custom font assets are required for this milestone.

## Security and privacy

- No login or user identity is needed for a private local library.
- No INTERNET permission is declared.
- SAF grants access only to documents/folders selected by the user.
- URI values are stored as data and never concatenated into SQL.
- SQLite queries use parameterized arguments.
- No WebView, remote code loading, telemetry, ads or file upload.
- Android backup is disabled so selected game document URIs are not copied to cloud backup.
- Third-party emulator code and licenses remain isolated in Git submodules.

## Performance and failure handling

- SQLite uses WAL for concurrent read/write efficiency.
- The game list is queried from local metadata and filtered in memory.
- Search, sorting and filters do not open game binaries.
- Missing SAF grants produce an explicit re-import message rather than a crash.
- Unrecognized formats are rejected.
- Native engines will be loaded lazily, one at a time; each adapter must report supported capabilities and startup errors.

## Tests

JVM tests cover file extension classification, ambiguous disc handling, unsupported files, engine registry coverage and launch readiness. CI additionally builds the debug APK and launches it in an Android emulator. These checks validate the launcher, not game compatibility. Native engine/game tests are separate and require real game files plus any legally required firmware/BIOS.

## Production signing

The CI artifact is a debug-signed APK suitable for installation and smoke testing. A public/release build must be signed with a private keystore stored as GitHub Actions secrets. Never commit the keystore or passwords.
