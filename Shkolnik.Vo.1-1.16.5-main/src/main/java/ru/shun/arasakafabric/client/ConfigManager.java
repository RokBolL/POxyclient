package ru.shun.arasakafabric.client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.client.MinecraftClient;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleManager;
import ru.shun.arasakafabric.ui.ISetting;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class ConfigManager {
    private static ConfigManager instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configsDirectory;
    private ConfigManager() {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        configsDirectory = new File(gameDir, "Shkolnik/configs");
        if (!configsDirectory.exists()) {
            boolean created = configsDirectory.mkdirs();
            System.out.println("[ConfigManager] Создание директории " + configsDirectory.getAbsolutePath() + ": " + (created ? "успешно" : "не удалось"));
        }
    }
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    public boolean saveConfig(String configName) {
        try {
            if (!configsDirectory.exists()) {
                configsDirectory.mkdirs();
            }
            File configFile = new File(configsDirectory, configName + ".json");
            JsonObject configObject = new JsonObject();
            JsonArray enabledModules = new JsonArray();
            JsonObject moduleSettings = new JsonObject();
            for (Module module : ModuleManager.getModules()) {
                if (module.isEnabled()) {
                    enabledModules.add(module.getName());
                }
                JsonObject moduleConfig = new JsonObject();
                moduleConfig.addProperty("bind", module.getBind());
                if (!module.getSettings().isEmpty()) {
                    JsonObject settingsObject = new JsonObject();
                    for (ISetting setting : module.getSettings()) {
                        if (setting.getValue() != null) {
                            settingsObject.addProperty(setting.getName(), setting.getValue().toString());
                        }
                    }
                    moduleConfig.add("settings", settingsObject);
                }
                moduleSettings.add(module.getName(), moduleConfig);
            }
            configObject.add("enabledModules", enabledModules);
            configObject.add("moduleSettings", moduleSettings);
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(configObject, writer);
            }
            System.out.println("[ConfigManager] Конфигурация сохранена: " + configFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.out.println("[ConfigManager] Ошибка при сохранении конфигурации: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean loadConfig(String configName) {
        try {
            File configFile = new File(configsDirectory, configName + ".json");
            if (!configFile.exists()) {
                System.out.println("[ConfigManager] Конфигурация не найдена: " + configFile.getAbsolutePath());
                return false;
            }
            JsonObject configObject;
            try (FileReader reader = new FileReader(configFile)) {
                configObject = gson.fromJson(reader, JsonObject.class);
            }
            List<Module> allModules = ModuleManager.getModules();
            for (Module module : allModules) {
                if (module.isEnabled()) {
                    module.setEnabled(false);
                }
            }
            if (configObject.has("enabledModules")) {
                JsonArray enabledModules = configObject.getAsJsonArray("enabledModules");
                for (int i = 0; i < enabledModules.size(); i++) {
                    String moduleName = enabledModules.get(i).getAsString();
                    Module module = ModuleManager.getByName(moduleName);
                    if (module != null && !module.isEnabled()) {
                        module.setEnabled(true);
                    }
                }
            }
            if (configObject.has("moduleSettings")) {
                JsonObject moduleSettings = configObject.getAsJsonObject("moduleSettings");
                for (Module module : allModules) {
                    if (moduleSettings.has(module.getName())) {
                        JsonObject moduleConfig = moduleSettings.getAsJsonObject(module.getName());
                        if (moduleConfig.has("bind")) {
                            module.setBind(moduleConfig.get("bind").getAsInt());
                        }
                        if (moduleConfig.has("settings")) {
                            JsonObject settingsObject = moduleConfig.getAsJsonObject("settings");
                            for (ISetting setting : module.getSettings()) {
                                if (settingsObject.has(setting.getName())) {
                                    String valueStr = settingsObject.get(setting.getName()).getAsString();
                                    setting.fromString(valueStr);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("[ConfigManager] Конфигурация загружена: " + configFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.out.println("[ConfigManager] Ошибка при загрузке конфигурации: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getAvailableConfigs() {
        List<String> configs = new ArrayList<>();
        if (configsDirectory.exists() && configsDirectory.isDirectory()) {
            File[] configFiles = configsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (configFiles != null) {
                for (File file : configFiles) {
                    String name = file.getName();
                    configs.add(name.substring(0, name.length() - 5));
                }
            }
        }
        return configs;
    }
    public List<String> getSuggestions(String action, String currentArg) {
        if (action.isEmpty()) {
            return List.of("save", "load", "list");
        }
        if (action.equals("load") && currentArg != null) {
            return getAvailableConfigs().stream()
                    .filter(name -> currentArg.isEmpty() || name.startsWith(currentArg))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
} 
