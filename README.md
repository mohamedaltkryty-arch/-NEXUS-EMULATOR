# NEXUS EMULATOR

**Android-first game library and multi-engine frontend.** Target device: POCO X4 GT (Dimensity 8100, Mali-G610 MC6, 12 GB RAM, 256 GB storage as reported by the owner).

> Current milestone is a functioning game-library launcher. Four upstream engine sources are linked as pinned Git submodules but are not embedded into the APK yet. This build does not execute game files.

## Working features

- Arabic RTL by default and English language switch.
- Dark and light themes.
- Local SQLite game library with search, platform filters and favorites.
- Single/multi-file import through Android Storage Access Framework.
- Folder import for installed Windows game folders or PS3 game folders.
- Manual platform selection for ambiguous ISO/BIN/CSO images.
- Local storage of URI references; game binaries are not copied or uploaded.
- Four-engine source registry that reports runtime integration status.
- GitHub Actions: JVM unit tests, debug APK build and Android emulator smoke launch.

## Engine source modules

| Platform | Engine | Source | License |
|---|---|---|---|
| PSP | PPSSPP | https://github.com/hrydgard/ppsspp | GPL-2.0-or-later |
| PS2 | ARMSX2 | https://github.com/ARMSX2/ARMSX2 | GPL-3.0 |
| Windows | Winlator app | https://github.com/brunodev85/winlator-app | LGPL-2.1 |
| PS3 | ARMSX3 | https://github.com/ARMSX2/ARMSX3 | GPL-2.0-only |

Clone with all pinned submodules:

    git clone https://github.com/mohamedaltkry-arch/-NEXUS-EMULATOR.git NEXUS-EMULATOR
    cd NEXUS-EMULATOR
    git submodule update --init --recursive

## Build

Requirements: JDK 17, Android SDK Platform 35, Build Tools 35.0.0, Gradle 8.9.

    gradle --no-daemon testDebugUnitTest assembleDebug

APK: app/build/outputs/apk/debug/app-debug.apk.

CI runs JVM tests, builds the APK, installs it on an Android emulator and launches the main activity. A successful run uploads a debug-signed APK artifact under the Actions run.

## Importing games

- PSP: .ISO, .CSO, .PBP, .ZSO.
- PS2: .ISO, .CHD, .CSO, .BIN.
- PS3: .PKG or a PS3 game folder.
- Windows: import the complete installed game folder. An EXE by itself is not a complete game.

The picker asks the user to grant access. NEXUS stores document URIs in its private SQLite database and does not copy or upload game binaries.

## Not yet integrated

- Native PPSSPP, ARMSX2, Winlator and ARMSX3 runtime adapters.
- BIOS, firmware, keys or commercial game files.
- Universal game compatibility guarantees.

A source submodule is not a running emulator. A platform is marked ready only after its native runtime is integrated and verified on real devices.

## Security and privacy

- No INTERNET permission or remote API.
- No account or analytics.
- SAF-only access to user-selected files and folders.
- SQLite ContentValues and selection arguments; no shell execution.
- Android auto-backup disabled.
- Do not upload game images, console BIOS, PS3 firmware or keys to this public source repository.

See docs/PRODUCT_SPEC.md, docs/DATABASE_AND_SECURITY.md, and docs/ENGINE_INTEGRATION.md.

## Licenses

Each engine remains a separate Git submodule and retains its own notices and license. Audit transitive dependencies and fulfil the exact corresponding-source and attribution requirements before distributing a combined APK. ARMSX2 (GPL-3.0) and ARMSX3 (GPL-2.0-only) must remain separately identifiable; do not assume those licenses are automatically compatible for combined derivative code.
