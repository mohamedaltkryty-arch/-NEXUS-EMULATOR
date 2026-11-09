package com.nexus.emulator.util;

import java.util.HashMap;
import java.util.Map;

public final class AppStrings {
    private static final Map<String, String[]> VALUES = new HashMap<>();

    static {
        put("tagline", "مكتبتك. ألعابك. مكان واحد.", "YOUR GAMES. ONE PLACE.");
        put("hero_title", "عِش تجربة اللعب بطريقتك", "Your games, organized.");
        put("hero_body", "اجمع ألعابك وأدرها من مكتبة واحدة.", "Bring your collection into one library.");
        put("import", "استيراد ألعاب", "IMPORT GAMES");
        put("import_file", "استيراد ملف لعبة", "Import a game file");
        put("import_folder", "استيراد مجلد لعبة PC أو PS3", "Import a PC or PS3 game folder");
        put("library", "مكتبة الألعاب", "GAME LIBRARY");
        put("search", "ابحث عن لعبة...", "Search games...");
        put("all", "الكل", "ALL");
        put("pc", "PC", "PC");
        put("psp", "PSP", "PSP");
        put("ps2", "PS2", "PS2");
        put("ps3", "PS3", "PS3");
        put("empty_title", "مكتبتك فارغة حالياً", "Your library is empty");
        put("empty_body", "استورد ملفات ألعابك لتظهر هنا.", "Import your game files to see them here.");
        put("engines", "محركات المحاكاة", "EMULATOR ENGINES");
        put("settings", "الإعدادات", "SETTINGS");
        put("language", "اللغة", "Language");
        put("theme", "المظهر", "Theme");
        put("dark", "داكن", "Dark");
        put("light", "فاتح", "Light");
        put("arabic", "العربية", "Arabic");
        put("english", "English", "English");
        put("engine_pending", "المصدر مرتبط، لكن محرك التشغيل لم يُدمج في APK بعد.", "Source is linked, but the runtime is not embedded in this APK yet.");
        put("not_available", "التشغيل غير متاح في هذا الإصدار", "Launching is not available in this build");
        put("source", "فتح المصدر", "OPEN SOURCE");
        put("favorite", "إضافة إلى المفضلة", "Add to favorites");
        put("unfavorite", "إزالة من المفضلة", "Remove favorite");
        put("remove", "إزالة من المكتبة", "Remove from library");
        put("cancel", "إلغاء", "Cancel");
        put("ok", "حسناً", "OK");
        put("choose_platform", "اختر منصة اللعبة", "Choose the game platform");
        put("choose_folder_type", "نوع مجلد اللعبة", "Game folder type");
        put("pc_folder", "مجلد لعبة Windows مثبتة", "Installed Windows game folder");
        put("ps3_folder", "مجلد لعبة PS3", "PS3 game folder");
        put("unsupported", "صيغة الملف غير مدعومة للاستيراد.", "This file type is not supported for import.");
        put("duplicate", "هذه اللعبة موجودة في مكتبتك بالفعل.", "This game is already in your library.");
        put("imported", "تمت إضافة اللعبة إلى المكتبة.", "Game added to your library.");
        put("permission_error", "تعذر حفظ صلاحية الوصول إلى الملف. أعد استيراده.", "Could not persist file access. Please import it again.");
        put("details", "تفاصيل اللعبة", "GAME DETAILS");
        put("platform", "المنصة", "Platform");
        put("file_type", "نوع الملف", "File type");
        put("size", "الحجم", "Size");
        put("engine_status", "حالة المحرك", "Engine status");
        put("source_only", "المصدر متاح — الدمج قيد التطوير", "Source available — integration in progress");
        put("about", "حول NEXUS", "About NEXUS");
        put("about_body", "NEXUS يجمع ألعابك في مكتبة واحدة. تشغيل ألعاب PSP يتم عبر PPSSPP الرسمي، ولا يتضمن التطبيق ألعاباً أو BIOS أو Firmware.", "NEXUS brings your games together in one library. PSP playback uses the official PPSSPP app. Games, BIOS and firmware are not included.");
        put("pc_folder_tip", "استورد مجلد اللعبة كاملاً، لا ملف EXE وحده.", "Import the complete game folder, not only the EXE file.");
        put("engines_count", "أربعة مصادر محركات مرتبطة", "Four engine sources linked");
        put("play", "تشغيل", "Play");
        put("psp_ready_body", "يمكن تشغيل ألعاب PSP باستخدام تطبيق PPSSPP الرسمي المثبّت على جهازك.", "PSP games can be played with the official PPSSPP app installed on your device.");
        put("psp_engine_missing_title", "محرك PSP غير مثبت", "PSP engine not installed");
        put("psp_engine_missing_body", "ثبّت تطبيق PPSSPP الرسمي أولاً، ثم ارجع إلى NEXUS واضغط تشغيل.", "Install the official PPSSPP app, then return to NEXUS and tap Play.");
        put("install_engine", "تنزيل PPSSPP", "Get PPSSPP");
        put("launch_error", "تعذر فتح اللعبة. تحقق من الملف وصلاحية الوصول.", "Could not open the game. Check the file and access permission.");
        put("coming_soon", "تشغيل هذه المنصة غير متاح في هذا الإصدار بعد.", "This platform is not playable in this version yet.");
    }

    private AppStrings() { }
    private static void put(String key, String arabic, String english) { VALUES.put(key, new String[]{arabic, english}); }
    public static String get(String key, boolean arabic) {
        String[] values = VALUES.get(key);
        return values == null ? key : values[arabic ? 0 : 1];
    }
}
