package com.nexus.emulator.security;

import android.content.Intent;

public final class UriGrantPolicy {
    private UriGrantPolicy() { }

    public static boolean canPersistReadPermission(int resultFlags, String uriScheme) {
        if (uriScheme == null || !"content".equalsIgnoreCase(uriScheme)) return false;

        int requiredFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
        return (resultFlags & requiredFlags) == requiredFlags;
    }
}