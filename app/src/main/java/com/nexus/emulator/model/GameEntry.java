package com.nexus.emulator.model;

public final class GameEntry {
    private final long id;
    private final String name;
    private final String platform;
    private final String uri;
    private final String extension;
    private final long sizeBytes;
    private final long addedAt;
    private final boolean favorite;

    public GameEntry(long id, String name, String platform, String uri, String extension,
                     long sizeBytes, long addedAt, boolean favorite) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.uri = uri;
        this.extension = extension;
        this.sizeBytes = sizeBytes;
        this.addedAt = addedAt;
        this.favorite = favorite;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getPlatform() { return platform; }
    public String getUri() { return uri; }
    public String getExtension() { return extension; }
    public long getSizeBytes() { return sizeBytes; }
    public long getAddedAt() { return addedAt; }
    public boolean isFavorite() { return favorite; }
}
