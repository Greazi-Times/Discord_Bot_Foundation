/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import com.google.gson.annotations.Since;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.utils.annotations.Disabled;
import net.dv8tion.jda.api.entities.*;
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
