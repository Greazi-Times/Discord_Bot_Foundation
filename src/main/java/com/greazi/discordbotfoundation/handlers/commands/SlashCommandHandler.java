/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * The slash command handler that handles the whole slash command event.
 * Uses the information of {@link SimpleSlashCommand}
 */
public class SlashCommandHandler extends ListenerAdapter {

	// The hashmap and list of tall the slash commands
	private final HashMap<String, SimpleSlashCommand> cmdList = new HashMap<>();
	private final List<SlashCommandData> publicSlashCommands = new ArrayList<>();
	private final List<SlashCommandData> mainGuildSlashCommands = new ArrayList<>();

	/**
	 * The main slash command handler
	 */
	public SlashCommandHandler() {
		Debugger.debug("SlashCommand", "Slash Command main method");
		SimpleBot.getJDA().addEventListener(this);
	}

	/**
	 * Add a slash command to the SlashCommand list
	 *
	 * @param module The SlashCommand module
	 * @return this
	 */
	public SlashCommandHandler addCommand(final SimpleSlashCommand module) {
		// Retrieve the slash command data
		final SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());

		// add sub commands
		final List<SubcommandData> moduleSubcommands = module.getSubCommands();
		for (final SubcommandData var : moduleSubcommands) {
			command.addSubcommands(var);
		}

		// Add sub command groups
		final List<SubcommandGroupData> moduleSubcommandGroup = module.getSubcommandGroup();
		for (final SubcommandGroupData var : moduleSubcommandGroup) {
			command.addSubcommandGroups(var);
		}

		// Get options if sub commands are empty
		if (module.getSubCommands().isEmpty() && module.getSubcommandGroup().isEmpty() && !module.getOptions().isEmpty()) {
			final List<OptionData> moduleOptions = module.getOptions();
			for (final OptionData var : moduleOptions) {
				command.addOptions(var);
			}
		}

		// Set description
		command.setDescription(module.getDescription());


		// TODO: When api has an update for the slash command system update it to that system
		//       Check it out here: https://github.com/DV8FromTheWorld/JDA/pull/2113
		// Add the slash command
		if (module.getGuildOnly()) {
			mainGuildSlashCommands.add(command);
			Debugger.debug("SlashCommand", "Guild only for: " + command.getName());
		} else {
			publicSlashCommands.add(command);
			Debugger.debug("SlashCommand", "Public command for: " + command.getName());
		}

		// Add it to our internal list
		cmdList.put(module.getCommand(), module);

		return this;
	}

	/**
	 * Register all slash commands to JDA
	 */
	public void registerCommands() {
//        for(Command command : SimpleBot.getJDA().retrieveCommands().complete()) {
//            SimpleBot.getJDA().deleteCommandById(command.getIdLong());
//        }

		// Check if the slash commands isn't empty
		if (mainGuildSlashCommands.isEmpty()) Common.warning("Be aware no main guild slash commands can be found!");
		if (publicSlashCommands.isEmpty()) Common.warning("Be aware no public slash commands can be found!");

		Debugger.debug("SlashCommand", "Registering slash commands");

		// Add all slash commands to the main guild
		SimpleBot.getMainGuild().updateCommands()
				.addCommands(mainGuildSlashCommands)
				.queue();

		// Add the commands to all the guilds PUBLIC!!
		SimpleBot.getJDA().updateCommands()
				.addCommands(publicSlashCommands)
				.queue();
	}

	/**
	 * The main event listener for the slash command interaction event
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	@SubscribeEvent
	public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
		Debugger.debug("SlashCommand", "A Slash Command has been used");

		// Log who used a slash command
		Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Slash Command: " + ConsoleColor.CYAN + event.getCommandString() + ConsoleColor.RESET);

		// Retrieve the command class from the command that has been run
		final SimpleSlashCommand module = cmdList.get(event.getName());

		// If the module doesn't exist in the bot return an error
		if (module == null) {
			event.replyEmbeds(new SimpleEmbedBuilder("ERROR - command not found")
					.text("The command that you have used does not exist or hasn't been activated!",
							"Please contact an admin and report this error!")
					.error()
					.setFooter("")
					.build()).setEphemeral(true).queue();
			return;
		}

		// Check if main guild is enabled
		if (!Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.Bot.MainGuild()) && module.getGuildOnly()) {
			return;
		}

		// Run the command logic
		module.execute(event);
	}

	// TODO: Make the total count better so it will show the actually registered commands with JDA instead of the count
	//       of the lists that are made

	/**
	 * Get the total amount of slash commands registered
	 *
	 * @return Total amount of slash commands
	 */
	public int getTotal() {
		return cmdList.size();
	}

	/**
	 * Get the total amount of public registered slash commands
	 *
	 * @return Total amount of public slash commands
	 */
	public int getPublicTotal() {
		return publicSlashCommands.size();
	}

	/**
	 * Get the total amount of private registered slash commands
	 *
	 * @return Total amount of private slash commands
	 */
	public int getGuildTotal() {
		return mainGuildSlashCommands.size();
	}
}
