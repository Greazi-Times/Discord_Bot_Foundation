/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.core;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A simple command to test new values and features
 */
public class TestCommand extends SimpleSlashCommand {

	/**
	 * Setup the command
	 */
	public TestCommand() {
		setCommand("test");
		setDefaultEnabled();
		setDescription("The core test command to test values and new systems");
		// Only allow the foundation owners to run these commands
		setEnabledUsers(UserSnowflake.fromId(619084935655063552L), UserSnowflake.fromId(240439907833741322L));
	}

	@Override
	protected void execute(SlashCommandInteractionEvent event) {
		Common.log("Test command executed");
		event.reply("Ran test command").setEphemeral(true).queue();
	}
}
