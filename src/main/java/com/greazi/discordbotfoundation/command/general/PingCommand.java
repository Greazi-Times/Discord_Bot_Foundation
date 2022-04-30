/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.general;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

/**
 * A simple ping command to get the latency of the bot
 * The message output can be changed by overriding this file!
 */
public class PingCommand extends SimpleSlashCommand {

    /**
     * Default enabled can not be disabled.
     */
    public PingCommand() {
        setCommand("ping");
        setDefaultEnabled();
        setDescription("Test the latency of the bot");
    }

    /**
     * The main code of the ping command
     * @param event SlashCommandInteractionEvent
     */
    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new SimpleEmbedBuilder("Bot latency")
                .text("Gateway: " + event.getJDA().getGatewayPing(),
                        "Rest: " + event.getJDA().getRestPing().complete(),
                        "Response: " + event.getJDA().getResponseTotal())
                .setColor(Color.decode("#2f3136"))
                .build()).setEphemeral(true).queue();
    }
}
