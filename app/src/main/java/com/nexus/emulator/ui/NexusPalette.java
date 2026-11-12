package com.nexus.emulator.ui;

import android.graphics.Color;

public final class NexusPalette {
    public final int background;
    public final int surface;
    public final int elevatedSurface;
    public final int border;
    public final int primaryText;
    public final int secondaryText;
    public final int accent;
    public final int accentSoft;
    public final int positive;
    public final int field;

    private NexusPalette(boolean dark) {
        if (dark) {
            background = Color.rgb(11, 16, 32);
            surface = Color.rgb(19, 28, 46);
            elevatedSurface = Color.rgb(25, 36, 58);
            border = Color.rgb(41, 55, 82);
            primaryText = Color.rgb(244, 247, 255);
            secondaryText = Color.rgb(166, 179, 200);
            accent = Color.rgb(139, 124, 255);
            accentSoft = Color.rgb(41, 37, 78);
            positive = Color.rgb(66, 214, 176);
            field = Color.rgb(22, 32, 51);
        } else {
            background = Color.rgb(244, 246, 251);
            surface = Color.WHITE;
            elevatedSurface = Color.WHITE;
            border = Color.rgb(225, 231, 240);
            primaryText = Color.rgb(23, 32, 51);
            secondaryText = Color.rgb(102, 116, 140);
            accent = Color.rgb(93, 75, 224);
            accentSoft = Color.rgb(239, 237, 255);
            positive = Color.rgb(22, 143, 120);
            field = Color.rgb(236, 240, 247);
        }
    }

    public static NexusPalette from(boolean dark) {
        return new NexusPalette(dark);
    }
}