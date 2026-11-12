# NEXUS — Product and UI Redesign Design

Date: 2026-11-12
Status: Approved by explicit execution instruction in the user request; implementation proceeds autonomously.

## Intent
Turn the recovered NEXUS Android source into a polished, honest, local-first PSP game library and launcher. The current product source describes a universal game catalogue, but only PSP has a working launch handoff (to the separately installed official PPSSPP app). The redesign must make the working path obvious and remove dead-end controls.

## Goals
- A premium, compact mobile-first Android interface with Arabic RTL by default, English, dark and light themes.
- One clear primary action: import PSP games.
- Local library with search, favorites, details, removal, and launch.
- Support only the file formats the current PSP integration explicitly handles: ISO, CSO, PBP, ZSO.
- Persist document read permissions correctly and handle revoked/missing file access without crashing.
- Preserve privacy: no account, telemetry, ads, network permission, broad storage permission, game ROMs, BIOS or firmware bundled.
- Keep existing application identity, SQLite library data, and package compatibility for upgrade path.
- Accurate feature status: do not expose PC/PS2/PS3 catalog/import paths while their runtimes are not integrated.

## Non-goals
- Embedding or compiling PPSSPP, ARMSX2, ARMSX3, or Winlator.
- Claiming PS2/PS3/Windows playback.
- Downloading commercial games, BIOS, firmware, keys, or copyrighted content.
- Adding online accounts, remote services, analytics, ads, or a cloud library.

## Anti-goals
- Fake launch buttons, engine pages, unsupported platform filters, placeholder actions, or claims of universal compatibility.
- Excessive dashboard cards, settings, menus, or decorative elements that obscure the game library.
- Breaking access to existing locally saved games or persisted SAF permissions.

## Current source findings
- Android Java 17, Gradle 8.9, Android Gradle Plugin 8.7.3, compile/target SDK 35, min SDK 23.
- SQLiteOpenHelper local library and SharedPreferences settings already exist.
- The PSP adapter delegates to official PPSSPP packages and passes a content URI.
- PC/PS2/PS3 are catalog-only; engine source submodules are not APK runtimes.
- Current UI exposes unsupported platforms and folder cataloguing; current import flow does not carefully account for returned grant flags or permission loss.
- The current main branch had been emptied; work is isolated on branch `nexus/redesign-v1`, based on the last complete source commit.

## Approaches considered
1. Keep the current universal catalog UI and label unavailable engines. Rejected because it continues to foreground non-working paths and adds clutter.
2. Rebuild the frontend with Compose/Material 3. Rejected for this pass because it adds a migration and dependency surface to a working Java 17 project and makes preserving current behavior/data riskier.
3. Retain the native Java stack, rebuild the UI with a small design system, and focus the product on the actually playable PSP workflow. Selected because it preserves the source architecture and stored library while removing dead-end flows and keeping the build dependency-light.

## Product definition
NEXUS is a local-first PSP game library and launcher for Android. Users import game files they own, organize them, search or favorite them, then hand off playback to the official PPSSPP Android app. NEXUS does not contain the emulator runtime or games.

## UX structure
- Home: NEXUS brand, small settings action, library count, concise import hero, search, All/Favorites filter, clean game cards.
- Empty state: one explanation and one import action.
- Game card: platform chip, title, file format, favorite control, play action; tap opens details.
- Settings: language, theme, privacy/product information only.
- Hide unsupported platforms, engine-source management, and folder import.
- Consistent spacing, accessible touch targets, contrast, focus/pressed states, responsive scrolling, Arabic/English labels.

## Technical decisions
- Keep Java 17 and existing Android Gradle Plugin 8.7.3 / Gradle 8.9.
- Keep SQLiteOpenHelper and SharedPreferences; avoid new runtime dependencies.
- Use ACTION_OPEN_DOCUMENT with multiple selection, validate each file extension, persist only the granted read permission, and surface failures.
- Release persisted URI permission when a library entry is removed.
- Validate the content URI and external PPSSPP availability at launch; show actionable recovery.
- Keep INTERNET permission absent and avoid analytics or remote services.
- Keep debug APK as the installable CI artifact; a Play Store release requires a user-owned, securely managed signing key and is outside this pass.

## Acceptance criteria
- App compiles with JDK 17, SDK 35 and Gradle 8.9.
- Unit tests cover accepted/rejected PSP formats and platform restrictions.
- Existing local database schema remains readable; no destructive migration.
- Multi-file import handles duplicates, unsupported files and missing persistable grants safely.
- Removing a game also releases its persisted URI permission.
- Launch works only for PSP and gracefully handles missing PPSSPP, inaccessible URIs and activity-not-found cases.
- Arabic RTL/English and dark/light work without losing saved library data.
- Unsupported platform and engine-source controls are absent from the user-facing UI.
- CI unit tests pass, then APK build succeeds, then emulator install/launch smoke test passes.
- Report exact verification performed and any remaining limitations.
