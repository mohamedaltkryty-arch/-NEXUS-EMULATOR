# NEXUS EMULATOR

Android-first multi-engine game launcher for the POCO X4 GT target device.

**Current milestone: repository bootstrap + engine source pinning.** The Android app currently provides a launcher/library foundation. The upstream projects are added as Git submodules, but they are **not yet linked into the APK**. Do not treat this bootstrap APK as an emulator.

## Target device profile

- Device: POCO X4 GT
- SoC: MediaTek Dimensity 8100
- GPU: Mali-G610 MC6
- ABI: arm64-v8a
- RAM: 12 GB (user-reported)
- Storage: 256 GB

Performance and compatibility are game-specific. PS3 and Windows PC emulation must not be promised for every title on this device.

## Engine sources

| Platform | Source | Pinned revision | License |
|---|---|---|---|
| PSP | [PPSSPP](https://github.com/hrydgard/ppsspp) | See `engines.lock.json` | GPL-2.0-or-later |
| PS2 | [ARMSX2](https://github.com/ARMSX2/ARMSX2) | See `engines.lock.json` | GPL-3.0 |
| Windows | [Winlator app source](https://github.com/brunodev85/winlator-app) | See `engines.lock.json` | LGPL-2.1 |
| PS3 | [ARMSX3](https://github.com/ARMSX2/ARMSX3) | See `engines.lock.json` | GPL-2.0-only |

The exact upstream sources are mounted at `engines/<name>` as Git submodules. They remain separate upstream projects with their own history and license files. This repository does not relicense them.

## Get the full source tree

Clone the repository and initialize all engine submodules:

```bash
git clone https://github.com/mohamedaltkryty-arch/-NEXUS-EMULATOR.git NEXUS-EMULATOR
cd NEXUS-EMULATOR
git submodule update --init --recursive
```

The pinned upstream revisions are intentional. Do not update submodules to moving branches without checking compatibility, build requirements, and license notices.

## Build the Android launcher

Requirements: JDK 17, Android SDK Platform 35, Build Tools 35.0.0, and Gradle 8.9.

```bash
gradle --no-daemon :app:assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

A GitHub Actions workflow builds the launcher APK and uploads it as a workflow artifact on pushes and manual dispatches. The workflow does not compile the four native engines yet.

## Engine integration status

- [x] Create Android project and launcher/library foundation.
- [x] Add upstream source repositories as pinned Git submodules.
- [x] Add reproducible engine revision manifest.
- [x] Add Android debug-APK CI workflow.
- [ ] Add PPSSPP Android JNI/native build and in-app launch adapter.
- [ ] Add ARMSX2 native build and in-app launch adapter.
- [ ] Add Winlator runtime/container integration.
- [ ] Add ARMSX3 native core build and in-app launch adapter.
- [ ] Add per-game compatibility tests, controller profiles, save handling, telemetry-free crash logs, and device thermal safeguards.

See [ENGINE_INTEGRATION.md](docs/ENGINE_INTEGRATION.md) before attempting native builds.

## Legal and safety

NEXUS does not include game images, console BIOS, PlayStation firmware, decryption keys, proprietary SDKs, or other copyrighted system files. Users must supply files they are legally entitled to use. PPSSPP does not require a PSP BIOS; ARMSX2 requires a legitimately dumped PS2 BIOS; ARMSX3 requires the official PS3 firmware supplied by the user.

Before distributing a build containing an engine, preserve its copyright notices and license, provide corresponding source where required, and audit all third-party dependencies. ARMSX2 (GPL-3.0) and ARMSX3 (GPL-2.0-only) have different licensing terms; keep engine code isolated and obtain a proper license review before combining or redistributing modified binaries.

This project is not affiliated with Sony, Microsoft, Nintendo, the PPSSPP team, ARMSX, PCSX2, RPCS3, or Winlator developers.
