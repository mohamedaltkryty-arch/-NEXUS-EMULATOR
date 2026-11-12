# NEXUS Premium PSP Library Redesign Implementation Plan

> **For agentic workers:** Use task-by-task execution. Each task ends with a fresh verification run.

**Goal:** Restore and redesign NEXUS as a polished, dependable Android PSP game-library companion, hiding unsupported platforms and fixing its import/launch boundaries.

**Architecture:** Retain Java 17, Android SDK 35, SQLiteOpenHelper, Storage Access Framework and external official PPSSPP handoff. Restrict the active product policy to PSP formats; retain but hide legacy non-PSP rows.

**Tech Stack:** Java 17, Android Native Views, Android SDK 35, Gradle 8.9, JUnit 4.13.2, GitHub Actions.

**Spec:** `docs/NEXUS_REDESIGN.md`

## Global Constraints
- minSdk 23; targetSdk 35; compileSdk 35.
- JDK 17 and Gradle 8.9.
- Preserve offline-first design; no internet permission, ads, telemetry or remote APIs.
- Never package games, BIOS, firmware, keys or third-party emulator binaries.
- Arabic RTL is default; English and dark/light modes remain supported.
- Never advertise platforms that are not actually launchable.

## Review Focus
- Mixed/uppercase filenames and URI display names — test the PSP classifier.
- Unsupported or misleading extensions — test exact allow-list and reject bin/CHD/PKG/EXE.
- Multiple selections and missing persistable flags — test grant handling and exercise via Android emulator flow.
- Moved/deleted files and revoked URI access — validate descriptors and show recovery guidance.
- Old non-PSP database entries — test filtering and ensure no destructive deletion.

---

### Task 1: Define PSP-only policy tests
**Files:**
- Create: `app/src/test/java/com/nexus/emulator/catalog/PspOnlyPolicyTest.java`
- Modify: `app/src/test/java/com/nexus/emulator/catalog/GameFileClassifierTest.java`

- [x] Added regression tests. CI must demonstrate the current classifier fails them before production changes.

### Task 2: Correct the active game-file policy
**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/catalog/GameFileClassifier.java`
- Test: `app/src/test/java/com/nexus/emulator/catalog/PspOnlyPolicyTest.java`

- [ ] Accept only ISO, CSO, PBP, ZSO as PSP and reject other platforms.
- [ ] Run complete JVM test suite.

### Task 3: Redesign the mobile library experience
**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/MainActivity.java`
- Modify: `app/src/main/res/values/styles.xml`

- [ ] Premium responsive header/hero/library count/search/All-Favorites filters/game cards/empty states.
- [ ] Remove PC/PS2/PS3 filters and folder-import UI; retain settings only for language/theme/about.
- [ ] Keep every visible control functional and accessible.

### Task 4: Harden SAF import and PSP launch
**Files:**
- Modify: `app/src/main/java/com/nexus/emulator/MainActivity.java`
- Modify: `app/src/main/java/com/nexus/emulator/engine/PspEngine.java`
- Test: `app/src/test/java/com/nexus/emulator/engine/PspEngineTest.java`

- [ ] Persist only read permissions actually granted by Android.
- [ ] Handle multi-select, duplicate, unsupported, null, inaccessible and moved URIs.
- [ ] Validate the selected URI can be opened before PPSSPP handoff.
- [ ] Release persisted permission only after a successful removal from the library.

### Task 5: Harden product and build documentation
**Files:**
- Modify: `README.md`
- Modify: `docs/PRODUCT_SPEC.md`
- Modify: `docs/ENGINE_INTEGRATION.md`
- Modify: `.github/workflows/android-build.yml`

- [ ] Document current actual capabilities and limitations.
- [ ] CI must run tests before APK build.
- [ ] Add debug build + checksum + emulator install/launch job only after verification is green.

### Task 6: Final review and release artifact
**Files:**
- Generated: `app/build/outputs/apk/debug/app-debug.apk`
- Generated: CI artifact ZIP and checksum.

- [ ] Run complete JVM suite.
- [ ] Build APK as the final pipeline stage.
- [ ] Install and launch on Android Emulator.
- [ ] Download and validate final APK artifact; record checksum.
