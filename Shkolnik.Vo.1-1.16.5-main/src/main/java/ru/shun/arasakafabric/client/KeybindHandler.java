package ru.shun.arasakafabric.client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleManager;
import ru.shun.arasakafabric.ui.ClickGuiMain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class KeybindHandler {
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();
    private static boolean wasGuiOpen = false;
    public static void handleKeyTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return;
        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), 344)) {
            if (!wasGuiOpen && client.currentScreen == null) {
                client.openScreen(new ClickGuiMain());
            }
            wasGuiOpen = true;
        } else {
            wasGuiOpen = false;
        }
        Map<Integer, List<Module>> keyBindingsMap = new HashMap<>();
        for (Module module : ModuleManager.getModules()) {
            int key = module.getBind();
            if (key == -1) continue;
            keyBindingsMap.computeIfAbsent(key, k -> new ArrayList<>()).add(module);
        }
        for (Map.Entry<Integer, List<Module>> entry : keyBindingsMap.entrySet()) {
            int key = entry.getKey();
            List<Module> modules = entry.getValue();
            boolean isPressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), key);
            boolean wasPressed = keyStates.getOrDefault(key, false);
            if (isPressed && !wasPressed) {
                for (Module module : modules) {
                    module.toggled();
                }
            }
            keyStates.put(key, isPressed);
        }
    }
}
