package ru.shun.arasakafabric.command.impl;
import ru.shun.arasakafabric.command.Command;
import ru.shun.arasakafabric.command.CommandManager;
public class HelpCommand implements Command {
    private final CommandManager commandManager;
    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }
    @Override
    public String getName() {
        return "help";
    }
    @Override
    public String getDescription() {
        return "Показывает список всех доступных команд";
    }
    @Override
    public String getSyntax() {
        return CommandManager.PREFIX + "help";
    }
    @Override
    public String execute(String[] args) {
        StringBuilder response = new StringBuilder("§6§lДоступные команды:\n");
        for (Command command : commandManager.getCommands()) {
            response.append("§b")
                   .append(command.getSyntax())
                   .append(" §7- §f")
                   .append(command.getDescription())
                   .append("\n");
        }
        return response.toString();
    }
} 
