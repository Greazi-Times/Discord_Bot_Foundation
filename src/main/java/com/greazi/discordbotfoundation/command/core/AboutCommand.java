/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.core;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.constants.Constants;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

/**
 * An about command that displays information about the bot
 */
public class AboutCommand extends SimpleSlashCommand {

    public AboutCommand() {
        setCommand("about");
        setDefaultEnabled();
        setDescription("Get some info about the bot");
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new SimpleEmbedBuilder("About the bot")
                .text("This bot uses the [Discord Bot Foundation](https://github.com/Greazi-Times/Discord_Bot_Foundation) version: " + Constants.Version.FOUNDATION + "\n\nBot information:")
                .field("Bot name", SimpleSettings.Bot.Name(), true)
                .field("Bot version", SimpleBot.getVersion(), true)
                .field("Developer", SimpleBot.getInstance().getDeveloper(), true)
                .setColor(Color.decode("#2f3136"))
                .build()).setEphemeral(true).queue();
    }
}