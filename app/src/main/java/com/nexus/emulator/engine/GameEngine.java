package com.nexus.emulator.engine;

import android.content.Context;
import android.net.Uri;

public interface GameEngine {
    String getEngineId();
    boolean canLaunch(Context context, Uri gameUri);
    void launch(Context context, Uri gameUri) throws EngineLaunchException;
}
