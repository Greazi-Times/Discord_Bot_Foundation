/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Make proper debug messages with IDs

public abstract class SimpleSlashCommand {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * Set the command like "/command"
     */
    private String command = "example";

    /**
     * Set the help description for the slash command
     */
    private String description = "No Description";

    /**
     * Is the command bound to only the main guild of the bot
     */
    private boolean guildOnly = false;

    /**
     * Is the command bound to a NSFW channel
     * (This means if it can only be used in a NSFW channel)
     */
    private boolean nsfwOnly = false;

    /**
     * Set the roles that can use this command
     */
    private List<Role> enabledRoles = new ArrayList<>();

    /**
     * Set the users that can use this command
     */
    private List<User> enabledUsers = new ArrayList<>();

    /**
     * Set the roles that can not use this command
     */
    private List<Role> disabledRoles = new ArrayList<>();

    /**
     * Set the users that can not use this command
     */
    private List<User> disabledUsers = new ArrayList<>();

    /**
     * Is the command enabled by default
     */
    private boolean defaultEnabled = true;

    /**
     * Set the subcommands
     */
    private List<SubcommandData> subCommands = new ArrayList<>();

    /**
     * Set the subcommand groups
     */
    private List<SubcommandGroupData> subcommandGroup = new ArrayList<>();

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    private List<OptionData> options = new ArrayList<>();

    private String category = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the command logic
     *
     * @param event SlashCommandInteractionEvent
     */
    protected abstract void execute(SlashCommandInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Set the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set this command as main guild only
     */
    public void setMainGuildOnly() {
        this.guildOnly = true;
    }

    /**
     * Set whether the command can only be used inside a NSFW channel
     */
    public void setNsfwOnly() {
        this.nsfwOnly = true;
    }

    /**
     * Set the list of roles that can use this command
     */
    public void setEnabledRoles(List<Role> roles) {
        this.enabledRoles = roles;
    }

    /**
     * Set the list of roles that can use this command
     */
    public void setEnabledRoles(Role... roles) {
        this.enabledRoles = Arrays.asList(roles);
    }

    public void setEnabledUsers(List<User> users) {
        this.enabledUsers = users;
    }

    /**
     * Set the list users that can use this command
     */
    public void setEnabledUsers(User... users) {
        this.enabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of roles that can not use the command
     */
    public void setDisabledRoles(Role... roles) {
        this.disabledRoles = Arrays.asList(roles);
    }

    /**
     * Set the list of roles that can not use the command
     */
    public void setDisabledRoles(List<Role> roles) {
        this.disabledRoles = roles;
    }

    /**
     * Set the list of users that can not use the command
     */
    public void setDisabledUsers(User... users) {
        this.disabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of users that can not use the command
     */
    public void setDisabledUsers(List<User> users) {
        this.disabledUsers = users;
    }

    /**
     * Set whether the command is enabled by default
     */
    public void setDefaultEnabled() {
        this.defaultEnabled = true;
    }

    /**
     * Set the subcommands
     */
    public void setSubCommands(List<SubcommandData> subCommands) {
        this.subCommands = subCommands;
    }

    /**
     * Set the subcommands
     */
    public void setSubCommands(SubcommandData... subCommands) {
        this.subCommands = Arrays.asList(subCommands);
    }

    /**
     * Set the subcommand group
     */
    public void setSubcommandGroup(List<SubcommandGroupData> subcommandGroupDataList) {
        this.subcommandGroup = subcommandGroupDataList;
    }

    /**
     * Set the subcommand group
     */
    public void setSubcommandGroup(SubcommandGroupData... subcommandGroupDataList) {
        this.subcommandGroup = Arrays.asList(subcommandGroupDataList);
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void setOptions(List<OptionData> optionDataList) {
        this.options = optionDataList;
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void setOptions(OptionData... optionDataList) {
        this.options = Arrays.asList(optionDataList);
    }

    public void setCategory(String category) {
        this.category = category;
    }
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

    public String getDescription() {
        return description;
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
     * Whether the command can only be used inside a NSFW channel
     *
     * @return the restriction to a NSFW channel
     */
    public boolean getNsfwOnly() {
        return nsfwOnly;
    }

    /**
     * Returns a list of the roles that can use this command
     *
     * @return the allowed roles
     */
    public List<Role> getEnabledRoles() {
        return enabledRoles;
    }

    /**
     * Returns a list of users that can use this command
     *
     * @return the allowed users
     */
    public List<User> getEnabledUsers() {
        return enabledUsers;
    }

    /**
     * Returns a list of roles that can not use the command
     *
     * @return the disallowed roles
     */
    public List<Role> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * Returns a list of users that can not use the command
     *
     * @return the disallowed users
     */
    public List<User> getDisabledUsers() {
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
    public List<SubcommandData> getSubCommands() {
        return subCommands;
    }

    /**
     * Returns the subcommand group
     *
     * @return the subcommand group
     */
    public List<SubcommandGroupData> getSubcommandGroup() {
        return subcommandGroup;
    }

    /**
     * Get the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     *
     * @return the options
     */
    public List<OptionData> getOptions() {
        return options;
    }

    public String getCategory() {
        return category;
    }

}
