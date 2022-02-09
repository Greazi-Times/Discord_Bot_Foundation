/*
 * Copyright (c) 2021 - 2022. Greazi All rights reservered
 *
 * You may not copy or redistribute any of this code.
 */

package com.greazi.discordbotfoundation.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2><b>Slash command</b></h2>
 *
 * A simple slash command handler that allows you to make a easy slash command
 * Use the pre-made setup to register the command
 */
public abstract class SimpleSlashCommand {

	// ----------------------------------------------------------------------------------------
	// Main options
	// ----------------------------------------------------------------------------------------

	/**
	 * Set the command like "/command"
	 */
	protected String command = "null";

	/**
	 * Set the help description fo the slash command
	 */
	protected String commandHelp = null;

	/**
	 * Set the category of the command that will be displayed in the "/help"
	 */
	protected String category = null;

	/**
	 * Is the command bound to only the main guild of the bot
	 */
	protected boolean guildOnly = false;

	/**
	 * Is the command bound to a NSWF channel
	 * (This means if it can only be used in a NSWF channel)
	 */
	protected boolean nswfOnly = false;

	/**
	 * Set the roles that can use this command
	 */
	protected String[] enabledRoles = new String[]{};

	/**
	 * Set the users that can use this command
	 */
	protected String[] enabledUsers = new String[]{};

	/**
	 * Set the roles that can not use this command
	 */
	protected String[] disabledRoles = new String[]{};

	/**
	 * Set the users that can not use this command
	 */
	protected String[] disabledUsers = new String[]{};

	/**
	 * Is the command enabled by default
	 */
	protected boolean defaultEnabled = true;

	/**
	 * Set the subcommands
	 */
	protected SimpleSlashCommand[] subCommands = new SimpleSlashCommand[0];

	/**
	 * Set the subcommand groups
	 */
	protected SubcommandGroupData subcommandGroup = null;

	/**
	 * Set the options of the command
	 * !! This can not be used alongside subCommand or subCommandGroup !!
	 */
	protected List<OptionData> options = new ArrayList<>();

	// ----------------------------------------------------------------------------------------
	// Main methods
	// ----------------------------------------------------------------------------------------

	protected abstract void execute(SlashCommandInteractionEvent event);

	// ----------------------------------------------------------------------------------------
	// Getters
	// ----------------------------------------------------------------------------------------

	/**
	 * Returns the command
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the command help/description
	 *
	 * @return the commmand help/description
	 */
	public String getCommandHelp() {
		return commandHelp;
	}

	/**
	 * Returns the category
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Whether the command is restricted to the main guild of the bot
	 *
	 * @return the restricted guild
	 */
	public boolean getGuildOnly() {
		return guildOnly;
	}

	/**
	 * Whether the command can only be used inside a NSWF channel
	 *
	 * @return the restriction to a NSWF channel
	 */
	public boolean getNswfOnly() {
		return nswfOnly;
	}

	/**
	 * Returns a list of the roles that can use this command
	 *
	 * @return the allowed roles
	 */
	public String[] getEnabledRoles() {
		return enabledRoles;
	}

	/**
	 * Returns a list of users that can use this command
	 *
	 * @return the allowed users
	 */
	public String[] getEnabledUsers() {
		return enabledUsers;
	}

	/**
	 * Returns a list of roles that can not use the command
	 *
	 * @return the disallowed roles
	 */
	public String[] getDisabledRoles() {
		return disabledRoles;
	}

	/**
	 * Returns a list of users that can not use the command
	 *
	 * @return the disallowed users
	 */
	public String[] getDisabledUsers() {
		return disabledUsers;
	}

	/**
	 * Whether the command is enabled by default
	 *
	 * @return the default enabled
	 */
	public boolean getDefaultEnabled() {
		return defaultEnabled;
	}

	/**
	 * Returns the subcommands
	 *
	 * @return the subcommands
	 */
	public SimpleSlashCommand[] getSubCommands() {
		return subCommands;
	}

	/**
	 * Returns the subcommand group
	 *
	 * @return the subcommand group
	 */
	public SubcommandGroupData getSubcommandGroup() {
		return subcommandGroup;
	}

	/**
	 * Set the options of the command
	 * !! This can not be used alongside subCommand or subCommandGroup !!
	 */
	public List<OptionData> getOptions() {
		return options;
	}

}
