package ru.shun.arasakafabric.command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import ru.shun.arasakafabric.command.impl.ClearCommand;
import ru.shun.arasakafabric.command.impl.ConfigCommand;
import ru.shun.arasakafabric.command.impl.FriendCommand;
import ru.shun.arasakafabric.command.impl.HelpCommand;
import ru.shun.arasakafabric.command.impl.KillsCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class CommandManager {
    private final List<Command> commands = new ArrayList<>();
    public static final String PREFIX = "."; 
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public void init() {
        registerCommand(new HelpCommand(this));
        registerCommand(new KillsCommand());
        registerCommand(new ClearCommand());
        registerCommand(new FriendCommand());
        registerCommand(new ConfigCommand());
    }
    public void registerCommand(Command command) {
        commands.add(command);
    }
    public List<Command> getCommands() {
        return commands;
    }
    public List<String> getCommandNames() {
        return commands.stream()
                .map(Command::getName)
                .collect(Collectors.toList());
    }
    public List<String> getCommandArgSuggestions(String commandName, String[] args) {
        List<String> suggestions = new ArrayList<>();
        Command command = findCommandByName(commandName);
        if (command == null) {
            return suggestions;
        }
        if (command instanceof KillsCommand && args.length == 1 && (args[0].isEmpty() || "reset".startsWith(args[0].toLowerCase()))) {
            suggestions.add("reset");
        }
        if (command instanceof HelpCommand) {
            if (args.length == 0 || args.length == 1) {
                suggestions.addAll(getCommandNames().stream()
                        .filter(cmd -> args.length == 0 || cmd.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList()));
            }
        }
        if (command.getName().equalsIgnoreCase("cfg")) {
            if (args.length == 0 || args.length == 1) {
                List<String> actions = Arrays.asList("save", "load", "list");
                suggestions.addAll(actions.stream()
                        .filter(action -> args.length == 0 || action.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList()));
            } 
            else if (args.length == 2 && args[0].equalsIgnoreCase("load")) {
                suggestions.addAll(ru.shun.arasakafabric.client.ConfigManager.getInstance()
                        .getAvailableConfigs().stream()
                        .filter(name -> name.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList()));
            }
        }
        return suggestions;
    }
    public Command findCommandByName(String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }
    public boolean handleMessage(String message) {
        if (!message.startsWith(PREFIX)) {
            return false;
        }
        String[] parts = message.substring(PREFIX.length()).split(" ");
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        if (commandName.equalsIgnoreCase("clear")) {
            Command clearCommand = findCommandByName(commandName);
            if (clearCommand != null) {
                String result = clearCommand.execute(args);
                if (result != null && !result.isEmpty()) {
                    displayRawMessage(result);
                }
                return true;
            }
        }
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                String result = command.execute(args);
                if (result != null && !result.isEmpty()) {
                    displayMessage(result);
                }
                return true;
            }
        }
        displayMessage("§c[Ошибка] §fКоманда не найдена. Используйте " + PREFIX + "help для списка команд.");
        return true;
    }
    public static void displayMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of("§b[Shkolnik] §f" + message), false);
        }
    }
    public static void displayRawMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of(message), false);
        }
    }
} 
