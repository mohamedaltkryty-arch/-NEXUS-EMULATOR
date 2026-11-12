# NEXUS 0.8 Premium Experience Implementation Plan

> For agentic workers: execute task-by-task; each task ends with a fresh verification run.

**Goal:** Deliver a polished, efficient, reliable PSP-only Android library with a simplified premium interface and hardened file access.

**Architecture:** Keep Java 17 Android Native, SQLiteOpenHelper, Storage Access Framework, and official PPSSPP handoff. Introduce a pure-Java filter and URI-grant policy, a recycled ListView adapter, and a focused home screen with no unsupported platform surfaces.

**Tech Stack:** Java 17, Android SDK 36, AGP 8.10.1, Gradle 8.11.1, JUnit 4.13.2, Android native Views/ListView, GitHub Actions.

**Spec:** docs/superpowers/specs/2026-11-12-nexus-v08-premium-experience-design.md

## Global Constraints
- minSdk 23; compileSdk 36; targetSdk 36.
- JDK 17, Android Gradle Plugin 8.10.1, Gradle 8.11.1.
- No internet permission, ads, telemetry, remote APIs, game downloads, or game/BIOS/firmware binaries.
- Arabic RTL default; English, dark theme, and light theme remain supported.
- Only PSP ISO, CSO, PBP, and ZSO files may enter the active library.
- Preserve historical SQLite rows; hide unsupported platforms without destructive migration.
- Do not build the APK until the final verification stage.

## Review Focus
- Empty library vs. filtered-empty library — verify the correct message and recovery action.
- Arabic RTL with long game names and narrow screens — verify no clipped controls and 48dp targets.
- Providers without persistable read permission — reject safely without storing inaccessible entries.
- Duplicate URIs and database insert failures — avoid leaking persisted grants.
- Revoked or moved document URIs — show a recoverable launch error and keep the app responsive.

---

### Task 1: Add pure-Java regression tests
**Files:** Create GameLibraryFilter.java and GameLibraryFilterTest.java; create UriGrantPolicy.java and UriGrantPolicyTest.java.
- [x] Write tests for all/favorites filtering, case-insensitive search, null-safe names, empty inputs, and URI scheme/read/persistable flags.
- [x] Run focused tests and confirm they fail because policy classes do not exist.
- [x] Implement GameLibraryFilter.filter(List<GameEntry>, String, boolean) and UriGrantPolicy.canPersistReadPermission(int, String).
- [x] Re-run focused tests and confirm they pass.

### Task 2: Harden library persistence
**Files:** Modify GameDatabase.java.
- [x] Add containsUri(String) using a parameterized query.
- [x] Upgrade database schema to version 3 with a non-destructive PSP library query index.
- [x] Keep existing records and indexes; do not drop or rewrite user data.
- [x] Run full JVM tests and Android lint.

### Task 3: Build the recycled game-row component
**Files:** Create NexusPalette.java and GameListAdapter.java; modify styles.xml.
- [x] Define complete dark/light palette tokens from the design spec.
- [x] Implement a recycled BaseAdapter/ViewHolder game card with localized labels and 48dp favorite/play actions.
- [x] Ensure row actions do not trigger the row-details click.
- [ ] Run full JVM tests and Android lint.

### Task 4: Rebuild the home screen
**Files:** Modify MainActivity.java and AppStrings.java.
- [x] Replace the permanent hero + ScrollView list with a compact top bar, collection summary, search, All/Favorites chips, recycled ListView, and fixed import action.
- [x] Hide search and filter controls when no PSP games exist.
- [x] Add distinct empty-library and no-results states with working recovery behavior.
- [x] Keep details, favorite, play, remove, settings, theme, language, and about actions functional.
- [x] Preserve Arabic RTL and English layout direction across the screen and dialogs.
- [ ] Run full JVM tests and Android lint.

### Task 5: Harden SAF import lifecycle
**Files:** Modify MainActivity.java and GameDatabase.java; test UriGrantPolicy.
- [x] Use UriGrantPolicy before taking persisted permissions.
- [x] Detect duplicate URIs before taking grants.
- [x] If an insert fails, release the just-taken permission only if no library row references that URI.
- [x] Keep imports on the worker executor and post results only to a live resumed activity.
- [ ] Verify single/multiple selection, unsupported file rejection, and no-crash error handling by code review and CI smoke test.

### Task 6: Update release controls and documentation
**Files:** Modify android-build.yml, README.md, PRODUCT_SPEC.md, ENGINE_INTEGRATION.md; create CHANGELOG.md.
- [x] Keep verification (JVM tests, lint, source/privacy checks) on pushes and pull requests.
- [x] Gate APK packaging and emulator smoke tests behind an explicit manual workflow input.
- [x] Document real features, unsupported platforms, data privacy, build commands, and debug-signing limitations.
- [x] Run full CI verification without invoking APK packaging.

### Task 7: Final verification and APK (pending)

The APK has deliberately not been assembled yet. The latest CI run verified JVM tests and lint only. The emulator install/launch and visible UI check are reserved for the final marked build commit.
**Files:** Generated app-debug.apk, CI artifact, and SHA-256 checksum.
- [ ] Re-read the spec and verify every acceptance criterion.
- [ ] Run full JVM tests, Android lint, and source/privacy checks.
- [ ] Only after all checks pass, dispatch the workflow with build_apk=true.
- [ ] Verify APK signature and checksum.
- [ ] Install and launch the APK on the Android emulator.
- [ ] Download the exact artifact, verify the APK file and checksum, and report remaining physical-device limitations.