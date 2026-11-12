# SDD ledger — plan: docs/superpowers/plans/2026-11-12-nexus-v08-premium-experience.md

## Branch and baseline
- Working branch: redesign/nexus-v0.8-mobile-premium
- Baseline commit: 2d55ccf689afae1fb692a8f7879c8c1cf5060a48 (v0.7.0 source branch)
- Current implementation commit: f96519ca09e72da80997b27744c9f56f5a897abe
- The default main branch has a separate lineage from the active v0.7.0 redesign branch. Changes are isolated on this feature branch; no merge or force-push to main has been performed.

## Pre-flight interfaces
- Task 1 → Task 4: GameLibraryFilter.filter(List<GameEntry>, String, boolean) is consumed by MainActivity. Signature matches.
- Task 1 → Task 5: UriGrantPolicy.canPersistReadPermission(int, String) is consumed by the SAF import path. Signature matches.
- Task 2 → Task 5: GameDatabase.containsUri(String) is consumed before taking persisted grants and after failed inserts. Signature matches.
- Task 3 → Task 4: NexusPalette and GameListAdapter are consumed by MainActivity. Constructors and callbacks match.
- Ruling: keep historical non-PSP rows in SQLite and hide them from the active PSP library — preserving user data is safer than destructive cleanup; the cost if wrong is unused legacy rows remaining stored.

## Task results
- Task 1: complete. Added filter and URI-grant tests. RED verified in run 43233688731: compilation failed specifically because GameLibraryFilter and UriGrantPolicy were not implemented. GREEN verified in run 43234915637 and the latest run 43236362187.
- Task 2: complete. Added parameterized containsUri lookup and non-destructive schema v3 index. Full JVM tests and lint passed in run 43235383215.
- Task 3: complete. Added NexusPalette and recycled GameListAdapter; removed unbounded ScrollView row creation. Android Lint initially caught API 27 navigation-bar theme attributes on minSdk 23 and a hardcoded platform label. Fixed both; latest run 43236362187 passed.
- Task 4: complete. Rebuilt the home screen: compact header, collection count, search, All/Favorites, recycled list, separate empty/no-results states, persistent import action, simplified settings. Arabic RTL and English retained. Refined Arabic empty-state/count copy and ellipsized metadata on narrow screens.
- Task 5: implementation complete. Import checks supported PSP formats, duplicate URIs, content URI scheme, and both read+persistent grant flags; it releases a newly acquired grant after failed insertion only when no row references the URI. Imports remain on a worker executor. The final emulator smoke test will verify UI startup and visible UI hierarchy; real-world document-provider behavior and actual PPSSPP game launch are not exercised by CI.
- Task 6: complete. Updated README/product/engine docs/changelog; verification runs on pushes; APK packaging is gated by manual input or the final [build-apk] commit marker.
- Task 7: in progress. APK assembly, apksigner verification, artifact upload and SHA-256 generation succeeded in run 43236936676. API 36 emulator smoke did not complete; retrying on API 35 with install/foreground-launch checks and a screenshot. Do not mark complete until the retry succeeds.

## Verification evidence
- RED: run 43233688731 — missing production classes caused the intended compile failure.
- First GREEN: run 43234915637 — JVM tests and Android Lint passed after API-level lint fixes.
- Latest verification: run 43236362187 on commit 6bb5cd177725cbc9410d838bbbdc6ec86dede0b6 — source/privacy checks, :app:testDebugUnitTest, and :app:lintDebug all succeeded; BUILD SUCCESSFUL in 45 seconds.
- An initial v0.8.0 APK was assembled and signature-verified in run 43236936676. The API 36 emulator smoke step did not finish within the expected window, so that artifact is not accepted as final. The next marked build uses API 35 (the previously working emulator target), checks install/foreground launch, and uploads a screenshot.

## Rulings
- Ruling: keep Android Native Java and native ListView instead of migrating to Compose or adding a UI runtime dependency — the project is already Java-only and a recycled ListView fixes the concrete scaling issue with less dependency and migration risk; the cost if wrong is less expressive UI tooling than Compose.
- Ruling: keep minSdk 23 and remove API 27-only navigation-bar theme attributes from base resources — compatibility is a product requirement; runtime guarded system-bar handling covers newer devices; the cost is a dark navigation bar in light theme on Android 6.0–7.1 because those releases do not support dark navigation icons.
- Ruling: build a debug-signed installable APK — no user-owned release keystore is available and private signing secrets must not be requested in chat; the cost is that this artifact is not suitable for Play Store publishing or seamless updates over an APK signed by another key.


## Final pre-build review
- MainActivity uses a recycled ListView; the permanent hero and unsupported platform filters are removed from the active screen.
- Search and All/Favorites controls are not rendered when the PSP library is empty.
- Import CTA remains visible; file types are constrained by the classifier to PSP ISO/CSO/PBP/ZSO.
- URI permissions are read-only, require both read and persistable flags, and duplicate checks precede permission acquisition.
- Existing non-PSP rows are retained; the active UI does not advertise those platforms as playable.
- System bar handling is guarded by Android API level; Android 6.0–7.1 keeps a dark navigation bar in light theme for icon contrast.
- CI has not assembled an APK before this final stage.

- Initial final-stage run 43236936676: verify job succeeded; build job succeeded (APK assembled, signature verified, SHA-256 uploaded); API 36 emulator smoke remained in progress. Retrying smoke on API 35 due runner stall, not a compilation or signing failure.
