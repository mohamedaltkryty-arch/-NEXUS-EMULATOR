# NEXUS

NEXUS is an Android-first local library for PSP games. It focuses on the functions that are available and verified in this release instead of displaying unfinished console runtimes.

## What works

- Import one or multiple PSP game files through Android's Storage Access Framework.
- Supported formats: ISO, CSO, PBP and ZSO.
- Local SQLite library with search, favorites, game details and removal.
- Direct launch to the official PPSSPP Android app (standard or Gold) using the selected content URI and temporary read access.
- Clear setup prompt and official download link when PPSSPP is not installed.
- Arabic RTL by default, English language option, dark and light themes.
- No account, ads, analytics, Internet permission, upload, or broad storage permission.

NEXUS does not include game files, BIOS, firmware, decryption keys, or a built-in console emulator. Use only game files you are legally entitled to use.

## Runtime boundary

The PSP integration is an external-app launch adapter. PPSSPP owns emulation, graphics, audio, controls, save states and title compatibility. A game is playable only when a compatible official PPSSPP build is installed on the device.

PS2 (ARMSX2), PS3 (ARMSX3), and Windows (Winlator) source references remain pinned as development research submodules. They are not imported, listed as playable, or bundled in this APK. Do not treat a source link as an integrated runtime.

## Android compatibility

- Frontend: Android 6.0 (API 23) and newer.
- Java 17, Android SDK 35, Android Gradle Plugin 8.7.3, Gradle 8.9.
- No ABI filter is imposed on the Java frontend. PPSSPP runtime compatibility depends on the installed PPSSPP build and the device CPU, GPU driver, RAM, thermal limits, Android version and game.

## Build

Requirements: JDK 17, Android SDK Platform 35, Build Tools 35.0.0 and Gradle 8.9.

```bash
gradle --no-daemon :app:testDebugUnitTest
gradle --no-daemon :app:assembleDebug
```

APK output: app/build/outputs/apk/debug/app-debug.apk.

GitHub Actions runs JVM tests, builds the debug APK, installs it on an Android API 35 x86_64 emulator and starts the main Activity. This smoke test does not verify every PPSSPP game or every physical device.

## Privacy and permissions

- Game metadata is stored locally in SQLite.
- Selected content URIs are retained via Android's document-provider read grants.
- Removing a game removes its database entry and releases its persisted read grant when the provider permits.
- NEXUS does not copy or upload game images and does not request broad storage access.

## Engine source and licenses

Upstream source repositories are pinned as submodules for future evaluation. They are not compiled into this APK. Keep their license files, copyright notices and attribution when researching integration.

- PPSSPP: GPL-2.0-or-later (external installed application)
- ARMSX2: GPL-3.0 (source reference only)
- ARMSX3: GPL-2.0-only (source reference only)
- Winlator app: LGPL-2.1 (source reference only)

See docs/ENGINE_INTEGRATION.md for the exact boundary.
