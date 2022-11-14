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
        super("about");
        description("Get some info about the bot");
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {


        SimpleEmbedBuilder embed = new SimpleEmbedBuilder("About the bot");

        embed.text("This bot uses the [Discord Bot Foundation](https://github.com/Greazi-Times/Discord_Bot_Foundation) version: " + Constants.Version.FOUNDATION + "\n\nBot information:");
        embed.field("Bot name", SimpleSettings.Bot.Name(), true);
        embed.field("Bot version", SimpleBot.getVersion(), true);
        embed.field("Developer", SimpleBot.getInstance().getDeveloperName(), true);
        if(SimpleBot.getInstance().getFoundedYear() != -1) embed.field("Founded", SimpleBot.getInstance().getDeveloperName(), true);
        embed.setColor(Color.decode("#2f3136"));

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
