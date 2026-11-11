package com.nexus.emulator.util;

import java.util.HashMap;
import java.util.Map;

public final class AppStrings {
    private static final Map<String, String[]> VALUES = new HashMap<>();

    static {
        put("tagline", "مكتبتك. ألعابك. مكان واحد.", "YOUR GAMES. ONE PLACE.");
        put("hero_kicker", "NEXUS  /  مكتبة PSP", "NEXUS  /  PSP LIBRARY");
        put("hero_title", "كل ألعابك بمكان واحد", "Your games. Ready to play.");
        put("hero_body", "نظّم ألعاب PSP وشغّلها من مكتبة واحدة مرتبة.", "Organize your PSP collection and launch games from one clean library.");
        put("import", "استيراد ألعاب PSP", "Import PSP games");
        put("runtime_title", "محرك تشغيل PSP", "PSP RUNTIME");
        put("runtime_ready", "PPSSPP جاهز", "PPSSPP is ready");
        put("runtime_missing", "PPSSPP غير مثبت", "PPSSPP is not installed");
        put("runtime_ready_body", "التشغيل عبر تطبيق PPSSPP الرسمي على جهازك.", "Games launch through the official PPSSPP app on your device.");
        put("runtime_missing_body", "ثبّت PPSSPP حتى تتمكن من تشغيل ألعابك.", "Install PPSSPP to launch your games.");
        put("download", "تنزيل PPSSPP", "Get PPSSPP");
        put("library", "مكتبتك", "Your library");
        put("on_device", "محفوظة على جهازك", "STORED ON THIS DEVICE");
        put("total_games", "الألعاب", "GAMES");
        put("favorite_games", "المفضلة", "FAVORITES");
        put("search", "ابحث عن لعبة...", "Search your games...");
        put("all", "الكل", "All games");
        put("favorites_filter", "المفضلة", "Favorites");
        put("empty_title", "ابدأ مكتبتك", "Start your library");
        put("empty_body", "استورد ألعاب PSP بصيغ ISO أو CSO أو PBP أو ZSO لتظهر هنا.", "Import PSP games in ISO, CSO, PBP or ZSO format to see them here.");
        put("no_results_title", "ما لقينا ألعاب مطابقة", "No matching games");
        put("no_results_body", "جرّب كلمة بحث ثانية أو ارجع إلى كل الألعاب.", "Try another search or switch back to all games.");
        put("show_all", "عرض كل الألعاب", "Show all games");
        put("clear_search", "مسح البحث", "Clear search");
        put("settings", "الإعدادات", "Settings");
        put("language", "اللغة", "Language");
        put("theme", "المظهر", "Appearance");
        put("dark", "داكن", "Dark");
        put("light", "فاتح", "Light");
        put("arabic", "العربية", "Arabic");
        put("english", "English", "English");
        put("about", "حول NEXUS", "About NEXUS");
        put("about_body", "NEXUS مكتبة محلية لألعاب PSP. يستخدم التطبيق PPSSPP الرسمي لتشغيل الألعاب. لا يتضمن ألعاباً أو BIOS أو Firmware، ولا يرفع ملفاتك إلى الإنترنت.", "NEXUS is a local PSP game library. It uses the official PPSSPP app to run games. It does not include games, BIOS or firmware, and it does not upload your files.");
        put("details", "تفاصيل اللعبة", "Game details");
        put("platform", "المنصة", "Platform");
        put("file_type", "الصيغة", "Format");
        put("size", "الحجم", "Size");
        put("play", "تشغيل", "Play");
        put("favorite", "إضافة للمفضلة", "Add to favorites");
        put("unfavorite", "إزالة من المفضلة", "Remove from favorites");
        put("remove", "إزالة من المكتبة", "Remove from library");
        put("confirm_remove", "هل تريد إزالة هذه اللعبة من مكتبتك؟ لن يتم حذف ملف اللعبة من جهازك.", "Remove this game from your library? The game file will remain on your device.");
        put("cancel", "إلغاء", "Cancel");
        put("ok", "حسناً", "OK");
        put("unsupported", "هذه الصيغة غير مدعومة. الصيغ المدعومة: ISO وCSO وPBP وZSO.", "Unsupported file format. Supported formats: ISO, CSO, PBP and ZSO.");
        put("duplicate", "هذه اللعبة موجودة في مكتبتك بالفعل.", "This game is already in your library.");
        put("imported", "تمت إضافة اللعبة إلى مكتبتك.", "Game added to your library.");
        put("imported_many", "تمت إضافة", "Added");
        put("games_suffix", "ألعاب", "games");
        put("permission_error", "تعذّر حفظ صلاحية الملف. أعد استيراده من مدير الملفات.", "Could not retain file access. Please import it again from your file picker.");
        put("launch_error", "تعذّر فتح اللعبة. تأكد أن الملف ما زال متاحاً ثم أعد استيراده.", "Could not open the game. Confirm the file is still available, then import it again.");
        put("psp_engine_missing_title", "PPSSPP غير مثبت", "PPSSPP is not installed");
        put("psp_engine_missing_body", "ثبّت تطبيق PPSSPP الرسمي أولاً، ثم ارجع إلى NEXUS واضغط تشغيل.", "Install the official PPSSPP app first, then return to NEXUS and tap Play.");
        put("import_error", "تعذرت إضافة الملف إلى المكتبة.", "The file could not be added to your library.");
        put("footer", "مصمّم لألعاب PSP  •  بياناتك تبقى على جهازك", "MADE FOR PSP  •  YOUR LIBRARY STAYS ON DEVICE");
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
