# SDD ledger — plan: docs/superpowers/plans/2026-11-12-nexus-v08-premium-experience.md

## Branch and baseline
- Working branch: redesign/nexus-v0.8-mobile-premium
- Baseline commit: 2d55ccf689afae1fb692a8f7879c8c1cf5060a48 (v0.7.0 source branch)
- Current implementation commit: 3ac5671450b04f18dffd0ff1463d131888cdf9bf
- The default main branch has a separate lineage from the active v0.7.0 redesign branch. Changes are isolated on this feature branch; no merge or force-push to main has been performed.

## Pre-flight interfaces
- Task 1 → Task 4: GameLibraryFilter.filter(List<GameEntry>, String, boolean) is consumed by MainActivity. Signature matches.
- Task 1 → Task 5: UriGrantPolicy.canPersistReadPermission(int, String) is consumed by the SAF import path. Signature matches.
- Task 2 → Task 5: GameDatabase.containsUri(String) is consumed before taking persisted grants and after failed inserts. Signature matches.
- Task 3 → Task 4: NexusPalette and GameListAdapter are consumed by MainActivity. Constructors and callbacks match.
- Ruling: keep historical non-PSP rows in SQLite and hide them from the active PSP library — preserving user data is safer than destructive cleanup; the cost if wrong is unused legacy rows remaining stored.

## Task results
- Task 1: complete. Added filter and URI-grant tests. RED verified in run 43233688731: compilation failed specifically because GameLibraryFilter and UriGrantPolicy were not implemented. GREEN verified in run 43234915637 and the latest run 43235383215.
- Task 2: complete. Added parameterized containsUri lookup and non-destructive schema v3 index. Full JVM tests and lint passed in run 43235383215.
- Task 3: complete. Added NexusPalette and recycled GameListAdapter; removed unbounded ScrollView row creation. Android Lint initially caught API 27 navigation-bar theme attributes on minSdk 23 and a hardcoded platform label. Fixed both; run 43234915637 passed.
- Task 4: complete. Rebuilt the home screen: compact header, collection count, search, All/Favorites, recycled list, separate empty/no-results states, persistent import action, simplified settings. Arabic RTL and English retained.
- Task 5: implementation complete. Import checks supported PSP formats, duplicate URIs, content URI scheme, and both read+persistent grant flags; it releases a newly acquired grant after failed insertion only when no row references the URI. Imports remain on a worker executor. The final emulator smoke test will verify UI startup; real-world document-provider behavior and actual PPSSPP game launch are not exercised by CI.
- Task 6: complete. Updated README/product/engine docs/changelog; verification runs on pushes; APK packaging is gated by manual input or the final [build-apk] commit marker.
- Task 7: pending. Do not mark complete until APK assembly, apksigner verification, emulator installation/launch, UI hierarchy assertions, screenshot artifact, and SHA-256 verification all succeed.

## Verification evidence
- RED: run 43233688731 — missing production classes caused the intended compile failure.
- First GREEN: run 43234915637 — JVM tests and Android Lint passed after API-level lint fixes.
- Latest verification: run 43235383215 — source/privacy checks, :app:testDebugUnitTest, and :app:lintDebug all succeeded; BUILD SUCCESSFUL.
- No APK has been built from v0.8.0 yet.

## Rulings
- Ruling: keep Android Native Java and native ListView instead of migrating to Compose or adding a UI runtime dependency — the project is already Java-only and a recycled ListView fixes the concrete scaling issue with less dependency and migration risk; the cost if wrong is less expressive UI tooling than Compose.
- Ruling: keep minSdk 23 and remove API 27-only navigation-bar theme attributes from base resources — compatibility is a product requirement; runtime guarded system-bar handling covers newer devices; the cost is a dark navigation bar in light theme on Android 6.0–7.1 because those releases do not support dark navigation icons.
- Ruling: build a debug-signed installable APK — no user-owned release keystore is available and private signing secrets must not be requested in chat; the cost is that this artifact is not suitable for Play Store publishing or seamless updates over an APK signed by another key.
