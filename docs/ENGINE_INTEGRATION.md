# Engine integration status

## PSP — playable through official PPSSPP

NEXUS includes a PSP launch adapter. It sends the selected SAF content URI to the official PPSSPP Android activity with a temporary read grant. Both standard and Gold package IDs are detected. If neither is installed, NEXUS displays an install prompt linking to the official PPSSPP download page.

This is an external-engine handoff, not an embedded PPSSPP native core. PPSSPP remains separately installed and owns rendering, audio, touch controls, settings and save states. NEXUS owns the game catalogue and import flow.

Supported formats routed to PPSSPP: ISO, CSO, PBP and ZSO. Title compatibility remains determined by PPSSPP.

## PS2 — not yet playable in NEXUS

Source: https://github.com/ARMSX2/ARMSX2  
License: GPL-3.0.

ARMSX2 remains a pinned source submodule. It is not compiled or bundled into NEXUS. Future integration must follow its Android build, inspect all native dependencies, use a legally dumped PS2 BIOS and test actual games and thermal stability.

## PS3 — experimental source only

Source: https://github.com/ARMSX2/ARMSX3  
License: GPL-2.0-only (verify individual source headers).

ARMSX3 remains a pinned source submodule. It is not compiled or bundled into NEXUS. Its large ARM64 native core and firmware requirements mean broad-device support cannot be promised. Future releases must report tested GPU/driver profiles and title-specific results.

## Windows — runtime not yet integrated

Source: https://github.com/brunodev85/winlator-app  
License: LGPL-2.1 (verify all bundled runtime components).

Windows games require their complete installed directories and a Wine/Box64/graphics runtime. NEXUS currently catalogues Windows folders only.

## Device compatibility policy

The NEXUS Java frontend targets Android 6.0+ and declares no ABI filter. The PSP runtime must match the device's supported Android version and CPU architecture. Actual compatibility depends on CPU instruction support, GPU driver/API, RAM, thermal limits and the specific game. A source submodule alone never marks a platform ready.

## Game files and privacy

NEXUS uses Android's Storage Access Framework. The PSP adapter shares only the user-selected content URI through a temporary read grant. It does not upload or duplicate game images. Do not bundle commercial games, console BIOS/firmware, PS3 keys or proprietary SDKs.

## Licensing boundary

Keep upstream projects separately identifiable. Do not remove license files, copyright notices or attribution. Review corresponding-source and redistribution requirements before distributing any APK that embeds or links upstream engine code. ARMSX2 (GPL-3.0) and ARMSX3 (GPL-2.0-only) must remain separate unless a qualified license review confirms compatibility.
