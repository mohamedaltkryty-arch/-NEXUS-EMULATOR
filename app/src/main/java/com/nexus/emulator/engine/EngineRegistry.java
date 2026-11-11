package com.nexus.emulator.engine;

import com.nexus.emulator.catalog.GameFileClassifier;

import java.util.Collections;
import java.util.List;

public final class EngineRegistry {
    private static final List<EngineDescriptor> ENGINES = Collections.singletonList(
            new EngineDescriptor(
                    "ppsspp",
                    GameFileClassifier.PSP,
                    "PPSSPP",
                    "https://www.ppsspp.org/",
                    "EXTERNAL_RUNTIME_SUPPORTED",
                    true
            )
    );

    private EngineRegistry() { }

    public static List<EngineDescriptor> all() {
        return ENGINES;
    }

    public static EngineDescriptor forPlatform(String platform) {
        for (EngineDescriptor engine : ENGINES) {
            if (engine.getPlatform().equals(platform)) return engine;
        }
        return null;
    }
}
