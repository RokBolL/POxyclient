package ru.shun.arasakafabric.command;
public interface Command {
    String getName();
    String getDescription();
    String getSyntax();
    String execute(String[] args);
} 
