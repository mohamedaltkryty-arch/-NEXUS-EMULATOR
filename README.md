# NEXUS

NEXUS is a clean Android game library with a single user-facing interface. Repository and developer tooling are hidden from the app.

## Current playable path

- PSP games launch through the official PPSSPP Android app installed on the device.
- Import PSP files in ISO, CSO, PBP or ZSO format through Android's document picker.
- NEXUS passes the selected content URI and temporary read permission to PPSSPP; it does not copy or upload game files.
- If PPSSPP is missing, NEXUS offers its official download page.
- PS2, PS3 and Windows titles can be catalogued, but their playback engines are not yet integrated. They are not shown as playable.

## Android compatibility

- NEXUS frontend: Android 6.0 (API 23) and newer.
- No ABI filter is imposed on the Java frontend; supported ARM32, ARM64 and x86/x86_64 Android builds can install it.
- PSP runtime compatibility depends on the official PPSSPP build available for the device's CPU/GPU and Android version.
- No emulator can guarantee every device or every game. Performance depends on the SoC, graphics driver, RAM, thermal limits and game.

## User features

- Arabic RTL by default and English language switch.
- Dark and light themes.
- Local SQLite game library, search, platform filters and favorites.
- Single/multi-file import and folder import via Android Storage Access Framework.
- Game details, duplicate detection and URI permission handling.
- No account, analytics, ads, remote API or broad storage permission.

## Build

Requirements: JDK 17, Android SDK Platform 35, Build Tools 35.0.0 and Gradle 8.9.

```bash
gradle --no-daemon :app:testDebugUnitTest
gradle --no-daemon :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

GitHub Actions runs JVM tests, builds the APK, installs it on an x86_64 Android emulator and launches the main activity.

## Import formats

- PSP: `.iso`, `.cso`, `.pbp`, `.zso`.
- PS2: `.chd` and manually selected disc images (catalogue only in this build).
- PS3: `.pkg` or a PS3 folder (catalogue only in this build).
- Windows: import the complete installed game folder (catalogue only in this build).

Use only game files you are legally entitled to use. NEXUS does not include commercial games, console firmware, BIOS images or encryption keys.

## Engine source and licenses

Upstream sources remain pinned as submodules for planned work. They are development references, not engine binaries embedded in the current NEXUS APK. Retain all upstream copyright and license notices and review each engine's dependencies before redistribution.

- PPSSPP: GPL-2.0-or-later
- ARMSX2: GPL-3.0
- ARMSX3: GPL-2.0-only
- Winlator app: LGPL-2.1

See `docs/ENGINE_INTEGRATION.md` for scope and legal/technical boundaries.
