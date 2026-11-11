package com.nexus.emulator.engine;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;

import com.nexus.emulator.catalog.GameFileClassifier;

import java.util.Locale;

public final class PspEngine {
    private static final String FREE_PACKAGE = "org.ppsspp.ppsspp";
    private static final String GOLD_PACKAGE = "org.ppsspp.ppssppgold";
    private static final String FREE_ACTIVITY = "org.ppsspp.ppsspp.PpssppActivity";
    private static final String GOLD_ACTIVITY = "org.ppsspp.ppsspp.PpssppActivity";
    private static final String MIME_TYPE = "application/octet-stream";

    private PspEngine() { }

    public static boolean supportsGame(String platform, String extension) {
        return GameFileClassifier.PSP.equals(platform)
                && GameFileClassifier.isSupportedPspExtension(extension);
    }

    public static boolean isInstalled(Context context) {
        if (context == null) return false;
        return isPackageInstalled(context, FREE_PACKAGE) || isPackageInstalled(context, GOLD_PACKAGE);
    }

    private static boolean isPackageInstalled(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo ignored;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ignored = manager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } else {
                ignored = manager.getPackageInfo(packageName, 0);
            }
            return ignored != null;
        } catch (PackageManager.NameNotFoundException | RuntimeException unavailable) {
            return false;
        }
    }

    public static void launch(Activity activity, Uri gameUri, String displayName)
            throws EngineNotInstalledException {
        if (activity == null || gameUri == null || !"content".equals(gameUri.getScheme())) {
            throw new IllegalArgumentException("A readable content URI is required.");
        }

        boolean installed = false;
        String[][] candidates = {
                {FREE_PACKAGE, FREE_ACTIVITY},
                {GOLD_PACKAGE, GOLD_ACTIVITY}
        };

        for (String[] candidate : candidates) {
            String packageName = candidate[0];
            if (!isPackageInstalled(activity, packageName)) continue;
            installed = true;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setComponent(new ComponentName(packageName, candidate[1]));
            intent.setDataAndType(gameUri, MIME_TYPE);
            intent.putExtra(Intent.EXTRA_STREAM, gameUri);
            intent.setClipData(ClipData.newUri(
                    activity.getContentResolver(),
                    displayName == null ? "PSP game" : displayName,
                    gameUri
            ));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                activity.startActivity(intent);
                return;
            } catch (ActivityNotFoundException unavailable) {
                // Try the other official PPSSPP package variant.
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
