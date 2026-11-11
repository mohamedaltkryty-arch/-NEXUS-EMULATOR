# Engine integration status

## PSP — external runtime supported

NEXUS implements an Android Activity handoff to the official PPSSPP Android app. It detects standard PPSSPP and PPSSPP Gold, sends an explicit VIEW intent to the installed package's PpssppActivity, and grants read access to the selected SAF content URI.

Supported files: ISO, CSO, PBP and ZSO.

This is not an embedded PPSSPP core. PPSSPP remains separately installed and owns rendering, audio, touch/controller input, settings, save states and title compatibility. NEXUS owns local indexing, search, favorites and launch handoff. Actual gameplay was not exercised in CI because a compatible PPSSPP package and legally distributable game file are not part of the test environment.

## PS2 — source reference only

Source: https://github.com/ARMSX2/ARMSX2  
License: GPL-3.0.

The ARMSX2 submodule remains pinned as a development reference. It is not compiled, loaded, displayed as playable or bundled in NEXUS. Integration requires a separate native build and ABI/device validation, legal BIOS handling, storage integration and real-game thermal/performance tests.

## PS3 — source reference only

Source: https://github.com/ARMSX2/ARMSX3  
License: GPL-2.0-only (verify individual source headers).

ARMSX3 remains a pinned source reference only. It is not compiled or bundled. Firmware and key boundaries, ARM64 requirements, graphics-driver support and title-level stability must be evaluated before any user-facing claim.

## Windows — source reference only

Source: https://github.com/brunodev85/winlator-app  
License: LGPL-2.1 (verify bundled components).

Winlator remains a pinned source reference only. NEXUS does not catalogue Windows folders or claim to run Windows games in this release.

## Device compatibility policy

The Java frontend has no ABI restriction and targets Android 6.0+. PPSSPP runtime support depends on the installed PPSSPP build, Android version, CPU architecture, GPU driver/API, RAM, thermals and the specific game. A source submodule is never considered an integrated runtime.

## Game files and privacy

NEXUS uses Android's Storage Access Framework. The PSP adapter shares only the user-selected content URI with temporary read access. Game images are not uploaded or duplicated. Do not bundle commercial games, console BIOS/firmware, PS3 keys or proprietary SDKs.

## Licensing boundary

Keep upstream projects separately identifiable. Do not remove license files, copyright notices or attribution. Review corresponding-source and redistribution requirements before distributing any APK that embeds or links upstream engine code. ARMSX2 (GPL-3.0) and ARMSX3 (GPL-2.0-only) must remain separate unless a qualified license review confirms compatibility.
