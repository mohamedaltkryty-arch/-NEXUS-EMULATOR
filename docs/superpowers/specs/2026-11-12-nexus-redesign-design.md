# NEXUS — Product and UX Redesign

Date: 2026-11-12
Status: Approved by explicit execution request; implementation in progress.

## Intent
NEXUS is an Android-first local game library with a reliable, honest PSP launch path. Its value is a calm, polished home for the user's own game files, with one-tap launch through the official PPSSPP app. The interface presents supported functionality and keeps unfinished console/desktop integrations out of the user's way.

## Audience and product promise
- Android users who store legally obtained PSP game images on their device or an accessible document provider.
- Users who want one clean library, simple import, search, favorites, and direct launch.
- Promise: organize PSP games locally and hand them off to PPSSPP. Do not promise that NEXUS itself emulates PS2, PS3, or Windows.

## Goals
1. Deliver a premium, mobile-first interface with correct Arabic RTL and English LTR.
2. Make the primary loop obvious: import a PSP game, find it in the library, launch it.
3. Expose only implemented runtime capability: PSP via external PPSSPP.
4. Preserve private local storage, SAF-scoped file permissions, SQLite metadata, favorites, and search.
5. Provide clear states for empty library, no search results, missing PPSSPP, imported games, and errors.
6. Keep the code dependency-light and compatible with Java 17 / Android SDK 35.

## Non-goals
- Embedding or compiling PPSSPP, ARMSX2, ARMSX3, or Winlator in this release.
- Listing PS2, PS3, Windows, or not-yet-playable systems as selectable products.
- Adding accounts, telemetry, ads, remote APIs, or broad storage permissions.
- Shipping game ROMs, BIOS, firmware, keys, or copyrighted content.

## Approaches considered
1. Visual-only refresh of the current screen: smallest change, but leaves misleading PC/PS2/PS3 import paths and nonfunctional catalogue states visible.
2. Rebuild the Android screen using existing native Java views: selected. Preserves the working Android project, local database and SAF integration, adds no runtime UI dependencies, and simplifies the experience around the one verified launch route.
3. Migrate to Jetpack Compose or Flutter: can provide a modern UI system, but introduces a larger migration and build surface with no required user-facing benefit for this release.

Decision: approach 2. Keep the existing project architecture and redesign the user-facing experience in native Android Java.

## Product experience

### Main library
- Branded NEXUS header with a restrained settings action.
- One clear primary action: Import PSP games.
- Small runtime status showing whether PPSSPP is installed.
- Compact collection summary.
- Search field and simple All / Favorites filter.
- Clean game rows with title, PSP format, optional file size, favorite action, details, and a prominent Play action.
- Empty state with one direct import action.
- Separate no-results state for search or Favorites.

### Settings
Only needed user-facing controls remain: Arabic/English, dark/light, and About/privacy notes. No engine registry, developer source links, or unfinished platform options appear in the UI.

### Import and launch
- Accept only .iso, .cso, .pbp, and .zso PSP files.
- Keep Android Storage Access Framework document selection and persisted read grants.
- Reject unsupported extensions with a clear message; do not ask users to guess a console for ambiguous files.
- Detect official PPSSPP and PPSSPP Gold packages and use their correct explicit activity names.
- Launch with ACTION_VIEW, CATEGORY_DEFAULT, application/octet-stream, a content URI, clip data, read grant, and task flags.
- Offer the official PPSSPP download page only when the runtime is missing.
- On removal, release the persisted URI read grant where Android permits it.

## Visual system
- Dark default: deep navy canvas, layered blue-slate surfaces, violet primary action, mint runtime-success state, high-contrast text.
- Light mode: near-white cool canvas, white surfaces, restrained violet accent.
- 8dp spacing rhythm, generous safe area, 16–22dp corner radii, 48dp minimum touch targets, clear typography hierarchy.
- Use native shapes and system fonts; no remote images, unnecessary assets, or heavyweight visual libraries.
- All layout direction, labels, dialogs, and accessibility descriptions work in Arabic RTL and English LTR.

## Architecture and data
- Retain one Android Activity and existing SQLiteOpenHelper / SharedPreferences / SAF design.
- Simplify file classification so only valid PSP formats enter the playable library.
- Keep the engine registry truthful: only PPSSPP is launch-ready, and it remains an external runtime.
- No new permission, database schema, networking, analytics, or third-party dependency is required.

## Security and privacy
- Preserve allowBackup=false and no Internet permission.
- Continue using SAF user-granted content URIs and persist only read permission.
- Avoid broad file access, file upload, telemetry, logs containing user file paths, or bundling ROM/BIOS content.
- Release persisted document permission when a game is removed.
- Use explicit PPSSPP package/activity targets and catch launch failures.

## Acceptance criteria
- Only PSP files can be imported into the playable library.
- ISO/CSO/PBP/ZSO are accepted case-insensitively; non-PSP formats are rejected.
- The visible screen has no nonfunctional PS2/PS3/Windows controls.
- Import, duplicate handling, search, favorites, details, removal, settings, language, and theme remain functional.
- PPSSPP missing/present states are truthful; PSP launch passes the selected content URI with read access.
- Arabic and English layouts are legible and correctly directed.
- JVM tests pass; debug APK builds; Android emulator install and launch smoke test pass.
- Final artifact is created only after code review, tests, and CI verification.
