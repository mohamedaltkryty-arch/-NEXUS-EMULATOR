package com.nexus.emulator.engine;

public final class EngineDescriptor {
    private final String id;
    private final String platform;
    private final String name;
    private final String sourceUrl;
    private final String integrationStatus;

    public EngineDescriptor(String id, String platform, String name, String sourceUrl, String integrationStatus) {
        this.id = id;
        this.platform = platform;
        this.name = name;
        this.sourceUrl = sourceUrl;
        this.integrationStatus = integrationStatus;
    }

    public String getId() { return id; }
    public String getPlatform() { return platform; }
    public String getName() { return name; }
    public String getSourceUrl() { return sourceUrl; }
    public String getIntegrationStatus() { return integrationStatus; }
    public boolean isLaunchReady() { return false; }
}
