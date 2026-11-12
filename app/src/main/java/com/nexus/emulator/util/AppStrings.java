package com.nexus.emulator.util;

import java.util.HashMap;
import java.util.Map;

public final class AppStrings {
    private static final Map<String, String[]> VALUES = new HashMap<>();

    static {
        put("tagline", "مكتبة ألعابك، بدون تعقيد", "YOUR PSP COLLECTION, SIMPLIFIED");
        put("hero_title", "أضف أول لعبة إلى مكتبتك", "Add your first game to the library");
        put("hero_body", "استورد ألعاب PSP التي تملكها لتظهر هنا. الصيغ المدعومة: ISO وCSO وPBP وZSO.", "Import PSP games you own. Supported formats: ISO, CSO, PBP and ZSO.");
        put("import", "استيراد ألعاب PSP", "IMPORT PSP GAMES");
        put("import_file", "استيراد ملف لعبة", "Import a game file");
        put("import_folder", "استيراد مجلد لعبة PC أو PS3", "Import a PC or PS3 game folder");
        put("library", "مكتبتك", "YOUR LIBRARY");
        put("search", "ابحث عن لعبة...", "Search your games...");
        put("all", "الكل", "ALL");
        put("pc", "PC", "PC");
        put("psp", "PSP", "PSP");
        put("ps2", "PS2", "PS2");
        put("ps3", "PS3", "PS3");
        put("empty_title", "أضف أول لعبة إلى مكتبتك", "Add your first game to the library");
        put("empty_body", "استورد ألعاب PSP التي تملكها لتظهر هنا. الصيغ المدعومة: ISO وCSO وPBP وZSO.", "Import PSP games you own. Supported formats: ISO, CSO, PBP and ZSO.");
        put("engines", "محركات المحاكاة", "EMULATOR ENGINES");
        put("settings", "الإعدادات", "SETTINGS");
        put("language", "اللغة", "Language");
        put("theme", "المظهر", "Appearance");
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
        put("about_body", "ينظّم NEXUS مكتبة ألعاب PSP على جهازك. يتم تشغيل الألعاب عبر تطبيق PPSSPP الرسمي. لا يتضمن التطبيق ألعاباً أو ملفات BIOS أو Firmware.", "NEXUS organizes your PSP collection locally. Playback uses the official PPSSPP app. No games, BIOS or firmware are included.");
        put("pc_folder_tip", "استورد مجلد اللعبة كاملاً، لا ملف EXE وحده.", "Import the complete game folder, not only the EXE file.");
        put("engines_count", "أربعة مصادر محركات مرتبطة", "Four engine sources linked");
        put("play", "تشغيل", "Play");
        put("psp_ready_body", "يمكن تشغيل ألعاب PSP باستخدام تطبيق PPSSPP الرسمي المثبّت على جهازك.", "PSP games can be played with the official PPSSPP app installed on your device.");
        put("psp_engine_missing_title", "تطبيق PPSSPP غير مثبت", "PPSSPP is not installed");
        put("psp_engine_missing_body", "ثبّت تطبيق PPSSPP الرسمي أولاً، ثم ارجع إلى NEXUS واضغط تشغيل.", "Install the official PPSSPP app, then return to NEXUS and tap Play.");
        put("install_engine", "تنزيل PPSSPP", "Get PPSSPP");
        put("launch_error", "تعذر فتح اللعبة. تحقق من الملف وصلاحية الوصول.", "Could not open the game. Check the file and access permission.");
        put("coming_soon", "تشغيل هذه المنصة غير متاح في هذا الإصدار بعد.", "This platform is not playable in this version yet.");
        put("favorites", "المفضلة", "Favorites");
        put("games_in_library", "عدد الألعاب", "GAMES IN YOUR LIBRARY");
        put("local_library", "محفوظة على جهازك", "SAVED ON DEVICE");
        put("no_matches_title", "لا توجد نتائج مطابقة", "No matching games");
        put("no_matches_body", "جرّب البحث باسم آخر أو اعرض كل الألعاب.", "Try another search or switch back to all games.");
        put("clear_search", "عرض كل الألعاب", "Show all games");
        put("unknown_game", "لعبة PSP", "PSP Game");
        put("file_picker_error", "تعذر فتح مدير الملفات على هذا الجهاز.", "Could not open the file picker on this device.");
        put("no_files_selected", "لم يتم اختيار أي ملف.", "No files were selected.");
        put("import_error", "تعذر إضافة الملف. تحقق من مساحة الجهاز وصلاحية الوصول.", "Could not add this file. Check device storage and file access.");
        put("import_summary", "اكتمل الاستيراد: أُضيفت %1$d، مكررة %2$d، مرفوضة %3$d.", "Import complete: %1$d added, %2$d duplicates, %3$d skipped.");
        put("importing", "جارٍ استيراد الألعاب...", "Importing games...");
        put("supported_formats", "ISO  •  CSO  •  PBP  •  ZSO", "ISO  •  CSO  •  PBP  •  ZSO");
    }

    private AppStrings() { }
    private static void put(String key, String arabic, String english) {
        VALUES.put(key, new String[]{arabic, english});
    }
    public static String get(String key, boolean arabic) {
        String[] values = VALUES.get(key);
        return values == null ? key : values[arabic ? 0 : 1];
    }
}