/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command;

import java.awt.*;

/**
 * A simple ping command to get the latency of the bot
 * The message output can be changed by overriding this file!
 */
public class PingCommand extends SimpleCommand {

    /**
     * Default enabled can not be disabled.
     */
    public PingCommand() {
        super("ping");
        setDescription("Get the latency of the bot");
    }

    @Override
    protected void onCommand() {
        event.replyEmbeds(new SimpleEmbedBuilder("Bot latency")
                .text("Gateway: " + event.getJDA().getGatewayPing(),
                        "Rest: " + event.getJDA().getRestPing().complete(),
                        "Response: " + event.getJDA().getResponseTotal())
                .setColor(Color.decode("#2f3136"))
                .build()).setEphemeral(true).queue();
    }
}
