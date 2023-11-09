package com.greazi.old.command;

import com.greazi.old.SimpleBot;
import com.greazi.old.Valid;
import com.greazi.old.debug.Debugger;
import com.greazi.old.utils.SimpleEmbedBuilder;
import com.greazi.old.utils.expiringmap.ExpiringMap;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;

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
public abstract class SimpleCommand extends ListenerAdapter {

    /**
     * The command name, eg. /about or /ping
     */
    private final String command;

    /**
     * The command description, eg. "Get some info about the bot"
     */
    private String description;


    /**
     * The permissions that are required to run this command
     */
    private List<Permission> permissionList = new ArrayList<>();

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
     * The mainGuild is set inside {@link SimpleBot#setMainGuild(JDA)}
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

    // ----------------------------------------------------------------------------------------
    // Temporary variables
    // ----------------------------------------------------------------------------------------

    protected SlashCommandInteractionEvent event;

    /**
     * The User that ran the command
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known user
     */
    protected User user;

    /**
     * The Member that ran the command
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known member
     */
    protected Member member;

    /**
     * The Guild where the command was run
     * <p>
     * This variable is set dynamically when the command is run with the
     * last known guild
     */
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

    /**
     * The main method to create the command
     */
    protected SimpleCommand(final String command) {
        this.command = command;
        //this.description = description;
    }

    // ----------------------------------------------------------------------
    // Registration
    // ----------------------------------------------------------------------

    public final CommandData getCommandData() {
        Debugger.debug("SimpleCommand", "Registering command: " + this.getCommand() + " with description: " + this.getDescription());
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

    public final void execute(final SlashCommandInteractionEvent event) {
        // Setting the temporary variables
        this.guild = event.getGuild();

        if (this.guild == null || this.guild != SimpleBot.getMainGuild()) {
            replyErrorEmbed("Command denied", "This command can only be used in the main guild");
            return;
        }

        this.user = event.getUser();
        this.member = event.getMember();

        // Making sure that the channel is a text channel
        this.channel = event.getChannel() instanceof TextChannel ? event.getChannel().asTextChannel() : null;
        // Making sure that the channel is a thread channel
        this.threadChannel = event.getChannel() instanceof ThreadChannel ? event.getChannel().asThreadChannel() : null;

        this.event = event;

        // Runs the command
        this.onCommand();
    }

    /**
     * Executed when the command is run. You can get the variable sender and args directly,
     * and use convenience checks in the simple command class.
     */
    protected abstract void onCommand();

    // ----------------------------------------------------------------------
    // Temporary variables and safety
    // ----------------------------------------------------------------------

    public String getCommand() {
        return this.command;
    }

    public String getDescription() {
        return this.description;
    }

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

    public User getUser() {
        return this.user;
    }

    public Member getMember() {
        return this.member;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public SlashCommandInteractionEvent getEvent() {
        return this.event;
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
