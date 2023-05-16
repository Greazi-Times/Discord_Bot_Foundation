package com.greazi.discordbotfoundation.handlers.console;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConsoleCommandHandler {

    private final HashMap<String, SimpleConsoleCommand> commandList = new HashMap<>();

    public ConsoleCommandHandler() {
        Debugger.debug("Console", "Console main method");

        Thread thread = new Thread(() -> {
            Console console = System.console();

            if (console == null) {
                Common.error("Console is not available there for console commands are disabled");
                return;
            }

            while (true) {
                String input = console.readLine();

                if(input == null || input.isEmpty()) continue;

                List<String> splitInput = Arrays.asList(input.split(" "));

                String command = splitInput.get(0);
                List<String> args = splitInput.subList(1, splitInput.size());

                if(command.equals("stop")){
                    execute(command, args);
                    return;
                }

                if(SimpleSettings.Console.Commands.Disabled().contains(command.toLowerCase())) continue;

                execute(command, args);
            }
        });
        if(SimpleSettings.Console.Commands.Enabled()){
            thread.start();
            Debugger.debug("Console", "Console commands enabled");
        }
    }

    public ConsoleCommandHandler addCommand(SimpleConsoleCommand module) {
        commandList.put(module.getCommand(), module);
        return this;
    }

    public SimpleConsoleCommand getCommand(String input) {
        return commandList.get(input);
    }

    public HashMap<String, SimpleConsoleCommand> getCommandList() {
        return commandList;
    }

    private void execute(String input, List<String> args) {
        SimpleConsoleCommand command = commandList.get(input);

        if(command == null) {
            Common.error("Command no found");
            return;
        }

        Debugger.debug("Console", "Executing command: " + command.getCommand());

        command.execute(args);
    }

    /**
     * Get the total amount of console commands
     * @return Total amount of console commands
     */
    public int getTotal() {
        return commandList.size();
    }
}
