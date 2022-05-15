/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import com.google.gson.annotations.Since;
import com.greazi.discordbotfoundation.utils.annotations.Disabled;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A slash command creator to simply create your slash command
 */
public abstract class SimpleSlashCommand {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * The actual command itself
     */
    private String command = "example";

    /**
     * The help description for the slash command
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
    @Disabled(since = "2.0")
    private boolean nsfwOnly = false;

    /**
     * The roles that can use this command
     */
    @Disabled(since = "2.0")
    private List<Role> enabledRoles = new ArrayList<>();

    /**
     * The users that can use this command
     */
    @Disabled(since = "2.0")
    private List<UserSnowflake> enabledUsers = new ArrayList<>();

    /**
     * The roles that can not use this command
     */
    @Disabled(since = "2.0")
    private List<Role> disabledRoles = new ArrayList<>();

    /**
     * The users that can not use this command
     */
    @Disabled(since = "2.0")
    private List<User> disabledUsers = new ArrayList<>();

    /**
     * Is the command enabled by default
     */
    private boolean defaultEnabled = true;

    /**
     * The subcommands
     */
    private List<SubcommandData> subCommands = new ArrayList<>();

    /**
     * The subcommand groups
     */
    private List<SubcommandGroupData> subcommandGroup = new ArrayList<>();

    /**
     * The options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    private List<OptionData> options = new ArrayList<>();

    /**
     * The category of the command
     */
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
    public SimpleSlashCommand(String command) {
        this.command = command;
    }

    /**
     * Set the description
     */
    public void description(String description) {
        this.description = description;
    }

    /**
     * Set this command as main guild only
     */
    public void mainGuildOnly() {
        this.guildOnly = true;
    }

    /**
     * Set whether the command can only be used inside a NSFW channel
     */
    @Disabled(since = "2.0")
    public void nsfwOnly() {
        this.nsfwOnly = true;
    }

    /**
     * Set the list of roles that can use this command
     */
    @Disabled(since = "2.0")
    public void enabledRoles(List<Role> roles) {
        this.enabledRoles = roles;
    }

    /**
     * Set the list of roles that can use this command
     */
    @Disabled(since = "2.0")
    public void enabledRoles(Role... roles) {
        this.enabledRoles = Arrays.asList(roles);
    }

    /**
     * Set the list of users that can use this command
     */
    @Disabled(since = "2.0")
    public void enabledUsers(List<UserSnowflake> users) {
        this.enabledUsers = users;
    }

    /**
     * Set the list users that can use this command
     */
    @Disabled(since = "2.0")
    public void enabledUsers(UserSnowflake... users) {
        this.enabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of roles that can not use the command
     */
    @Disabled(since = "2.0")
    public void disabledRoles(Role... roles) {
        this.disabledRoles = Arrays.asList(roles);
    }

    /**
     * Set the list of roles that can not use the command
     */
    @Disabled(since = "2.0")
    public void disabledRoles(List<Role> roles) {
        this.disabledRoles = roles;
    }

    /**
     * Set the list of users that can not use the command
     */
    @Disabled(since = "2.0")
    public void disabledUsers(User... users) {
        this.disabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of users that can not use the command
     */
    @Disabled(since = "2.0")
    public void disabledUsers(List<User> users) {
        this.disabledUsers = users;
    }

    /**
     * Set whether the command is enabled by default
     */
    public void defaultEnabled() {
        this.defaultEnabled = true;
    }

    /**
     * Set whether the command is enabled by default
     */
    public void defaultEnabled(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    /**
     * Set the subcommands
     */
    public void subCommands(List<SubcommandData> subCommands) {
        this.subCommands = subCommands;
    }

    /**
     * Set the subcommands
     */
    public void subCommands(SubcommandData... subCommands) {
        this.subCommands = Arrays.asList(subCommands);
    }

    /**
     * Set the subcommand group
     */
    public void subcommandGroup(List<SubcommandGroupData> subcommandGroupDataList) {
        this.subcommandGroup = subcommandGroupDataList;
    }

    /**
     * Set the subcommand group
     */
    public void subcommandGroup(SubcommandGroupData... subcommandGroupDataList) {
        this.subcommandGroup = Arrays.asList(subcommandGroupDataList);
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void options(List<OptionData> optionDataList) {
        this.options = optionDataList;
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void options(OptionData... optionDataList) {
        this.options = Arrays.asList(optionDataList);
    }

    /**
     * Set the category of the command
     */
    @Disabled(since = "2.0")
    public void category(String category) {
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

    /**
     * Return the description of the command
     *
     * @return the description
     */
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
    @Disabled(since = "2.0")
    public boolean getNsfwOnly() {
        return nsfwOnly;
    }

    /**
     * Returns a list of the roles that can use this command
     *
     * @return the allowed roles
     */
    @Disabled(since = "2.0")
    public List<Role> getEnabledRoles() {
        return enabledRoles;
    }

    /**
     * Returns a list of users that can use this command
     *
     * @return the allowed users
     */
    @Disabled(since = "2.0")
    public List<UserSnowflake> getEnabledUsers() {
        return enabledUsers;
    }

    /**
     * Returns a list of roles that can not use the command
     *
     * @return the disallowed roles
     */
    @Disabled(since = "2.0")
    public List<Role> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * Returns a list of users that can not use the command
     *
     * @return the disallowed users
     */
    @Disabled(since = "2.0")
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

    /**
     * Get the category of the command
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }
}
