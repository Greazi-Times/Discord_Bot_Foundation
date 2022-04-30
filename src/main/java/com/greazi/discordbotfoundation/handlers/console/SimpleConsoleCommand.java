package com.greazi.discordbotfoundation.handlers.console;

import com.greazi.discordbotfoundation.utils.color.ConsoleColor;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleConsoleCommand {

    private final String command;
    private String description = null;
    private String usage = null;
    private final List<String> arguments = new ArrayList<>();

    public SimpleConsoleCommand(String command) {
        this.command = command;
    }

    public SimpleConsoleCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public SimpleConsoleCommand setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public SimpleConsoleCommand addArgument(String argument) {
        arguments.add(argument);
        return this;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public void sendHelp() {
        System.out.println(ConsoleColor.BLUE + this.command + ConsoleColor.RESET +  ": " + this.description);
    }

    public void sendUsage() {
        StringBuilder commandUsagePadding = new StringBuilder();
        for (int i = 0; i < this.command.length(); i++) {
            commandUsagePadding.append(" ");
        }
        commandUsagePadding.append(" ");
        commandUsagePadding.append(" ");

        System.out.println(ConsoleColor.BLUE + this.command + ConsoleColor.RESET + ": " + this.description);
        System.out.println(ConsoleColor.BLUE + commandUsagePadding + "Usage: " + ConsoleColor.RESET + this.usage);
    }

    public List<String> getArguments() {
        return arguments;
    }

    public abstract void execute(List<String> args);

}
