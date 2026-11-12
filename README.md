# NEXUS 0.8.0

NEXUS is a focused, offline-first Android companion for a personal PSP game collection. It keeps the everyday actions in one clean library: import, search, favorite, inspect, remove, and launch.

## Active features

- Import one or multiple PSP game files with Android's Storage Access Framework.
- Supported formats: ISO, CSO, PBP and ZSO.
- Local SQLite library with a non-destructive schema migration and indexed PSP queries.
- Persisted read-only document access when the selected provider grants it.
- Search, All/Favorites filters, game details, favorite toggles, remove, and launch.
- Recycled game rows for smoother scrolling with larger libraries.
- Arabic RTL by default, English, dark and light themes.
- A focused home screen that hides search and filters until the first game is added.
- A persistent import action and clear empty/no-results states.

## Playback boundary

PSP playback is handed to the official PPSSPP Android application (standard or Gold). NEXUS validates the selected content URI before launching and grants PPSSPP temporary read access. It does not embed a PPSSPP core.

PS2, PS3 and Windows/PC runtimes are not integrated. Their historical source references are not packaged binaries and those platforms are hidden from the active app. NEXUS does not claim they are playable.

## Privacy and security

- No account, ads, analytics, telemetry, remote API or INTERNET permission.
- No broad storage permission.
- Game files remain with the document provider selected by the user; NEXUS stores metadata and content URIs locally.
- Persisted read permission is taken only when Android returns both read and persistable grant flags.
- Duplicate imports are detected before taking another persisted grant.
- If an insert fails, a newly taken grant is released when no library row references that URI.
- App backup is disabled.
- NEXUS does not distribute games, BIOS, firmware, encryption keys or proprietary SDKs.

## Android compatibility

- Minimum Android: 6.0 (API 23).
- Compile SDK / target SDK: 36.
- Java: 17.
- Android Gradle Plugin: 8.10.1.
- Gradle: 8.11.1.

## Verification and build

Run JVM tests and Android lint:

```bash
gradle --no-daemon :app:testDebugUnitTest :app:lintDebug
```

Build the installable debug APK only after verification:

```bash
gradle --no-daemon :app:assembleDebug
```

GitHub Actions runs source/privacy checks, JVM tests and Android lint on pushes and pull requests. APK packaging and emulator installation are gated: they run only through a manual workflow dispatch with build_apk=true or a final commit whose message contains [build-apk]. The build job depends on successful verification.

The debug APK is signed with Android's standard debug certificate for device testing. It is not a Play Store release signed with a private production key. PSP compatibility still depends on the device, PPSSPP build, graphics driver and individual game.
