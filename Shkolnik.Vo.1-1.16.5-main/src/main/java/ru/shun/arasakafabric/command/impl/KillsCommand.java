package ru.shun.arasakafabric.command.impl;
import ru.shun.arasakafabric.command.Command;
import ru.shun.arasakafabric.command.CommandManager;
import ru.shun.arasakafabric.stats.PlayerStats;
public class KillsCommand implements Command {
    @Override
    public String getName() {
        return "kills";
    }
    @Override
    public String getDescription() {
        return "Показывает количество убийств за текущую сессию";
    }
    @Override
    public String getSyntax() {
        return CommandManager.PREFIX + "kills [reset]";
    }
    @Override
    public String execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reset")) {
            PlayerStats.resetKills();
            return "Статистика убийств сброшена!";
        }
        int kills = PlayerStats.getKills();
        String suffix = getSuffix(kills);
        return "Вы убили §b" + kills + " §fигрок" + suffix + " за текущую сессию";
    }
    private String getSuffix(int number) {
        if (number % 10 == 1 && number % 100 != 11) {
            return "а";
        } else if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            return "ов";
        } else {
            return "ов";
        }
    }
} 
