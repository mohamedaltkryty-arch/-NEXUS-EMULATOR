package com.nexus.emulator.security;

import android.content.Intent;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class UriGrantPolicyTest {
    @Test
    public void acceptsOnlyContentUrisWithBothReadAndPersistableGrants() {
        int validFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

        assertTrue(UriGrantPolicy.canPersistReadPermission(validFlags, "content"));
        assertFalse(UriGrantPolicy.canPersistReadPermission(Intent.FLAG_GRANT_READ_URI_PERMISSION, "content"));
        assertFalse(UriGrantPolicy.canPersistReadPermission(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION, "content"));
        assertFalse(UriGrantPolicy.canPersistReadPermission(validFlags, "file"));
        assertFalse(UriGrantPolicy.canPersistReadPermission(validFlags, null));
    }
}