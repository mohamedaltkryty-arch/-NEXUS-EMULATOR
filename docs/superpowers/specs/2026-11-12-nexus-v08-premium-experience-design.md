# NEXUS 0.8 — Focused Premium Android Experience

## Intent
NEXUS is a focused, offline-first Android library for PSP games the owner already has. The everyday path is import a supported PSP file, find it, organize it, and launch it through the official PPSSPP Android application. The interface should feel deliberate and premium without exposing developer internals or platforms that cannot actually run.

## Goals
- Make the PSP library the only visible product surface.
- Keep the primary actions visible: import, search, All/Favorites, game details, favorite, launch, remove, and settings.
- Rebuild the home screen around the user's collection rather than a permanent marketing hero.
- Hide search and filter controls while the library is empty; show a concise first-run state with one clear import action.
- Keep the import action available in a fixed bottom action area.
- Use a recycled list adapter so large collections do not inflate every row at once.
- Improve Arabic RTL, English, dark/light themes, accessibility labels, touch targets, and small-screen behavior.
- Harden persisted URI grants and cleanup so failed or duplicate imports do not leave unnecessary permissions.
- Preserve the user's existing SQLite records and SAF document references.

## Non-goals
- Do not embed or redistribute PPSSPP or any other emulator binary.
- Do not claim PS2, PS3, Windows/PC, or other platforms are playable.
- Do not add accounts, cloud sync, ads, analytics, telemetry, remote APIs, game downloads, BIOS/firmware, or commercial game files.
- Do not add decorative controls, fake status labels, or settings without working behavior.
- Do not replace the Java Android app with a new framework or add a UI dependency solely for appearance.

## Product decisions
- Keep Android Native Java 17, minSdk 23, compile/target SDK 36, AGP 8.10.1, Gradle 8.11.1.
- Keep SQLiteOpenHelper and Storage Access Framework. The app stores metadata and document URIs; it does not upload or copy game files.
- Use a native ListView with a recycled ViewHolder adapter. This avoids a new runtime dependency while fixing the current ScrollView's unbounded view inflation.
- Use one home screen, a settings dialog, and a game-details dialog. No bottom navigation because there is only one primary destination.
- Use a compact top bar, collection summary, search, two filters, a scrollable game list, and a persistent import action. In the empty state, show only the brand, a first-game message, and the import action.
- Keep Arabic as the default language and apply layout direction to all major containers. Preserve language/theme preferences.
- Only ISO, CSO, PBP, and ZSO are accepted into the active PSP library.
- Before taking a persistable permission, check whether the URI is already in the library. If an insert fails after a new grant, release that grant only when no library row references the URI.
- Keep the CI verification job active on pushes and pull requests. APK packaging and emulator installation are manual-dispatch gated and run only after verification is green.

## Visual system
### Dark theme
- Background #0B1020; surface #131C2E; elevated surface #19243A; border #293752.
- Primary text #F4F7FF; secondary text #A6B3C8; accent #8B7CFF; accent-soft #29254E; positive #42D6B0.
### Light theme
- Background #F4F6FB; surface/elevated surface #FFFFFF; border #E1E7F0.
- Primary text #172033; secondary text #66748C; accent #5D4BE0; accent-soft #EFEDFF; positive #168F78.
### Layout and interaction
- 8dp spacing rhythm; 20dp horizontal page margins; 16–20dp card radii.
- Minimum interactive target: 48dp.
- Clear type hierarchy: brand/title, section title, game title, metadata, supporting text.
- Avoid emoji as primary controls; icon-only controls must have localized content descriptions.
- Game rows show platform mark, filename/title, format and file size, favorite, and launch.
- Empty and no-results states have distinct copy and a working recovery action.
- Settings contains only language, theme, and about.

## Architecture and data
- MainActivity owns lifecycle, screen composition, settings, dialogs, and SAF result handling.
- NexusPalette owns theme colors.
- GameListAdapter owns recycled game-row views and row actions.
- GameLibraryFilter is a pure Java filter for all/favorites and case-insensitive search.
- UriGrantPolicy is a pure Java gate for content URI read+persistent grant eligibility.
- GameDatabase remains the only library persistence layer. Add URI existence lookup and a non-destructive query index migration.
- PspEngine remains the sole launch adapter and validates the URI can still be opened before handing it to PPSSPP.

## Acceptance criteria
1. Active library and import policy accept only PSP ISO/CSO/PBP/ZSO files.
2. Historical non-PSP rows remain stored but are hidden and excluded from the active count.
3. Home screen shows only working PSP actions and hides empty-library search/filter controls.
4. Game list uses recycled rows and does not inflate the full collection inside a ScrollView.
5. Search, All/Favorites, favorite toggle, details, remove, settings, theme, and language work.
6. Import handles one/multiple selections, null/missing metadata, unsupported files, duplicates, revoked/non-persistable grants, and database failures without crashing.
7. Persistable permissions are taken only when Android grants both read and persistable access; failed inserts do not orphan a permission.
8. Removing a game releases its persisted read grant only after the database row is deleted.
9. Database migration is non-destructive and old rows remain intact.
10. JVM tests cover filter behavior, PSP allow-list, URI grant policy, and engine launch eligibility.
11. CI verifies JVM tests, Android lint, and source/privacy policy on each push; it does not package an APK automatically.
12. Only the final manual workflow dispatch builds, signature-checks, installs, and launches the APK on an Android emulator.
13. Documentation distinguishes tested behavior from device/game compatibility and explains that the artifact is debug-signed, not a private-key Play Store release.