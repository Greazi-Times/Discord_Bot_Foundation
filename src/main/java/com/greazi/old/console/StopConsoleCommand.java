package com.greazi.old.console;

import com.greazi.old.SimpleBot;
import com.greazi.old.handlers.console.SimpleConsoleCommand;

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
