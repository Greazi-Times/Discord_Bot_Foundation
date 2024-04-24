package com.greazi.discordbotfoundation.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ReplyCommand extends SimpleCommand {

    public ReplyCommand() {
        super("reply");

        setDescription("Reply to a message with a message.");
        addOption(new OptionData(OptionType.STRING, "message", "The message to reply with.").setRequired(true));
        addOption(new OptionData(OptionType.USER, "user", "The user to reply to."));
        setMainGuildOnly(true);
    }

    public void onCommand(String message, User user) {

    }
}
