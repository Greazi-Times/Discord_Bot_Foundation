package com.greazi.discordbotfoundation.tests;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test1Command extends SimpleCommand {

    public Test1Command() {
        super("test1");
        setDescription("Test 1 command.");

        addOption(new OptionData(OptionType.STRING, "message", "The message to send.").setRequired(true));
        addOption(new OptionData(OptionType.ATTACHMENT, "attachment", "The attachment to send."));
    }

    public void onCommand(@NotNull String message, @Nullable Message.Attachment attachment) {
        event.reply("Test 1 command executed with message: " + message + " and attachment: " + (attachment == null ? "(none)" : attachment.getFileName())).queue();
    }

}
