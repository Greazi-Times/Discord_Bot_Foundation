package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;

import java.util.List;

public class StopConsoleCommand extends SimpleConsoleCommand {

    public StopConsoleCommand() {
        super("stop");
        description("Stops the bot");
        usage("stop");
    }

    @Override
    public void onConsoleCommand(final List<String> args) {
        SimpleBot.getInstance().stop();
    }
}
