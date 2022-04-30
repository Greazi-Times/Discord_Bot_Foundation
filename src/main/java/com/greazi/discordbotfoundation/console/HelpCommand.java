package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;

import java.util.List;

public class HelpCommand extends SimpleConsoleCommand {

    public HelpCommand() {
        super("help");
        setDescription("Shows this help message");
        setUsage("help <command>");
    }

    @Override
    public void execute(List<String> args) {
        if(args.size() == 0) {
            System.out.println("Available commands:");
            SimpleBot.getConsoleCommandHandler().getCommandList().forEach((name, command) -> {
                command.sendHelp();
            });
            System.out.println("");
            System.out.println("For more information on a specific command, type `help <command>`");
        }else{
            SimpleConsoleCommand command = SimpleBot.getConsoleCommandHandler().getCommand(args.get(0));
            if(command != null) {
                command.sendUsage();
            }else{
                System.out.println(ConsoleColor.RED + "Command not found");
            }
        }
    }
}
