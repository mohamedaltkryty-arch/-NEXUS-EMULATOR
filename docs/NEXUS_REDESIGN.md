# NEXUS — Premium PSP Library Redesign

## Product decision
NEXUS is a focused Android PSP game-library companion. It does not claim to emulate consoles itself: game execution is delegated to the official PPSSPP Android app. The app's visible product surface contains only working PSP-library actions.

## Audience and outcome
Android users who legally own PSP game dumps and want one polished place to import, search, organize, favorite, and launch them. Success means a game can be imported once, survives process/device restarts through persisted SAF access, appears in the library, and can be handed to PPSSPP with read access.

## Scope
- Include: PSP ISO, CSO, PBP, ZSO; multi-select import; local library; search; favorites; details; remove; Arabic RTL default; English; dark/light; official PPSSPP handoff.
- Exclude from active UI and import: PS2, PS3, Windows/PC, generic folders, developer tooling, engine source catalogs, accounts, ads, telemetry, remote APIs, game downloads, BIOS/firmware and commercial game files.
- Legacy non-PSP database entries are retained but hidden; no destructive migration is performed.

## UX direction
A mobile-first premium utility interface: clear NEXUS identity, one dominant import action, concise collection count, search, two simple filters (All/Favorites), clean game cards with file metadata, and a quiet settings surface. No disabled platform selectors, fake capability labels, or controls with no behavior. The UI follows system text direction and persists language/theme.

## Architecture
Keep the existing Java 17 Android Native app and SQLite database. Keep the Storage Access Framework for user-authorized file selection; take only the read grant actually returned by Android and persist it. A PSP policy classifies only four supported extensions. A single PPSSPP adapter validates the saved content URI before issuing ACTION_VIEW with a temporary read grant. No additional backend or network permission is introduced.

## Acceptance criteria
1. Only PSP formats can enter the active library.
2. Old non-PSP entries are not shown or counted.
3. The visible UI exposes only working features.
4. Multi-file SAF import persists read grants correctly and handles unsupported/duplicate files without crashing.
5. Favorites, search, detail, delete, settings, and theme/language persist and work.
6. Unreadable or moved files show a clear recoverable error.
7. Tests cover format policy, library filtering, and launch eligibility.
8. CI runs tests before any Android artifact build.
9. Final debug APK is built only after verification, then installed/launched on an Android emulator.
10. No assertion of physical-device or universal game compatibility without a device/game test.
