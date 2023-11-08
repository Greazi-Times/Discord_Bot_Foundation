/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.annotations.Disabled;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command creation system
 */
public abstract class SimpleSlashCommand extends ListenerAdapter {

    /**
     * The command name, eg. /about or /ping
     */
    private final String command
    
    /**
	 * You can set the cooldown time before executing the command again. This map
	 * stores the player uuid and his last execution of the command.
	 */
    private final ExpiringMap<User, Long> cooldownMap = ExpiringMap.builder().expiration(30, TimeUnit.MINUTES).build();

    /**
     * The command cooldown before we can run this command again
     */
    @Getter
    private int cooldownSeconds = 0;
    
    /**
     * The users that are allowd to bypass this command's cooldown, if it is set
     */
    private List<User> cooldownBypassUsers = null
    
    /**
     * The permissions that are allowd to bypass this command's cooldown, if it is set
     */
    private List<Permission> cooldownBypassPermission = null
    
    /**
	 * A custom message when the user attempts to run this command
	 * within {@link #cooldownSeconds}. By default we use the line below
	 * <p>
	 * TIP: Use {duration} to replace the remaining time till next run
	 */
    private String cooldownMessage = "The command you used is in cooldow you can use this command after {duration}";
    
    /**
     * Send the cooldownMessage in a embed or as a normal message
     */
    private boolean cooldownMessageAsEmbed = true
    
    /**
     * If the command can only be ran in the main guild of the bot
     * <p>
     * The mainGuild is set inside {@link SimpleBot#setMainGuild}
     */
    private boolean mainGuildOnly = false;
    
    /**
     * Users that can't use this command
     */
    private List<User> disabledUsers = null
    
    /**
     * Roles that can't use this command
     */
    private List<Role> disabledRoles = null;
    
    /**
     * Channels that aren't allowed to use this command
     */
    private List<Channel> disabledChannels = null
    
    /**
     * Users that are allowed use this command
     */
    private List<User> allowedUsers = null;
    
    /**
     * Roles that are allowed use this command
     */
    private List<Role> allowedRoles = null;
    
    /**
     * Channels where this command is allowed
     */
    private List<Channel> allowedChannels = null;
    
    // ----------------------------------------------------------------------------------------
    // Temporary variables
    // ----------------------------------------------------------------------------------------
    
    /**
     * The command User
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known user
     */
    protected User user;
    
    /**
     * The command Member
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known member
     */
    protected Member member;
    
    /**
     * The command Guild
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known guild
     */
    protected Guilg guild;
    
    // ----------------------------------------------------------------------------------------    
    
    /**
     * The main method to create the command
     */
    protected SimpleSlashCommand(final String command) {
        super(command)
        
        this.command = command;
        
    }
 
    // ----------------------------------------------------------------------------------------
    // Registration
    // ----------------------------------------------------------------------------------------
    // TODO: Register command

    // ----------------------------------------------------------------------------------------
    // Execution
    // ----------------------------------------------------------------------------------------
    
    // TODO: Check if its possible to extend SlashCommandInteractionEvent
    @Override
    public final boolean execute(final SlashCommandInteractionEvent event) {
        this.user = event.getuser();
        this.member = event.getMember();
        this.guild = event.getGuild();
        
    }
    
    
    // ----------------------------------------------------------------------------------------
    // OLD SYSTEM
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
    
    private List<Permission> permissions = new ArrayList<>();

    private List<User> allowedUsers = new ArrayList<>();

    private List<User> blockedUsers = new ArrayList<>();

    /**
     * The category of the command
     */
    private String category = null;

    private Member member = null;

    private User user = null;

    private Guild guild = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    public final boolean execute(final SlashCommandInteractionEvent event) {
        this.member = event.getMember();
        this.user = event.getUser();
        this.guild = event.getGuild();

        if (this.guildOnly && !event.isFromGuild()) {
            event.replyEmbeds(
                    new SimpleEmbedBuilder("Main Guild Only", false)
                            .text("This command can only be used in the main guild of the bot")
                            .build()
            ).setEphemeral(true).queue();
            return false;
        }

        boolean canUse = true;

        if (!this.allowedUsers.isEmpty() && !this.allowedUsers.contains(event.getUser())) canUse = false;
        if (!this.blockedUsers.isEmpty() && this.blockedUsers.contains(event.getUser())) canUse = false;

        if (canUse) {
            this.onCommand(event);
            return true;
        } else {
            event.replyEmbeds(
                    new SimpleEmbedBuilder("Missing Permissions", false)
                            .text("You are not allowed to execute this command")
                            .build()
            ).setEphemeral(true).queue();
            return false;
        }
    }

    /**
     * Run the command logic
     *
     * @param event SlashCommandInteractionEvent
     */
    protected abstract void onCommand(SlashCommandInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the command
     */
    public SimpleSlashCommand(final String command) {
        this.command = command;
    }

    /**
     * Set the description
     */
    public void description(final String description) {
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
    public void subCommands(final List<SubcommandData> subCommands) {
        this.subCommands = subCommands;
    }

    /**
     * Set the subcommands
     */
    public void subCommands(final SubcommandData... subCommands) {
        this.subCommands = Arrays.asList(subCommands);
    }

    /**
     * Set the subcommand group
     */
    public void subcommandGroup(final List<SubcommandGroupData> subcommandGroupDataList) {
        this.subcommandGroup = subcommandGroupDataList;
    }

    /**
     * Set the subcommand group
     */
    public void subcommandGroup(final SubcommandGroupData... subcommandGroupDataList) {
        this.subcommandGroup = Arrays.asList(subcommandGroupDataList);
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void options(final List<OptionData> optionDataList) {
        this.options = optionDataList;
    }

    /**
     * Set the options of the command
     * !! This can not be used alongside subCommand or subCommandGroup !!
     */
    public void options(final OptionData... optionDataList) {
        this.options = Arrays.asList(optionDataList);
    }

    public void permissions(final List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void permissions(final Permission... permissions) {
        this.permissions = Arrays.asList(permissions);
    }

    /**
     * Set the category of the command
     */
    @Disabled(since = "2.0")
    public void category(final String category) {
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
    
    public List<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Get the category of the command
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get the member who executed the command
     *
     * @return the member
     */
    protected final Member getMember() {
        return this.member;
    }

    /**
     * Get the user who executed the command
     *
     * @return the user
     */
    protected final User getUser() {
        return this.user;
    }

    /**
     * Get the guild where the command was executed
     *
     * @return the guild
     */
    protected final Guild getGuild() {
        return this.guild;
    }
}
