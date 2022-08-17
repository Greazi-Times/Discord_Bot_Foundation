/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.command.core;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;

public class StopCommand extends SimpleSlashCommand {

	private List<Role> roleList = new ArrayList<>();

	private void getRoleList() {
		roleList = SimpleBot.getGuild().getRolesByName(SimpleSettings.Stop.AllowedRoles().toString(), true);
	}

	/**
	 * Set the slash command
	 */
	public StopCommand() {
		super("stop");
		description("Stop the bot from running");

		mainGuildOnly();

//		getRoleList();
//		enabledRoles(roleList);
	}

	/**
	 * The main method for the slash command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void execute(SlashCommandInteractionEvent event) {
		event.replyEmbeds(new SimpleEmbedBuilder("STOP")
				.text("Do you really want to stop me?")
				.error()
				.build()
		).addActionRow(
				SimpleBot.getButton("confirm_stop").build(),
				SimpleBot.getButton("cancel_stop").build()
		).setEphemeral(true).queue();
	}

	public static class Buttons {

		public static class Confirm extends SimpleButton {

			/**
			 * Set the button
			 */
			public Confirm() {
				super("confirm_stop");
				buttonStyle(ButtonStyle.SUCCESS);
			}

			/**
			 * The main logic of the button
			 *
			 * @param event ButtonInteractionEvent
			 */
			@Override
			protected void execute(ButtonInteractionEvent event) {
				// Sending confirm message that the bot is being stopped
				event.getMessage().editMessageEmbeds(
						new SimpleEmbedBuilder("Stop | Confirmed")
								.text("Stopping the bot...")
								.error()
								.build()
				).queue();

				// Remove all buttons from the message
				event.getMessage().editMessageComponents().queue();

				// Shutdown the bot
				SimpleBot.getJDA().shutdown();
				System.exit(0);
			}
		}

		public static class Cancel extends SimpleButton {

			/**
			 * Set the button
			 */
			public Cancel() {
				super("cancel_stop");
				buttonStyle(ButtonStyle.DANGER);
			}

			/**
			 * The main logic of the button
			 *
			 * @param event ButtonInteractionEvent
			 */
			@Override
			protected void execute(ButtonInteractionEvent event) {
				event.getMessage().editMessageEmbeds(
						new SimpleEmbedBuilder("Stop | Canceled")
								.text("Canceled stopping the bot")
								.build()
				).queue();
				event.getMessage().editMessageComponents().queue();
			}
		}
	}
}
