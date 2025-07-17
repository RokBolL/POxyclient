package ru.shun.arasakafabric.command.impl;
import ru.shun.arasakafabric.client.ConfigManager;
import ru.shun.arasakafabric.command.Command;
import ru.shun.arasakafabric.command.CommandManager;
import java.util.List;
public class ConfigCommand implements Command {
    @Override
    public String getName() {
        return "cfg";
    }
    @Override
    public String getDescription() {
        return "Управление конфигурациями модулей";
    }
    @Override
    public String getSyntax() {
        return CommandManager.PREFIX + "cfg [save/load/list] [название]";
    }
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            return "§cИспользование: " + getSyntax();
        }
        String action = args[0].toLowerCase();
        ConfigManager configManager = ConfigManager.getInstance();
        switch (action) {
            case "save":
                return handleSaveCommand(args, configManager);
            case "load":
                return handleLoadCommand(args, configManager);
            case "list":
                return handleListCommand(configManager);
            default:
                return "§cНеизвестная команда. Используйте " + getSyntax();
        }
    }
    private String handleSaveCommand(String[] args, ConfigManager configManager) {
        if (args.length < 2) {
            return "§cИспользование: " + CommandManager.PREFIX + "cfg save [название]";
        }
        String configName = args[1];
        if (configManager.saveConfig(configName)) {
            return "§aКонфигурация §f" + configName + " §aуспешно сохранена";
        } else {
            return "§cОшибка при сохранении конфигурации §f" + configName;
        }
    }
    private String handleLoadCommand(String[] args, ConfigManager configManager) {
        if (args.length < 2) {
            return "§cИспользование: " + CommandManager.PREFIX + "cfg load [название]";
        }
        String configName = args[1];
        if (configManager.loadConfig(configName)) {
            return "§aКонфигурация §f" + configName + " §aуспешно загружена";
        } else {
            return "§cОшибка при загрузке конфигурации §f" + configName;
        }
    }
    private String handleListCommand(ConfigManager configManager) {
        List<String> configs = configManager.getAvailableConfigs();
        if (configs.isEmpty()) {
            return "§cСохраненных конфигураций нет";
        }
        StringBuilder message = new StringBuilder("§aДоступные конфигурации (" + configs.size() + "):\n");
        for (String config : configs) {
            message.append("§7- §f").append(config).append("\n");
        }
        return message.toString();
    }
} 
