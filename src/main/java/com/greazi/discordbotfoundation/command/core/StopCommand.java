/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.core;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.constants.Constants;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StopCommand extends SimpleSlashCommand {

	private List<Role> roleList = new ArrayList<>();

	private void getRoleList() {
		Common.warning("Getting enabled roles for Stop command!");
		roleList = SimpleBot.getGuild().getRolesByName(SimpleSettings.Stop.AllowedRoles().toString(), true);
	}

	public StopCommand() {
		getRoleList();

		setCommand("stop");

		if(SimpleSettings.Stop.Enabled()) setDefaultEnabled();

		setDescription("Stop the bot from running");
		setEnabledRoles(roleList);
	}

	@Override
	protected void execute(SlashCommandInteractionEvent event) {
		event.replyEmbeds(new SimpleEmbedBuilder("STOP")
				.text("Do you really want to stop me?")
				.error()
				.build()).setEphemeral(true).queue();
	}
}
