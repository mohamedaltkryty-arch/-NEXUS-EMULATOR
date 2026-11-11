# NEXUS Redesign — Implementation Plan

Base: previously verified Android source commit bc15d71c5165b45d2562f89271d6f8ee58e52d48
Target: Android Java 17, SDK 35, Gradle 8.9
Branch: nexus-redesign

## Task 1 — Define the only supported game capability
1. Replace classifier tests first.
2. Prove ISO/CSO/PBP/ZSO classify as PSP and all other formats/platforms are unsupported.
3. Reduce EngineRegistry to the single PPSSPP external runtime descriptor.
4. Run JVM tests; do not build an APK while the red test is failing.

## Task 2 — Harden the PSP handoff and file lifecycle
1. Add an installed-runtime query that supports Android 13+ package-info APIs.
2. Correct free and Gold activity component names.
3. Add default category and task flags to the VIEW handoff.
4. Keep content URI + clip data + read permission.
5. Release the persisted URI read grant when a game is removed.
6. Add unit coverage for the supported PSP extension contract.

## Task 3 — Rebuild the main user experience
1. Replace the crowded multi-platform home with the focused PSP library.
2. Implement the NEXUS visual tokens for dark and light modes.
3. Use a clear title bar, import call-to-action, runtime status, compact stats, search, All/Favorites filter, and game cards.
4. Provide separate empty-library and no-matching-results states.
5. Make launch and favorite actions explicit and accessible.
6. Keep Settings limited to language, theme, and About.

## Task 4 — Remove misleading product paths
1. Remove folder import and platform-guess dialogs from the app flow.
2. Remove PS2, PS3, Windows and pending-engine choices from user-visible strings and screens.
3. Preserve source submodule metadata and license notes as development references only.
4. Update README, product spec, engine status, and version metadata to match shipped behavior.

## Task 5 — Verify and package last
1. Run JVM unit tests.
2. Build the debug APK with JDK 17 / Android SDK 35 / Gradle 8.9.
3. Install the exact artifact on an Android API 35 x86_64 emulator.
4. Launch the Activity, confirm foreground package, and capture a UI screenshot for review.
5. Fix any failing checks and repeat.
6. Only after verification, upload the APK artifact and provide its SHA-256, version, and limitations.
