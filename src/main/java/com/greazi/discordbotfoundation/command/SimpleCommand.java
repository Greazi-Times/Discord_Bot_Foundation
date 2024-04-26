package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.command.checkers.SlashCommandChecker;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponse;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponseProvider;
import com.greazi.discordbotfoundation.command.checkers.response.DefaultCommandProvider;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.Valid;
import com.greazi.discordbotfoundation.utils.expiringmap.ExpiringMap;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple slash command class that can be used to create slash commands
 * <p>
 * <code>/{@link #command} {sub command group} {sub command} {options...}</code>
 * <p>
 * <h1>Execution</h1>
 * <p>
 *     You will have to define an <strong>execution</strong> method, that'll be called when the command is executed.
 *     The method <strong>must be</strong> called <code>onCommand</code> and <strong>must</strong> have the same parameters as the options you've registered (in the same order too!).
 *     <br>
 *     Here's the list of the commands option to Java type mapping:
 * </p>
 * <ul>
 *      <li>{@link OptionType#USER} => {@link User}</li>
 *      <li>{@link OptionType#CHANNEL} => {@link GuildChannelUnion}</li>
 *      <li>{@link OptionType#ROLE} => {@link Role}</li>
 *      <li>{@link OptionType#STRING} => {@link String}</li>
 *      <li>{@link OptionType#NUMBER} => {@link Double}</li>
 *      <li>{@link OptionType#INTEGER} => {@link Long}</li>
 *      <li>{@link OptionType#BOOLEAN} => {@link Boolean}</li>
 *      <li>{@link OptionType#MENTIONABLE} => {@link IMentionable}</li>
 *      <li>{@link OptionType#ATTACHMENT} => {@link Message.Attachment}</li>
 * </ul>
 * <p>
 *     For optional options, the passed argument will simply be <code>null</code>, so keep that in mind!
 * </p>
 */
public abstract class SimpleCommand {

    /**
     * The command name, eg. /about or /ping
     */
    @Getter
    private final String command;

    /**
     * The command description, eg. "Get some info about the bot"
     */
    @Getter
    @Setter
    private String description;

    /**
     * The command response provider that will be used in
     * slash command checkers.
     */
    @Getter
    @Setter
    private SlashCommandResponseProvider responseProvider = new DefaultCommandProvider();

    /**
     * List of {@link com.greazi.discordbotfoundation.command.checkers.SlashCommandChecker checkers} that will be used
     * to check if the command can be executed.
     */
    @Getter
    private final List<SlashCommandChecker> checkers = new ArrayList<>();

    /**
     * The permissions that are required to run this command
     */
    @Getter
    private final List<Permission> permissions = new ArrayList<>();

    /**
     * A list containing all the {@link SimpleCommand subcommands} of this command
     */
    @Getter
    private final List<SimpleCommand> subcommandsList = new ArrayList<>();

    /**
     * A list containing all the options
     */
    @Getter
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
     */
    @Setter
    private boolean mainGuildOnly = false;

    /**
     * Users that can't use this command
     */
    @Getter @Setter
    private List<User> disabledUsers = null;

    /**
     * Users that are allowed use this command
     */
    @Getter @Setter
    private List<User> allowedUsers = null;

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
    // Data Building
    // ----------------------------------------------------------------------

    /**
     * Builds the slash command data, as a "core" command
     * @return The built slash command
     */
    public final SlashCommandData buildSlashCommand() {
        final SlashCommandData commandData = Commands.slash(command, description);

        // Add options
        for (final OptionData option : optionDataList) {
            commandData.addOptions(option);
        }

        // Add subcommands
        for (final SimpleCommand subcommand : subcommandsList) {
            commandData.addSubcommands(subcommand.buildSubCommand());
        }

        // Set up the rest
        commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
        commandData.setGuildOnly(mainGuildOnly);

        return commandData;
    }

    /**
     * Builds the subcommand data, as a "sub" command
     * @return The built subcommand
     */
    public final SubcommandData buildSubCommand() {
        final SubcommandData subcommandData = new SubcommandData(command, description);

        // Add options
        for (final OptionData option : optionDataList) {
            subcommandData.addOptions(option);
        }

        return subcommandData;
    }

    /**
     * Check if this command is a "core" command or a subcommand
     * @return True if this command is a "core" command, false otherwise
     */
    public final boolean isCoreCommand() {
        return subcommandsList.isEmpty();
    }

    // ----------------------------------------------------------------------------------------
    // Execution
    // ----------------------------------------------------------------------------------------

    protected void execute(@NotNull final SlashCommandInteractionEvent event, final SimpleCommand simpleCommand) {
        Common.log("Received command: " + this.getCommand() + " User: " + event.getUser().getName());
        // First we run the checkers
        for (final SlashCommandChecker checker : checkers) {
            final SlashCommandResponse response = checker.check(this, event);
            if (!response.isSuccessful()) {
                response.respond(event);
                return;
            }
        }

        // Set the event variable
        simpleCommand.event = event;

        // Set the guild where to command is used
        simpleCommand.guild = simpleCommand.event.getGuild();

        // Check if the guild is set and if its set to mainguild only
        if (simpleCommand.guild == null || simpleCommand.guild != SimpleBot.getMainGuild()) {
            // Return an error message
            replyErrorEmbed("Command denied", "This command can only be used in the main guild");
            return;
        }

        // Set the user and member
        simpleCommand.user = simpleCommand.event.getUser();
        simpleCommand.member = simpleCommand.event.getMember();

        // Making sure that the channel is a text channel set to null if it's a thread channel
        simpleCommand.channel = simpleCommand.event.getChannel() instanceof TextChannel ? simpleCommand.event.getChannel().asTextChannel() : null;
        // Making sure that the channel is a thread channel set to null if it's a text channel
        simpleCommand.threadChannel = simpleCommand.event.getChannel() instanceof ThreadChannel ? simpleCommand.event.getChannel().asThreadChannel() : null;

        // Check if the user can use the command
        boolean canExecute = true;

        // Check if the user is in the disabled users list
        if (simpleCommand.disabledUsers != null && simpleCommand.disabledUsers.contains(simpleCommand.user)) {
            canExecute = false;
        }

        // Check if the user is in the allowed users list
        if (simpleCommand.allowedUsers != null && !simpleCommand.allowedUsers.contains(simpleCommand.user)) {
            canExecute = false;
        }

        // Check if the user has the permission to bypass the cooldown
        if (simpleCommand.cooldownBypassPermission != null && simpleCommand.member != null) {
            for (final Permission permission : this.cooldownBypassPermission) {
                if (simpleCommand.member.hasPermission(permission)) {
                    canExecute = true;
                    break;
                }
            }
        }

        // Check if the user is in the cooldown bypass list
        if (simpleCommand.cooldownBypassUsers != null && simpleCommand.cooldownBypassUsers.contains(simpleCommand.user)) {
            canExecute = true;
        }

        // Check if the user is in the cooldown map
        if (simpleCommand.cooldownMap.containsKey(simpleCommand.user)) {
            // Get the last execution time
            final long lastExecution = simpleCommand.cooldownMap.get(simpleCommand.user);

            // Get the current time
            final long currentTime = System.currentTimeMillis();

            // Get the remaining time
            final long remainingTime = (lastExecution + (simpleCommand.cooldownSeconds * 1000L)) - currentTime;

            // Check if the remaining time is greater than 0
            if (remainingTime > 0) {
                // Check if we should send the cooldown message
                if (simpleCommand.cooldownMessage != null && !simpleCommand.cooldownMessage.isEmpty()) {
                    // Replace the duration placeholder
                    final String message = simpleCommand.cooldownMessage.replace("{duration}", String.valueOf(remainingTime / 1000));

                    // Check if we should send the message as an embed
                    if (simpleCommand.cooldownMessageAsEmbed) {
                        // Send the message as an embed
                        simpleCommand.replyErrorEmbed("Command denied", message);
                    } else {
                        // Send the message as a normal message
                        simpleCommand.reply(message);
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
            if (simpleCommand.guild == null) {
                // Return an error message
                replyErrorEmbed("Command denied", "This command can only be used in the main guild");
                return;
            }

            // Check if the guild is the main guild
            if (simpleCommand.guild != SimpleBot.getMainGuild()) {
                // Return an error message
                replyErrorEmbed("Command denied", "This command can only be used in the main guild");
                return;
            }
        }

        // Check if the command is in cooldown
        if (simpleCommand.cooldownSeconds > 0) {
            // Add the user to the cooldown map
            simpleCommand.cooldownMap.put(simpleCommand.user, System.currentTimeMillis());
        }

        // Try to run the dynamic method, given the options registered:
        // First, get all options to store them in a list of pair, containing the (possible null) parsed option and its class type
        final List<Pair<Object, Class<?>>> parsedOptions = new ArrayList<>();
        for (final OptionData data : simpleCommand.getOptionDataList()) {
            final String name = data.getName();

            switch (data.getType()) {
                case USER -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsUser), User.class));
                case CHANNEL -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsChannel), GuildChannelUnion.class));
                case ROLE -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsRole), Role.class));
                case STRING -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsString), String.class));
                case NUMBER -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsDouble), Double.class));
                case INTEGER -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsLong), Long.class));
                case BOOLEAN -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsBoolean), Boolean.class));
                case MENTIONABLE -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsMentionable), IMentionable.class));
                case ATTACHMENT -> parsedOptions.add(Pair.of(event.getOption(name, null, OptionMapping::getAsAttachment), Message.Attachment.class));
            }
        }
        try {

            // Reflection's starting :)
            // Get the classes required, so we can get the signature of the method
            final List<Class<?>> classes = new ArrayList<>();
            for (final Pair<Object, Class<?>> parsedPair : parsedOptions)
                classes.add(parsedPair.getRight());

            final Method method = simpleCommand.getClass()
                    .getDeclaredMethod("onCommand", classes.toArray(new Class[0]));
            method.invoke(simpleCommand, parsedOptions.stream().map(Pair::getLeft).toArray());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            Common.throwError(exception, "The command " + this.getCommand() + " does not have an onCommand method with the required parameters!");
        }
    }

    // ----------------------------------------------------------------------
    // Temporary variables and safety
    // ----------------------------------------------------------------------

    public GuildMessageChannel getChannel() {
        if (this.channel == null && this.threadChannel != null) {
            return this.threadChannel;
        } else {
            return this.channel;
        }
    }

    /**
     * Adds a new option data to the command
     * @param optionData The option data to add
     */
    public void addOption(final OptionData optionData) {
        this.optionDataList.add(optionData);
    }

    /**
     * Adds a new sub command to the command
     * @param subcommand The sub command to add
     */
    public void addSubcommand(final SimpleCommand subcommand) {
        this.subcommandsList.add(subcommand);
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

    protected final boolean isMainGuildOnly() {
        // Make sure that the bot has and is in the main guild
        if (SimpleBot.getMainGuild() == null)
            return false;

        return this.mainGuildOnly;
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

        } catch (Exception e) {
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
