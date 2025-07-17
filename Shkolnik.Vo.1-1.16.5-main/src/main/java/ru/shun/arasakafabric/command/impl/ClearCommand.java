package ru.shun.arasakafabric.command.impl;
import ru.shun.arasakafabric.command.Command;
import ru.shun.arasakafabric.command.CommandManager;
public class ClearCommand implements Command {
    private static final int CLEAR_LINES = 300;
    @Override
    public String getName() {
        return "clear";
    }
    @Override
    public String getDescription() {
        return "Очищает чат";
    }
    @Override
    public String getSyntax() {
        return CommandManager.PREFIX + "clear";
    }
    @Override
    public String execute(String[] args) {
        StringBuilder clearString = new StringBuilder();
        for (int i = 0; i < CLEAR_LINES; i++) {
            clearString.append(" \n");
        }
        return clearString.toString();
    }
} 
