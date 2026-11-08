package com.nexus.emulator.engine;

import com.nexus.emulator.catalog.GameFileClassifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EngineRegistry {
    private static final List<EngineDescriptor> ENGINES;

    static {
        List<EngineDescriptor> engines = new ArrayList<>();
        engines.add(new EngineDescriptor("ppsspp", GameFileClassifier.PSP, "PPSSPP",
                "https://github.com/hrydgard/ppsspp", "SOURCE_LINKED_RUNTIME_PENDING"));
        engines.add(new EngineDescriptor("armsx2", GameFileClassifier.PS2, "ARMSX2",
                "https://github.com/ARMSX2/ARMSX2", "SOURCE_LINKED_RUNTIME_PENDING"));
        engines.add(new EngineDescriptor("winlator", GameFileClassifier.PC, "Winlator",
                "https://github.com/brunodev85/winlator-app", "SOURCE_LINKED_RUNTIME_PENDING"));
        engines.add(new EngineDescriptor("armsx3", GameFileClassifier.PS3, "ARMSX3",
                "https://github.com/ARMSX2/ARMSX3", "SOURCE_LINKED_RUNTIME_PENDING"));
        ENGINES = Collections.unmodifiableList(engines);
    }

    private EngineRegistry() { }
    public static List<EngineDescriptor> all() { return ENGINES; }

    public static EngineDescriptor forPlatform(String platform) {
        for (EngineDescriptor engine : ENGINES) {
            if (engine.getPlatform().equals(platform)) return engine;
        }
        return null;
    }
}
