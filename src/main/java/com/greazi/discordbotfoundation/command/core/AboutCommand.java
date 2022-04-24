/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.core;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class AboutCommand extends SimpleSlashCommand {

    public AboutCommand() {
        setCommand("about");
        setDefaultEnabled();
        setDescription("Get some info about the bot");
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new SimpleEmbedBuilder("About the bot")
                .text("Gateway: " + event.getJDA().getGatewayPing(),
                        "Rest: " + event.getJDA().getRestPing().complete(),
                        "Response: " + event.getJDA().getResponseTotal())
                .setColor(Color.decode("#2f3136"))
                .build()).setEphemeral(true).queue();
    }
}
