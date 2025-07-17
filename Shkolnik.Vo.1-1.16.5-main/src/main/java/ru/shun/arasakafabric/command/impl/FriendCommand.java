package ru.shun.arasakafabric.command.impl;
import ru.shun.arasakafabric.client.FriendManager;
import ru.shun.arasakafabric.command.Command;
import ru.shun.arasakafabric.command.CommandManager;
import java.util.List;
public class FriendCommand implements Command {
    private final FriendManager friendManager = FriendManager.getInstance();
    @Override
    public String getName() {
        return "friend";
    }
    @Override
    public String getDescription() {
        return "Управление списком друзей";
    }
    @Override
    public String getSyntax() {
        return CommandManager.PREFIX + "friend [add/remove/list] [ник]";
    }
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            return "§cИспользование: " + getSyntax();
        }
        String action = args[0].toLowerCase();
        switch (action) {
            case "add":
                return handleAddCommand(args);
            case "remove":
                return handleRemoveCommand(args);
            case "list":
                return handleListCommand();
            default:
                return "§cНеизвестная команда. Используйте " + getSyntax();
        }
    }
    private String handleAddCommand(String[] args) {
        if (args.length < 2) {
            return "§cИспользование: " + CommandManager.PREFIX + "friend add [ник]";
        }
        String name = args[1];
        if (friendManager.addFriend(name)) {
            return "§aИгрок §f" + name + " §aдобавлен в список друзей";
        } else {
            return "§cИгрок §f" + name + " §cуже в списке друзей";
        }
    }
    private String handleRemoveCommand(String[] args) {
        if (args.length < 2) {
            return "§cИспользование: " + CommandManager.PREFIX + "friend remove [ник]";
        }
        String name = args[1];
        if (friendManager.removeFriend(name)) {
            return "§aИгрок §f" + name + " §aудален из списка друзей";
        } else {
            return "§cИгрок §f" + name + " §cне найден в списке друзей";
        }
    }
    private String handleListCommand() {
        List<String> friends = friendManager.getFriends();
        if (friends.isEmpty()) {
            return "§cУ вас нет друзей в списке";
        }
        StringBuilder message = new StringBuilder("§aСписок друзей (" + friends.size() + "):\n");
        for (String friend : friends) {
            message.append("§7- §f").append(friend).append("\n");
        }
        return message.toString();
    }
} 
