package com.greazi.old.console;

import com.greazi.old.handlers.console.SimpleConsoleCommand;

import java.util.List;

public class ClearConsoleCommand extends SimpleConsoleCommand {

    public ClearConsoleCommand() {
        super("clear");
        description("Clears the console");
        usage("clear");
    }

    @Override
    public void onConsoleCommand(final List<String> args) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
