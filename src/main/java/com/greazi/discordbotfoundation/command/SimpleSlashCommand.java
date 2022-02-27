package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleSlashCommand {

    public SimpleSlashCommand(){
        SimpleBot.getSlashCommandHandler().addCommand(this);
    }

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * Set the command like "/command"
     */
    protected String command = "example";

    /**
     * Set the help description fo the slash command
     */
    protected String description = null;

    /**
     * Is the command bound to only the main guild of the bot
     */
    protected boolean guildOnly = false;

    /**
     * Is the command bound to a NSFW channel
     * (This means if it can only be used in a NSFW channel)
     */
    protected boolean nsfwOnly = false;

    /**
     * Set the roles that can use this command
     */
    protected List<String> enabledRoles = new ArrayList<>();

    /**
     * Set the users that can use this command
     */
    protected List<String> enabledUsers = new ArrayList<>();

    /**
     * Set the roles that can not use this command
     */
    protected List<String> disabledRoles = new ArrayList<>();

    /**
     * Set the users that can not use this command
     */
    protected List<String> disabledUsers = new ArrayList<>();

    /**
     * Is the command enabled by default
     */
    protected boolean defaultEnabled = true;

    /**
     * Set the subcommands
     */
    protected List<SubcommandData> subCommands = new ArrayList<>();

    /**
     * Set the subcommand groups
     */
    protected List<SubcommandGroupData> subcommandGroup = new ArrayList<>();

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
    public List<String> getEnabledRoles() {
        return enabledRoles;
    }

    /**
     * Returns a list of users that can use this command
     *
     * @return the allowed users
     */
    public List<String> getEnabledUsers() {
        return enabledUsers;
    }

    /**
     * Returns a list of roles that can not use the command
     *
     * @return the disallowed roles
     */
    public List<String> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * Returns a list of users that can not use the command
     *
     * @return the disallowed users
     */
    public List<String> getDisabledUsers() {
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
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public List<OptionData> getOptions() {
        return options;
    }

}
