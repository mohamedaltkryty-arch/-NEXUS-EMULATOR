# NEXUS UI Redesign and Reliability Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Deliver a polished, reliable Android PSP game library and launcher while hiding unsupported emulator paths and preserving local data.

**Architecture:** Retain the existing Java 17 Android app, SQLiteOpenHelper, SharedPreferences, and external PPSSPP launch adapter. Replace the cluttered universal-catalog interface with a focused local library, harden SAF permissions and deletion, and add test-only verification before the final APK workflow.

**Tech Stack:** Java 17, Android Gradle Plugin 8.7.3, Gradle 8.9, Android SDK 35, minSdk 23, SQLiteOpenHelper, Android SAF, JUnit 4.13.2.

**Spec:** docs/superpowers/specs/2026-11-12-nexus-redesign-design.md

## Global Constraints
- Do not add INTERNET permission, accounts, telemetry, ads, remote APIs, or broad storage permissions.
- Do not bundle game files, BIOS, firmware, or encryption keys.
- Keep minSdk 23, compileSdk/targetSdk 35, Java 17, AGP 8.7.3, Gradle 8.9.
- Only ISO, CSO, PBP and ZSO are accepted as PSP launch formats.
- Only PSP launch handoff is user-visible until other runtimes are actually integrated.
- Preserve existing SQLite data and application identity.
- Do not create the final APK until all code, tests, and review work is complete.

## Review Focus
- URI grants missing READ or PERSISTABLE flags; import must fail clearly and not save an unusable record.
- URI permission revoked or source file deleted after import; launch must show recovery instead of crashing.
- Multiple selected files with mixed valid, duplicate, and unsupported types; each item must be handled independently.
- PPSSPP package installed but activity unavailable; launch must fail gracefully.
- Existing database and settings on upgrade; no reset or data loss.

---

### Task 1: Product truth and supported import policy

**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/catalog/GameFileClassifier.java`
- Modify: `app/src/test/java/com/nexus/emulator/catalog/GameFileClassifierTest.java`
- Modify: `app/src/main/java/com/nexus/emulator/engine/EngineRegistry.java` only if required to remove unused UI-facing source references.

**Interfaces:**
- `GameFileClassifier.extensionOf(String): String`
- `GameFileClassifier.classify(String): String`
- `GameFileClassifier.isSupportedPlatform(String): boolean`

- [ ] Write tests that only PSP formats are classified as PSP-importable; ISO and CSO are PSP in this product, and EXE/MSI/PKG/CHD/BIN/unknown are not importable.
- [ ] Run the focused JUnit tests and record the expected failure.
- [ ] Implement the strict PSP-only import policy.
- [ ] Run focused tests and full JVM unit tests.
- [ ] Commit the policy and tests.

### Task 2: SAF permission and library lifecycle hardening

**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/data/GameDatabase.java`
- Modify: `app/src/main/java/com/nexus/emulator/engine/PspEngine.java`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/test/java/com/nexus/emulator/engine/PspEngineTest.java`

**Interfaces:**
- Keep existing database API signatures unless a focused test justifies a safe extension.
- PSP adapter remains the only launch integration.

- [ ] Add regression tests for accepted formats and rejection of every non-PSP format.
- [ ] Verify test failures before implementation.
- [ ] Make launch validate non-null content URI, supported extension, package visibility and PPSSPP activity; preserve read grant handoff.
- [ ] Ensure removal releases persisted URI permission at the activity boundary before deleting the row.
- [ ] Pass actual result flags to permission persistence; require READ and PERSISTABLE grants.
- [ ] Handle missing document/provider exceptions with localized recovery.
- [ ] Run unit tests and inspect manifest for forbidden permissions.
- [ ] Commit reliability changes.

### Task 3: Premium focused UI and localization

**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/MainActivity.java`
- Modify: `app/src/main/java/com/nexus/emulator/util/AppStrings.java`
- Modify: `app/src/main/res/values/styles.xml`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/build.gradle`

**Interfaces:**
- MainActivity continues using GameDatabase, SettingsStore, GameEntry, PspEngine, and AppStrings.
- No new runtime library dependency.

- [ ] Replace the screen with focused library, import, search, All/Favorites, game details, play, favorite and remove.
- [ ] Hide PC/PS2/PS3, folder import and engine-source controls.
- [ ] Add premium spacing, typography, colors, accessible touch targets, RTL, English, dark and light themes.
- [ ] Ensure settings preserve database data and language/theme state.
- [ ] Ensure empty state and errors have useful actions and no inert controls.
- [ ] Run JVM tests and static source checks.
- [ ] Commit UI changes.

### Task 4: CI verification and documentation

**Files:**
- Create: `.github/workflows/verify.yml`
- Modify: `.github/workflows/android-build.yml`
- Modify: `README.md`
- Modify: `docs/PRODUCT_SPEC.md`
- Modify: `docs/ENGINE_INTEGRATION.md`
- Create: `docs/RELEASE_CHECKLIST.md`

- [ ] Add test-only CI workflow that runs JVM tests without producing an APK.
- [ ] Ensure final Android workflow builds only after unit tests and then installs/launches on API 35 emulator.
- [ ] Update docs to state PSP-only actual launch, external PPSSPP dependency, and unsupported engines hidden.
- [ ] Run the test-only workflow and inspect its logs.
- [ ] Review changed files and workflow permissions.
- [ ] Commit verification and documentation changes.

### Task 5: Final APK release gate

**Files:**
- No source changes unless a verified blocker is found.

- [ ] Confirm Tasks 1–4 are committed and test-only CI is green.
- [ ] Dispatch the Android build workflow on `nexus/redesign-v1`.
- [ ] Confirm JVM tests, APK build, artifact upload, emulator install and launch all pass.
- [ ] Download the exact APK artifact and verify package metadata and SHA-256.
- [ ] Deliver APK, source branch link, test results and remaining limitations.
