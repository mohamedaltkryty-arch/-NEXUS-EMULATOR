# NEXUS engine integration status

## PSP — external launch handoff available

NEXUS recognizes PSP ISO, CSO, PBP and ZSO files and hands the selected content URI to the official PPSSPP Android app (standard or Gold). NEXUS validates that the content URI is still readable before launching and shares temporary read access with PPSSPP.

This is an external-engine handoff, not an embedded PPSSPP native core. PPSSPP owns rendering, audio, input controls, save states and game compatibility.

## PS2, PS3 and Windows — not active features

The repository's historical references to ARMSX2, ARMSX3 and Winlator are source references only. They are not compiled into or bundled with the NEXUS APK. These platforms are hidden from the active UI and cannot be imported into the current PSP library.

## Legal and technical boundary

- Do not bundle commercial game images, console BIOS/firmware, proprietary keys or SDKs.
- The source submodules remain pinned as research references, not packaged binaries.
- Retain upstream notices and review full dependency trees before any future runtime redistribution.
- PPSSPP: GPL-2.0-or-later.
- ARMSX2: GPL-3.0.
- ARMSX3: GPL-2.0-only.
- Winlator app: LGPL-2.1.

## Compatibility

The NEXUS frontend targets Android 6.0+ and does not impose a native ABI filter. PPSSPP must be installed separately and must support the device's Android version, CPU architecture and graphics stack. No universal game/device compatibility claim is made.
