package com.greazi.discordbotfoundation.tests;

import com.greazi.discordbotfoundation.command.SimpleCommand;

public class MainCommand extends SimpleCommand {

    public MainCommand() {
        super("main");
        setDescription("Main command.");
        setMainGuildOnly(true);

        addSubcommand(new Test1Command());
        addSubcommand(new Test2Command());
    }

}
