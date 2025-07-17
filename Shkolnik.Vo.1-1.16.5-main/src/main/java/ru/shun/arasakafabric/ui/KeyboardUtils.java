package ru.shun.arasakafabric.ui;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import java.awt.*;
import java.awt.im.InputContext;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class KeyboardUtils {
    private static final Map<Integer, String> EN_KEY_NAMES = new HashMap<>();
    private static final Map<Integer, String> RU_KEY_NAMES = new HashMap<>();
    private static boolean isRussianLayout = false;
    static {
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_A, "A");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_B, "B");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_C, "C");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_D, "D");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_E, "E");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_F, "F");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_G, "G");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_H, "H");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_I, "I");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_J, "J");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_K, "K");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_L, "L");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_M, "M");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_N, "N");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_O, "O");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_P, "P");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_Q, "Q");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_R, "R");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_S, "S");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_T, "T");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_U, "U");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_V, "V");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_W, "W");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_X, "X");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_Y, "Y");
        EN_KEY_NAMES.put(GLFW.GLFW_KEY_Z, "Z");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_A, "Ф");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_B, "И");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_C, "С");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_D, "В");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_E, "У");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_F, "А");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_G, "П");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_H, "Р");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_I, "Ш");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_J, "О");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_K, "Л");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_L, "Д");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_M, "Ь");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_N, "Т");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_O, "Щ");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_P, "З");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_Q, "Й");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_R, "К");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_S, "Ы");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_T, "Е");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_U, "Г");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_V, "М");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_W, "Ц");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_X, "Ч");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_Y, "Н");
        RU_KEY_NAMES.put(GLFW.GLFW_KEY_Z, "Я");
    }
    public static void updateKeyboardLayout() {
        try {
            InputContext context = InputContext.getInstance();
            Locale locale = context.getLocale();
            isRussianLayout = locale != null && 
                             (locale.getLanguage().equals("ru") || 
                              locale.getCountry().equals("RU"));
        } catch (Exception e) {
            isRussianLayout = false;
        }
    }
    public static String getKeyName(int keyCode) {
        if (keyCode == -1) return "";
        if (isRussianLayout && RU_KEY_NAMES.containsKey(keyCode)) {
            return RU_KEY_NAMES.get(keyCode);
        } else if (EN_KEY_NAMES.containsKey(keyCode)) {
            return EN_KEY_NAMES.get(keyCode);
        }
        String keyName = InputUtil.fromKeyCode(keyCode, 0).getLocalizedText().getString();
        if (keyName.length() > 8) {
            keyName = keyName.substring(0, 7) + "...";
        }
        return keyName;
    }
    public static boolean isRussianLayout() {
        return isRussianLayout;
    }
} 
