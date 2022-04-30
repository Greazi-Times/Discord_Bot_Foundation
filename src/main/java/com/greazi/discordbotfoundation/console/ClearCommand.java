package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;

import java.util.List;

public class ClearCommand extends SimpleConsoleCommand {

    public ClearCommand() {
        super("clear");
        setDescription("Clears the console");
        setUsage("clear");
    }

    @Override
    public void execute(List<String> args) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}