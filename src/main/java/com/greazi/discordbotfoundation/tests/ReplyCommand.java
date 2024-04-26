package com.greazi.discordbotfoundation.tests;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.checkers.RolesChecker;
import com.greazi.discordbotfoundation.command.checkers.response.DefaultCommandProvider;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class ReplyCommand extends SimpleCommand {

    public ReplyCommand() {
        super("reply");

        setDescription("Reply to a message with a message.");

        addOption(new OptionData(OptionType.STRING, "message", "The message to reply with.").setRequired(true));
        addOption(new OptionData(OptionType.USER, "user", "The user to reply to."));

        addChecker(RolesChecker.createAllowed(List.of("938425015274180628")));

        setMainGuildOnly(true);
    }

    public void onCommand(String message, User user) {
        if (user == null) {
            reply(message);
        } else {
            reply(user.getAsMention() + ", " + message);
        }
    }
}
