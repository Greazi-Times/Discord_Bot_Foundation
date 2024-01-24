package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.Valid;
import com.greazi.discordbotfoundation.utils.expiringmap.ExpiringMap;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple slash command class that can be used to create slash commands
 * <p>
 * Create a command by extending this class and overriding the onCommand method
 * to register the command use {@link #getCommandData()}
 * <p>
 * /{command} {sub command group} {sub command} {options...}
 * <p>
 * Example: /about or /info user Greazi
 */
public abstract class SimpleCommand extends SlashCommandHandler {

    /**
     * The command name, eg. /about or /ping
     */
    @Getter
    private final String command;

    /**
     * The command description, eg. "Get some info about the bot"
     */
    @Getter
    private String description;

    /**
     * The permissions that are required to run this command
     */
    private final List<Permission> permissionList = new ArrayList<>();

    /**
     * A list containing all subcommand groups
     */
    private final List<SubcommandGroupData> subcommandGroupDataList = new ArrayList<>();

    /**
     * A list containing all the subcommands
     */
    private final List<SubcommandData> subcommandDataList = new ArrayList<>();

    /**
     * A list containing all the options
     */
    private final List<OptionData> optionDataList = new ArrayList<>();

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
    private List<User> cooldownBypassUsers = null;

    /**
     * The permissions that are allowd to bypass this command's cooldown, if it is set
     */
    private List<Permission> cooldownBypassPermission = null;

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
    private boolean cooldownMessageAsEmbed = true;

    /**
     * If the command can only be run in the main guild of the bot
     * <p>
     * The mainGuild is set inside {@link SimpleBot#setMainGuild()}
     */
    private boolean mainGuildOnly = false;

    /**
     * Users that can't use this command
     */
    private List<User> disabledUsers = null;

    /**
     * Roles that can't use this command
     */
    private List<Role> disabledRoles = null;

    /**
     * Channels that aren't allowed to use this command
     */
    private List<Channel> disabledChannels = null;

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

    /**
     * A list of all commands that need to be registered
     */
    public static final List<SimpleCommand> COMMANDS = new ArrayList<>();

    // ----------------------------------------------------------------------------------------
    // Temporary variables
    // ----------------------------------------------------------------------------------------

    @Getter
    protected SlashCommandInteractionEvent event;

    /**
     * The User that ran the command
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known user
     */
    @Getter
    protected User user;

    /**
     * The Member that ran the command
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known member
     */
    @Getter
    protected Member member;

    /**
     * The Guild where the command was run
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known guild
     */
    @Getter
    protected Guild guild;

    /**
     * The Channel where the command was run, only works if the channel is a TextChannel,
     * otherwise it will be null
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known channel
     */
    protected TextChannel channel;

    /**
     * The Channel where the command was run, only works if the channel is a ThreadChannel,
     * otherwise it will be null
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known channel
     */
    protected ThreadChannel threadChannel;

    // ----------------------------------------------------------------------------------------

    public SimpleCommand() {
        this.command = null;
        Common.log("Starting up main command listener");
        SimpleBot.getJda().addEventListener(this);
    }

    /**
     * The main method to create the command
     */
    protected SimpleCommand(final String command) {
        this.command = command;
    }

    // ----------------------------------------------------------------------
    // Registration
    // ----------------------------------------------------------------------

    public static void register(final SimpleCommand command) {
        Debugger.debug("SimpleCommand", "Registering command: " + command.getCommand() + " with description: " + command.getDescription());
        COMMANDS.add(command);
        SimpleBot.getJda().addEventListener(command);
    }

    public static void registerAll() {
        Debugger.debug("SimpleCommand", "Registering all commands");
        // Register all commands
        SimpleBot.getJda().updateCommands().addCommands(COMMANDS.stream().map(SimpleCommand::getCommandData).toList()).queue();
    }

    public final CommandData getCommandData() {
        Debugger.debug("SimpleCommand", "Creating command data for command: " + this.getCommand() + " with description: " + this.getDescription());
        final SlashCommandData commandData = Commands.slash(this.getCommand(), this.getDescription());

        // add sub command groups
        for (final SubcommandGroupData subcommandGroup : getSubcommandGroup()) {
            commandData.addSubcommandGroups(subcommandGroup);
        }

        // add sub commands
        for (final SubcommandData subcommand : getSubcommandGroupDataList()) {
            commandData.addSubcommands(subcommand);
        }

        if (getSubcommandGroupDataList().isEmpty() && getSubcommandGroup().isEmpty()) {
            commandData.addOptions(getOptions());
        }

        final DefaultMemberPermissions defaultMemberPermissions = DefaultMemberPermissions.enabledFor(getPermissions());
        commandData.setDefaultPermissions(defaultMemberPermissions);

        return commandData;
    }

    // ----------------------------------------------------------------------------------------
    // Execution
    // ----------------------------------------------------------------------------------------

    @Override
    protected void execute(@NotNull final SlashCommandInteractionEvent event) {
        Common.log("SimpleCommand", "Executing command: " + this.getCommand() + " User: " + event.getUser().getName());
        // Set the event variable
        this.event = event;

        // Log the command
        Debugger.debug("SimpleCommand", "Executing command: " + this.getCommand() + " User: " + this.event.getUser().getName());

        // Check if the command is the same as the command name
        if (!this.event.getCommandString().equalsIgnoreCase(this.getCommand())) {
            // Return because the command is not the same
            return;
        }

        // Set the guild where to command is used
        this.guild = this.event.getGuild();

        // Check if the guild is set and if its set to mainguild only
        if (this.guild == null || this.guild != SimpleBot.getMainGuild()) {
            // Return an error message
            replyErrorEmbed("Command denied", "This command can only be used in the main guild");
            return;
        }

        // Set the user and member
        this.user = this.event.getUser();
        this.member = this.event.getMember();

        // Making sure that the channel is a text channel set to null if it's a thread channel
        this.channel = this.event.getChannel() instanceof TextChannel ? this.event.getChannel().asTextChannel() : null;
        // Making sure that the channel is a thread channel set to null if it's a text channel
        this.threadChannel = this.event.getChannel() instanceof ThreadChannel ? this.event.getChannel().asThreadChannel() : null;

        // Check if the user can use the command
        boolean canExecute = true;

        // Check if the user is in the disabled users list
        if (this.disabledUsers != null && this.disabledUsers.contains(this.user)) {
            canExecute = false;
        }

        // Check if the user is in the disabled roles list
        if (this.disabledRoles != null && this.member != null) {
            for (final Role role : this.disabledRoles) {
                if (this.member.getRoles().contains(role)) {
                    canExecute = false;
                    break;
                }
            }
        }

        // Check if the channel is in the disabled channels list
        if (this.disabledChannels != null && this.channel != null) {
            if (this.disabledChannels.contains(this.channel) || this.disabledChannels.contains(this.threadChannel)) {
                canExecute = false;
            }
        }

        // Check if the user is in the allowed users list
        if (this.allowedUsers != null && !this.allowedUsers.contains(this.user)) {
            canExecute = false;
        }

        // Check if the user is in the allowed roles list
        if (this.allowedRoles != null && this.member != null) {
            for (final Role role : this.allowedRoles) {
                if (this.member.getRoles().contains(role)) {
                    canExecute = true;
                    break;
                }
            }
        }

        // Check if the channel is in the allowed channels list
        if (this.allowedChannels != null && this.channel != null) {
            if (!this.allowedChannels.contains(this.channel) || !this.allowedChannels.contains(this.threadChannel)) {
                canExecute = false;
            }
        }

        // Check if the user has the permission to bypass the cooldown
        if (this.cooldownBypassPermission != null && this.member != null) {
            for (final Permission permission : this.cooldownBypassPermission) {
                if (this.member.hasPermission(permission)) {
                    canExecute = true;
                    break;
                }
            }
        }

        // Check if the user is in the cooldown bypass list
        if (this.cooldownBypassUsers != null && this.cooldownBypassUsers.contains(this.user)) {
            canExecute = true;
        }

        // Check if the user is in the cooldown map
        if (this.cooldownMap.containsKey(this.user)) {
            // Get the last execution time
            final long lastExecution = this.cooldownMap.get(this.user);

            // Get the current time
            final long currentTime = System.currentTimeMillis();

            // Get the remaining time
            final long remainingTime = (lastExecution + (this.cooldownSeconds * 1000)) - currentTime;

            // Check if the remaining time is greater than 0
            if (remainingTime > 0) {
                // Check if we should send the cooldown message
                if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) {
                    // Replace the duration placeholder
                    final String message = this.cooldownMessage.replace("{duration}", String.valueOf(remainingTime / 1000));

                    // Check if we should send the message as an embed
                    if (this.cooldownMessageAsEmbed) {
                        // Send the message as an embed
                        this.replyErrorEmbed("Command denied", message);
                    } else {
                        // Send the message as a normal message
                        this.reply(message);
                    }
                }

                replyErrorEmbed("Command denied", "This command is in cooldown you can use this command after " + remainingTime / 1000 + " seconds");

                // Return because the user is in cooldown
                return;
            }
        }

        // Check if the user can execute the command
        if (!canExecute) {
            // Return an error message
            replyErrorEmbed("Command denied", "You don't have permission to use this command");
            return;
        }

        // Check if the command is set to main guild only
        if (this.isMainGuildOnly()) {
            // Check if the guild is set
            if (this.guild == null) {
                // Return an error message
                replyErrorEmbed("Command denied", "This command can only be used in the main guild");
                return;
            }

            // Check if the guild is the main guild
            if (this.guild != SimpleBot.getMainGuild()) {
                // Return an error message
                replyErrorEmbed("Command denied", "This command can only be used in the main guild");
                return;
            }
        }

        // Check if the command is in cooldown
        if (this.cooldownSeconds > 0) {
            // Add the user to the cooldown map
            this.cooldownMap.put(this.user, System.currentTimeMillis());
        }

        // Runs the command
        onCommand();
    }

    /**
     * Executed when the command is run. You can get the variable sender and args directly,
     * and use convenience checks in the simple command class.
     */
    protected abstract void onCommand();

    // ----------------------------------------------------------------------
    // Temporary variables and safety
    // ----------------------------------------------------------------------

    public void setDescription(final String description) {
        this.description = description;
    }

    public void addPermission(final Permission permission) {
        this.permissionList.add(permission);
    }

    public void addPermission(final Permission... permissions) {
        this.permissionList.addAll(Arrays.asList(permissions));
    }

    public List<Permission> getPermissions() {
        return this.permissionList;
    }

    public void addSubcommandGroup(final SubcommandGroupData subcommandGroup) {
        this.subcommandGroupDataList.add(subcommandGroup);
    }

    public void addSubCommandGroup(final SubcommandGroupData... subcommandGroups) {
        this.subcommandGroupDataList.addAll(Arrays.asList(subcommandGroups));
    }

    public void addSubcommand(final SubcommandData subcommand) {
        this.subcommandDataList.add(subcommand);
    }

    public void addSubCommand(final SubcommandData... subcommands) {
        this.subcommandDataList.addAll(Arrays.asList(subcommands));
    }

    public void addOption(final OptionData option) {
        this.optionDataList.add(option);
    }

    public void addOption(final OptionData... options) {
        this.optionDataList.addAll(Arrays.asList(options));
    }

    public List<SubcommandGroupData> getSubcommandGroup() {
        return this.subcommandGroupDataList;
    }

    public List<SubcommandData> getSubcommandGroupDataList() {
        return this.subcommandDataList;
    }

    public List<OptionData> getOptions() {
        return this.optionDataList;
    }

    public GuildMessageChannel getChannel() {
        if (this.channel == null && this.threadChannel != null) {
            return this.threadChannel;
        } else {
            return this.channel;
        }
    }

    /**
     * Set the time before the same user can execute this command again
     *
     * @param cooldown
     * @param unit
     */
    protected final void setCooldown(final int cooldown, final TimeUnit unit) {
        Valid.checkBoolean(cooldown >= 0, "Cooldown must be >= 0 for /" + this.getCommand());

        this.cooldownSeconds = (int) unit.toSeconds(cooldown);
    }

    protected final void setCooldownMessage(final String cooldownMessage) {
        this.cooldownMessage = cooldownMessage;
    }

    protected final void sendCooldownMessageAsEmbed(final boolean cooldownMessageAsEmbed) {
        this.cooldownMessageAsEmbed = cooldownMessageAsEmbed;
    }

    protected final void setCooldownBypassUsers(final List<User> cooldownBypassUsers) {
        this.cooldownBypassUsers = cooldownBypassUsers;
    }

    protected final void setCooldownBypassPermission(final List<Permission> cooldownBypassPermission) {
        this.cooldownBypassPermission = cooldownBypassPermission;
    }

    protected final void setMainGuildOnly(final boolean mainGuildOnly) {
        this.mainGuildOnly = mainGuildOnly;
    }

    protected final boolean isMainGuildOnly() {
        // Make sure that the bot has and is in the main guild
        if (SimpleBot.getMainGuild() == null)
            return false;

        return this.mainGuildOnly;
    }

    protected final void setDisabledUsers(final List<User> disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    protected final List<User> getDisabledUsers() {
        return this.disabledUsers;
    }

    protected final void setDisabledRoles(final List<Role> disabledRoles) {
        this.disabledRoles = disabledRoles;
    }

    protected final List<Role> getDisabledRoles() {
        return this.disabledRoles;
    }

    protected final void setDisabledChannels(final List<Channel> disabledChannels) {
        this.disabledChannels = disabledChannels;
    }

    protected final List<Channel> getDisabledChannels() {
        return this.disabledChannels;
    }

    protected final void setAllowedUsers(final List<User> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    protected final List<User> getAllowedUsers() {
        return this.allowedUsers;
    }

    protected final void setAllowedRoles(final List<Role> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    protected final List<Role> getAllowedRoles() {
        return this.allowedRoles;
    }

    protected final void setAllowedChannels(final List<Channel> allowedChannels) {
        this.allowedChannels = allowedChannels;
    }

    protected final List<Channel> getAllowedChannels() {
        return this.allowedChannels;
    }

    // ----------------------------------------------------------------------
    // Messaging
    // ----------------------------------------------------------------------

    /**
     * Sends reply message
     *
     * @param message String
     */
    protected final void reply(final String message) {
        this.reply(false, message);
    }

    /**
     * Sends a private reply message
     *
     * @param message String
     */
    protected final void replyPrivate(final String message) {
        this.reply(true, message);
    }

    /**
     * Sends a multiline reply message
     *
     * @param messages String...
     */
    protected final void reply(final boolean ephemeral, String... messages) {

        if (messages == null)
            return;

        try {
            messages = this.replacePlaceholders(messages);

            getEvent().reply(String.join("\n", messages)).setEphemeral(ephemeral).queue();

        } finally {
            getChannel().sendMessage(String.join("\n", messages)).queue();
        }
    }

    // ----------------------------------------------------------------------

    /**
     * Reply with an success embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replySuccessEmbed(final String title, final String... messages) {
        replyEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)).success());
    }

    /**
     * Reply with an error embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replyErrorEmbed(final String title, final String... messages) {
        replyEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)).error());
    }

    /**
     * Reply with an embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replyEmbed(final String title, final String... messages) {
        replyEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)));
    }

    /**
     * Reply with an embed message
     *
     * @param embed SimpleEmbedBuilder
     */
    protected final void replyEmbed(final SimpleEmbedBuilder embed) {
        replyEmbed(embed, false);
    }

    // ----------------------------------------------------------------------

    /**
     * Reply with a private success embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replyPrivateSuccessEmbed(final String title, final String... messages) {
        replyPrivateEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)).success());
    }

    /**
     * Reply with an private error embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replyPrivateErrorEmbed(final String title, final String... messages) {
        replyPrivateEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)).error());
    }

    /**
     * Reply with an private embed message
     *
     * @param title    String
     * @param messages String...
     */
    protected final void replyPrivateEmbed(final String title, final String... messages) {
        replyPrivateEmbed(new SimpleEmbedBuilder(this.replacePlaceholders(title)).text(this.replacePlaceholders(messages)));
    }

    /**
     * Reply with an private embed message
     *
     * @param embed SimpleEmbedBuilder
     */
    protected final void replyPrivateEmbed(final SimpleEmbedBuilder embed) {
        replyEmbed(embed, true);
    }

    /**
     * Reply with an embed message
     *
     * @param embed     SimpleEmbedBuilder
     * @param ephemeral boolean
     */
    protected final void replyEmbed(final SimpleEmbedBuilder embed, final boolean ephemeral) {

        if (embed == null)
            return;

        try {
            getEvent().replyEmbeds(embed.build()).setEphemeral(ephemeral).queue();
        } finally {
            getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    // ----------------------------------------------------------------------
    // Placeholder
    // ----------------------------------------------------------------------

    /**
     * Replaces placeholders in all messages
     * To change them override {@link #replacePlaceholders(String)}
     *
     * @param messages
     * @return
     */
    protected final String[] replacePlaceholders(final String[] messages) {
        for (int i = 0; i < messages.length; i++)
            messages[i] = this.replacePlaceholders(messages[i]);

        return messages;
    }

    /**
     * Replaces placeholders in the message
     *
     * @param message
     * @return
     */
    protected String replacePlaceholders(String message) {
        // Replace basic labels
        message = this.replaceBasicPlaceholders0(message);

        /*// Replace {X} with arguments
        for (int i = 0; i < this.args.length; i++)
            message = message.replace("{" + i + "}", Common.getOrEmpty(this.args[i]));*/

        return message;
    }

    /**
     * Internal method for replacing {label} and {sublabel}
     *
     * @param message
     * @return
     */
    private String replaceBasicPlaceholders0(String message) {

        // First, replace label and sublabel
        message = message
                .replace("{label}", this.getCommand());

        // Replace hard variables
        // TODO: Add hard variables here

        return message;
    }
}
