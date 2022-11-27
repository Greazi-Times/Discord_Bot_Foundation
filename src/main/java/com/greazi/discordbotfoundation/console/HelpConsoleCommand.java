package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;

import java.util.List;

public class HelpConsoleCommand extends SimpleConsoleCommand {

    public HelpConsoleCommand() {
        super("help");
        description("Shows this help message");
        usage("help <command>");
    }

    @Override
    public void onConsoleCommand(final List<String> args) {
        if (args.size() == 0) {
            System.out.println("Available commands:");
            SimpleBot.getConsoleCommands()
                    .forEach((name, command) -> {
                        if (!SimpleSettings.Console.Commands.Disabled().contains(command.getCommand().toLowerCase())) {
                            command.sendHelp();
                        }
                    });
            System.out.println();
            System.out.println("For more information on a specific command, type `help <command>`");
        } else {
            final SimpleConsoleCommand command = SimpleBot.getConsoleCommand(args.get(0));
            if (command != null) {
                command.sendUsage();
            } else {
                System.out.println(ConsoleColor.RED + "Command not found");
            }
        }
    }
}
