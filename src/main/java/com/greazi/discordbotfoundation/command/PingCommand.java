/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.Common;

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
        setDescription("Get the latency of the bot - 1");
    }

    @Override
    protected void onCommand() {
        Common.warning("Ping command executed");
        replyEmbed("Bot latency",
                "Gateway: " + event.getJDA().getGatewayPing(),
                "Rest: " + event.getJDA().getRestPing().complete(),
                "Response: " + event.getJDA().getResponseTotal()
        );
    }
}
