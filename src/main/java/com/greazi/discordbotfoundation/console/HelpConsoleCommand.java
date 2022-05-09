package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;

import java.util.List;

public class HelpConsoleCommand extends SimpleConsoleCommand {

    public HelpConsoleCommand() {
        super("help");
        setDescription("Shows this help message");
        setUsage("help <command>");
    }

    @Override
    public void execute(List<String> args) {
        if(args.size() == 0) {
            System.out.println("Available commands:");
            SimpleBot.getConsoleCommands()
                    .forEach((name, command) -> {
                        if(!SimpleSettings.Console.Commands.Disabled().contains(command.getCommand().toLowerCase())) {
                            command.sendHelp();
                        }
                    });
            System.out.println("");
            System.out.println("For more information on a specific command, type `help <command>`");
        }else{
            SimpleConsoleCommand command = SimpleBot.getConsoleCommand(args.get(0));
            if(command != null) {
                command.sendUsage();
            }else{
                System.out.println(ConsoleColor.RED + "Command not found");
            }
        }
    }
}
