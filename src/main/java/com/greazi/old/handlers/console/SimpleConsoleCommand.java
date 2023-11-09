package com.greazi.old.handlers.console;

import com.greazi.old.utils.color.ConsoleColor;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleConsoleCommand {

    private final String command;
    private String description = null;
    private String usage = null;
    private final List<String> arguments = new ArrayList<>();

    private Member member = null;
    private User user = null;


    public SimpleConsoleCommand(final String command) {
        this.command = command;
    }

    public SimpleConsoleCommand description(final String description) {
        this.description = description;
        return this;
    }

    public SimpleConsoleCommand usage(final String usage) {
        this.usage = usage;
        return this;
    }

    public SimpleConsoleCommand argument(final String argument) {
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
        System.out.println(ConsoleColor.BLUE + this.command + ConsoleColor.RESET + ": " + this.description);
    }

    public void sendUsage() {
        final StringBuilder commandUsagePadding = new StringBuilder();
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

    public final boolean execute(final List<String> args) {
        // TODO: Add checks in here
        this.onConsoleCommand(args);
        return true;
    }

    public abstract void onConsoleCommand(List<String> args);

}
