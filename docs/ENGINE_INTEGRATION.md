# Engine source and integration plan

## What this repository contains now

The four official upstream projects are connected as Git submodules and pinned to exact commits recorded in `engines.lock.json`. This preserves upstream history and avoids copying thousands of upstream files into NEXUS.

A submodule is a source reference, not a compiled engine. The current Android app remains a launcher/library shell. It cannot execute PSP, PS2, PS3, or Windows game files yet.

## Sources and integration notes

### PSP — PPSSPP
- Source: https://github.com/hrydgard/ppsspp
- License: GPL-2.0-or-later.
- PPSSPP is a C++ PSP emulator with Android support.
- PPSSPP does not require a separate PSP BIOS.
- Integration work: review upstream Android Gradle/CMake targets, build arm64-v8a native libraries, define an Android intent/JNI bridge, then test game load, audio, rendering, touch input, controller input, and save paths.

### PS2 — ARMSX2
- Source: https://github.com/ARMSX2/ARMSX2
- License: GPL-3.0.
- It is an ARM64-focused PCSX2 fork with Android support.
- A BIOS dump from a legitimately owned PS2 is required.
- Integration work: follow upstream build workflows and native dependency setup; do not assume that the app module can be copied into NEXUS without adapting its Gradle and native build configuration.

### Windows — Winlator
- App source: https://github.com/brunodev85/winlator-app
- License shown by its repository: LGPL-2.1.
- Winlator uses Wine and Box86/Box64 plus graphics/runtime components such as Mesa, DXVK and VKD3D.
- Integration work: import the full runtime/container lifecycle rather than merely launching an EXE. A Windows game generally requires its whole installed directory, runtime libraries, registry/container configuration, and per-game environment variables.

### PS3 — ARMSX3
- Source: https://github.com/ARMSX2/ARMSX3
- License: GPL-2.0-only (check individual source headers for exceptions).
- Upstream build requires Android SDK/NDK r27 or newer, CMake 3.30 or newer, and JDK 17. It supports arm64-v8a / armv8.2.
- Upstream README describes an unstripped core library of roughly 1.3 GB and additional librashader/libadrenotools checkouts.
- A legitimate PS3 firmware file is required and is not included.
- Treat PS3 as experimental on the POCO X4 GT. The Dimensity 8100 / Mali-G610 configuration is not a guarantee of useful performance. Test actual titles and thermal stability before exposing a Play button.

## Device target

- POCO X4 GT
- MediaTek Dimensity 8100
- Mali-G610 MC6
- arm64-v8a
- 12 GB RAM and 256 GB storage (as supplied by the device owner)

## Licensing boundary

Keep upstream engines separately identifiable. Do not remove upstream copyright notices, license files, or attribution. Before distributing an APK that embeds or links modified engine code, review the exact license of each component and its dependencies and satisfy source-offer / corresponding-source obligations where applicable. ARMSX2 is GPL-3.0 while ARMSX3 is GPL-2.0-only; do not combine their code into one derivative work without a qualified compatibility review. Merely storing them as separate Git submodules does not merge their code.

## Game files

NEXUS does not download or bundle commercial games, BIOS images, firmware, keys, or proprietary SDKs. The owner imports files they are legally entitled to use. The launcher uses Android's Storage Access Framework so it does not need broad storage permissions.

## Next integration milestone

Integrate PPSSPP first:
1. Review the pinned upstream Android build target and license notices.
2. Produce and test arm64-v8a native libraries.
3. Add a dedicated PSP engine adapter in NEXUS.
4. Persist SAF URI permissions and pass readable file descriptors/paths safely.
5. Run smoke tests for boot, audio, renderer, touch/controller input, save/load, pause/resume, and process recreation.
6. Only mark PSP as available after all tests pass on the POCO X4 GT.

Do not mark an engine as integrated based only on the presence of its source directory.
