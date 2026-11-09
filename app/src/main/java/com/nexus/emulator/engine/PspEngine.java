package com.nexus.emulator.engine;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.Locale;

public final class PspEngine {
    private static final String[] PACKAGES = {
            "org.ppsspp.ppsspp",
            "org.ppsspp.ppssppgold"
    };
    private static final String ACTIVITY = "org.ppsspp.ppsspp.PpssppActivity";

    private PspEngine() { }

    public static boolean supportsGame(String platform, String extension) {
        if (platform == null || extension == null || !"PSP".equals(platform)) return false;
        String ext = extension.toLowerCase(Locale.ROOT);
        return "iso".equals(ext) || "cso".equals(ext) || "pbp".equals(ext) || "zso".equals(ext);
    }

    public static void launch(Activity activity, Uri gameUri, String displayName)
            throws EngineNotInstalledException {
        if (activity == null || gameUri == null || !"content".equals(gameUri.getScheme())) {
            throw new IllegalArgumentException("A readable content URI is required.");
        }

        PackageManager manager = activity.getPackageManager();
        boolean installed = false;
        for (String packageName : PACKAGES) {
            try {
                manager.getPackageInfo(packageName, 0);
                installed = true;
            } catch (PackageManager.NameNotFoundException missing) {
                continue;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setComponent(new ComponentName(packageName, ACTIVITY));
            intent.setDataAndType(gameUri, "application/octet-stream");
            intent.putExtra(Intent.EXTRA_STREAM, gameUri);
            intent.setClipData(ClipData.newUri(activity.getContentResolver(),
                    displayName == null ? "PSP game" : displayName, gameUri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                activity.startActivity(intent);
                return;
            } catch (ActivityNotFoundException unavailable) {
                // Try the other official package variant.
            }
        }
        if (!installed) throw new EngineNotInstalledException();
        throw new ActivityNotFoundException("The installed PPSSPP app cannot open this game URI.");
    }

    public static final class EngineNotInstalledException extends Exception {
        public EngineNotInstalledException() {
            super("Install the official PPSSPP Android app to play PSP games.");
        }
    }
}
