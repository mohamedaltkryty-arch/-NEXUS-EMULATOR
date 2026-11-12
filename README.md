# NEXUS

NEXUS is a polished, offline-first Android companion for a personal PSP game collection.

## What works in this build

- Import one or multiple PSP game files using Android's Storage Access Framework.
- Supported formats: ISO, CSO, PBP and ZSO.
- SQLite uses a versioned, non-destructive schema upgrade with an indexed PSP-only query; historical non-PSP rows are retained but not loaded into the active library.
- Persist only the read permission granted by the selected document provider.
- Browse a local SQLite library, search by game name, filter favorites, view file details, remove entries and launch games.
- Arabic RTL by default and English language switch.
- Dark and light themes.
- PSP playback handoff to the official PPSSPP Android app (standard or Gold).
- No user account, ads, analytics, remote API, broad storage permission or app-side file uploads.

## Deliberately hidden capabilities

PS2, PS3 and Windows game playback are not integrated into NEXUS. Their old catalogue entries are retained in the local database but excluded from the visible library and import flow. NEXUS does not claim that those systems are playable.

The Java frontend does not embed a PPSSPP core. It opens the user-selected PSP file in an independently installed official PPSSPP app. Runtime compatibility is controlled by the device's CPU, GPU, Android version, PPSSPP build and individual game.

## Android compatibility

- Frontend: Android 6.0 (API 23) and newer.
- Compile/target SDK: 36.
- Java: 17.
- No ABI filter on the Java frontend.
- External PPSSPP runtime must support the device ABI and graphics driver.

## Build and verification

Requirements: JDK 17, Android SDK Platform 36, Build Tools 36.0.0, Android Gradle Plugin 8.10.1 and Gradle 8.11.1.

Build commands: gradle --no-daemon :app:testDebugUnitTest, then gradle --no-daemon :app:assembleDebug.

APK output: app/build/outputs/apk/debug/app-debug.apk.

GitHub Actions runs source checks and the full JVM test suite first. The Android build and emulator install/launch test run only after verification succeeds.

## Privacy and legal boundary

NEXUS does not distribute commercial game files, console firmware, BIOS images or encryption keys. Import only games you are legally entitled to use. Files remain with the selected document provider; NEXUS stores document URIs and metadata locally.

The upstream engine source references are not bundled runtime binaries. See docs/ENGINE_INTEGRATION.md for their current status and licenses.
