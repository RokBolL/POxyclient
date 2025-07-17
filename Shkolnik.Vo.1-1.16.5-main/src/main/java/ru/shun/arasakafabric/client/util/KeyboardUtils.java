package ru.shun.arasakafabric.client.util;
import java.awt.im.InputContext;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
public class KeyboardUtils {
    private static final Map<Integer, String> ENGLISH_KEY_NAMES = new HashMap<>();
    private static final Map<Integer, String> RUSSIAN_KEY_NAMES = new HashMap<>();
    private static boolean isRussianLayout = false;
    static {
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_A, "A");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_B, "B");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_C, "C");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_D, "D");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_E, "E");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_F, "F");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_G, "G");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_H, "H");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_I, "I");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_J, "J");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_K, "K");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_L, "L");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_M, "M");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_N, "N");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_O, "O");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_P, "P");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_Q, "Q");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_R, "R");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_S, "S");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_T, "T");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_U, "U");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_V, "V");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_W, "W");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_X, "X");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_Y, "Y");
        ENGLISH_KEY_NAMES.put(GLFW.GLFW_KEY_Z, "Z");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_A, "Ф");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_B, "И");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_C, "С");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_D, "В");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_E, "У");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_F, "А");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_G, "П");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_H, "Р");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_I, "Ш");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_J, "О");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_K, "Л");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_L, "Д");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_M, "Ь");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_N, "Т");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_O, "Щ");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_P, "З");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_Q, "Й");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_R, "К");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_S, "Ы");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_T, "Е");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_U, "Г");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_V, "М");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_W, "Ц");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_X, "Ч");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_Y, "Н");
        RUSSIAN_KEY_NAMES.put(GLFW.GLFW_KEY_Z, "Я");
    }
    public static void updateKeyboardLayout() {
        try {
            String currentLocale = InputContext.getInstance().getLocale().toString().toLowerCase();
            isRussianLayout = currentLocale.contains("ru");
        } catch (Exception e) {
            isRussianLayout = false;
        }
    }
    public static String getKeyName(int keyCode) {
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            if (isRussianLayout && RUSSIAN_KEY_NAMES.containsKey(keyCode)) {
                return RUSSIAN_KEY_NAMES.get(keyCode);
            } else if (ENGLISH_KEY_NAMES.containsKey(keyCode)) {
                return ENGLISH_KEY_NAMES.get(keyCode);
            }
        }
        String keyName = GLFW.glfwGetKeyName(keyCode, 0);
        if (keyName != null && !keyName.isEmpty()) {
            return keyName.toUpperCase();
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE: return "Пробел";
            case GLFW.GLFW_KEY_ESCAPE: return "Esc";
            case GLFW.GLFW_KEY_ENTER: return "Enter";
            case GLFW.GLFW_KEY_TAB: return "Tab";
            case GLFW.GLFW_KEY_BACKSPACE: return "Backspace";
            case GLFW.GLFW_KEY_INSERT: return "Insert";
            case GLFW.GLFW_KEY_DELETE: return "Delete";
            case GLFW.GLFW_KEY_RIGHT: return "→";
            case GLFW.GLFW_KEY_LEFT: return "←";
            case GLFW.GLFW_KEY_DOWN: return "↓";
            case GLFW.GLFW_KEY_UP: return "↑";
            case GLFW.GLFW_KEY_PAGE_UP: return "PgUp";
            case GLFW.GLFW_KEY_PAGE_DOWN: return "PgDn";
            case GLFW.GLFW_KEY_HOME: return "Home";
            case GLFW.GLFW_KEY_END: return "End";
            case GLFW.GLFW_KEY_CAPS_LOCK: return "CapsLock";
            case GLFW.GLFW_KEY_SCROLL_LOCK: return "ScrollLock";
            case GLFW.GLFW_KEY_NUM_LOCK: return "NumLock";
            case GLFW.GLFW_KEY_PRINT_SCREEN: return "PrintScreen";
            case GLFW.GLFW_KEY_PAUSE: return "Pause";
            case GLFW.GLFW_KEY_F1: return "F1";
            case GLFW.GLFW_KEY_F2: return "F2";
            case GLFW.GLFW_KEY_F3: return "F3";
            case GLFW.GLFW_KEY_F4: return "F4";
            case GLFW.GLFW_KEY_F5: return "F5";
            case GLFW.GLFW_KEY_F6: return "F6";
            case GLFW.GLFW_KEY_F7: return "F7";
            case GLFW.GLFW_KEY_F8: return "F8";
            case GLFW.GLFW_KEY_F9: return "F9";
            case GLFW.GLFW_KEY_F10: return "F10";
            case GLFW.GLFW_KEY_F11: return "F11";
            case GLFW.GLFW_KEY_F12: return "F12";
            case GLFW.GLFW_KEY_F13: return "F13";
            case GLFW.GLFW_KEY_F14: return "F14";
            case GLFW.GLFW_KEY_F15: return "F15";
            case GLFW.GLFW_KEY_F16: return "F16";
            case GLFW.GLFW_KEY_F17: return "F17";
            case GLFW.GLFW_KEY_F18: return "F18";
            case GLFW.GLFW_KEY_F19: return "F19";
            case GLFW.GLFW_KEY_F20: return "F20";
            case GLFW.GLFW_KEY_F21: return "F21";
            case GLFW.GLFW_KEY_F22: return "F22";
            case GLFW.GLFW_KEY_F23: return "F23";
            case GLFW.GLFW_KEY_F24: return "F24";
            case GLFW.GLFW_KEY_F25: return "F25";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LShift";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCtrl";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LAlt";
            case GLFW.GLFW_KEY_LEFT_SUPER: return "LWin";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RShift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCtrl";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RAlt";
            case GLFW.GLFW_KEY_RIGHT_SUPER: return "RWin";
            case GLFW.GLFW_KEY_MENU: return "Menu";
            default: return "Клавиша " + keyCode;
        }
    }
} 
