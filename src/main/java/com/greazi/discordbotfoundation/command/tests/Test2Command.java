package com.greazi.discordbotfoundation.command.tests;

import com.greazi.discordbotfoundation.command.SimpleCommand;

public class Test2Command extends SimpleCommand {

    public Test2Command() {
        super("test2");
        setDescription("Test 2 command.");
    }

    public void onCommand() {
        event.reply("Test 2 command executed.").queue();
    }

}
