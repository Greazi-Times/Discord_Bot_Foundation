package com.greazi.discordbotfoundation.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.greazi.discordbotfoundation.commands.impl.AnnotatedModuleCompilerImpl;
import com.greazi.discordbotfoundation.commands.impl.CommandClientImpl;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A simple builder used to create a {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}.
 *
 * <p>Once built, add the {@link com.greazi.discordbotfoundation.commands.CommandClient CommandClient} as an EventListener to
 * {@link net.dv8tion.jda.api.JDA JDA} and it will automatically handle commands with ease!
 *
 */
public class CommandClientBuilder
{
    private Activity activity = Activity.playing("default");
    private OnlineStatus status = OnlineStatus.ONLINE;
    private String ownerId;
    private String[] coOwnerIds;
    private String prefix;
    private String altprefix;
    private String[] prefixes;
    private Function<MessageReceivedEvent, String> prefixFunction;
    private Function<MessageReceivedEvent, Boolean> commandPreProcessFunction;
    private BiFunction<MessageReceivedEvent, SimpleCommand, Boolean> commandPreProcessBiFunction;
    private String serverInvite;
    private String success;
    private String warning;
    private String error;
    private String carbonKey;
    private String botsKey;
    private final LinkedList<SimpleCommand> simpleCommands = new LinkedList<>();
    private final LinkedList<SimpleSlashCommand> slashCommands = new LinkedList<>();
    private String forcedGuildId = null;
    private boolean manualUpsert = false;
    private CommandListener listener;
    private boolean useHelp = true;
    private boolean shutdownAutomatically = true;
    private Consumer<CommandEvent> helpConsumer;
    private String helpWord;
    private ScheduledExecutorService executor;
    private int linkedCacheSize = 0;
    private AnnotatedModuleCompiler compiler = new AnnotatedModuleCompilerImpl();
    private GuildSettingsManager manager = null;

    /**
     * Builds a {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}
     * with the provided settings.
     * <br>Once built, only the {@link com.greazi.discordbotfoundation.commands.CommandListener CommandListener},
     * and {@link SimpleCommand Command}s can be changed.
     *
     * @return The CommandClient built.
     */
    public CommandClient build()
    {
        CommandClient client = new CommandClientImpl(prefix, altprefix, prefixes, prefixFunction, commandPreProcessFunction, commandPreProcessBiFunction, activity, status, serverInvite,
                                                     success, warning, error, carbonKey, botsKey, new ArrayList<>(simpleCommands), new ArrayList<>(slashCommands), forcedGuildId, manualUpsert, useHelp,
                                                     shutdownAutomatically, helpConsumer, helpWord, executor, linkedCacheSize, compiler, manager);
        if(listener!=null)
            client.setListener(listener);
        return client;
    }

    /**
     * Sets the owner for the bot.
     * <br>Make sure to verify that the ID provided is ISnowflake compatible when setting this.
     * If it is not, this will warn the developer.
     *
     * @param  ownerId
     *         The ID of the owner.
     *
     * @return This builder
     */
    public CommandClientBuilder setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
        return this;
    }

    /**
     * Sets the owner for the bot.
     * <br>Make sure to verify that the ID provided is ISnowflake compatible when setting this.
     * If it is not, this will warn the developer.
     *
     * @param  ownerId
     *         The ID of the owner.
     *
     * @return This builder
     */
    public CommandClientBuilder setOwnerId(long ownerId)
    {
        this.ownerId = String.valueOf(ownerId);
        return this;
    }

    /**
     * Sets the one or more CoOwners of the bot.
     * <br>Make sure to verify that all of the IDs provided are ISnowflake compatible when setting this.
     * If it is not, this will warn the developer which ones are not.
     *
     * @param  coOwnerIds
     *         The ID(s) of the CoOwners
     *
     * @return This builder
     */
    public CommandClientBuilder setCoOwnerIds(String... coOwnerIds)
    {
    	this.coOwnerIds = coOwnerIds;
    	return this;
    }

    /**
     * Sets the one or more CoOwners of the bot.
     * <br>Make sure to verify that all of the IDs provided are ISnowflake compatible when setting this.
     * If it is not, this will warn the developer which ones are not.
     *
     * @param  coOwnerIds
     *         The ID(s) of the CoOwners
     *
     * @return This builder
     */
    public CommandClientBuilder setCoOwnerIds(long... coOwnerIds)
    {
        this.coOwnerIds = Arrays.stream(coOwnerIds).mapToObj(String::valueOf).toArray(String[]::new);
        return this;
    }

    /**
     * Sets the bot's prefix.
     * <br>If set null, empty, or not set at all, the bot will use a mention {@literal @Botname} as a prefix.
     *
     * @param  prefix
     *         The prefix for the bot to use
     *
     * @return This builder
     */
    public CommandClientBuilder setPrefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the bot's alternative prefix.
     * <br>If set null, the bot will only use its primary prefix prefix.
     *
     * @param  prefix
     *         The alternative prefix for the bot to use
     *
     * @return This builder
     */
    public CommandClientBuilder setAlternativePrefix(String prefix)
    {
        this.altprefix = prefix;
        return this;
    }

    /**
     * Sets an array of prefixes in case it's not enough. Be careful.
     *
     * @param prefixes
     *        The prefixes to use
     *
     * @return This builder
     */
    public CommandClientBuilder setPrefixes(String[] prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    /**
     * Sets the Prefix Function. Used if you want custom prefixes per server.
     * <br>Be careful, this function should be quick,
     * as it's executed every time MessageReceivedEvent is called.
     * <br>If function returns null, it will be ignored.
     *
     * @param prefixFunction
     *        The prefix function to execute to use
     *
     * @return This builder
     */
    public CommandClientBuilder setPrefixFunction(Function<MessageReceivedEvent, String> prefixFunction) {
        this.prefixFunction = prefixFunction;
        return this;
    }

    /**
     * Sets the pre-process function. This code is executed before every command.<br>
     * Returning "true" will allow processing to proceed.<br>
     * Returning "false" or "null" will prevent the Command from executing.
     *
     * @param commandPreProcessFunction
     *        The function to execute
     *
     * @deprecated Please use {@link #setCommandPreProcessBiFunction(BiFunction)} instead.
     *             You can simply add a new parameter for the command, it doesn't have to be used.
     * @return This builder
     */
    @Deprecated
    @DeprecatedSince("1.24.0")
    @ForRemoval(deadline = "2.0")
    public CommandClientBuilder setCommandPreProcessFunction(Function<MessageReceivedEvent, Boolean> commandPreProcessFunction)
    {
        this.commandPreProcessFunction = commandPreProcessFunction;
        return this;
    }

    /**
     * Sets the pre-process function. This code is executed before every command.<br>
     * Returning "true" will allow processing to proceed.<br>
     * Returning "false" or "null" will prevent the Command from executing.<br>
     * You can use Command to see which command will run.<br>
     *
     * @param commandPreProcessBiFunction
     *        The function to execute
     *
     * @return This builder
     */
    public CommandClientBuilder setCommandPreProcessBiFunction(BiFunction<MessageReceivedEvent, SimpleCommand, Boolean> commandPreProcessBiFunction) {
        this.commandPreProcessBiFunction = commandPreProcessBiFunction;
        return this;
    }

    /**
     * Sets whether the {@link com.greazi.discordbotfoundation.commands.CommandClient CommandClient} will use
     * the builder to automatically create a help command or not.
     *
     * @param  useHelp
     *         {@code false} to disable the help command builder, otherwise the CommandClient
     *         will use either the default or one provided via {@link com.greazi.discordbotfoundation.commands.CommandClientBuilder#setHelpConsumer(Consumer)}}.
     *
     * @return This builder
     */
    public CommandClientBuilder useHelpBuilder(boolean useHelp)
    {
    	this.useHelp = useHelp;
        return this;
    }

    /**
     * Sets the consumer to run as the bot's help command.
     * <br>Setting it to {@code null} or not setting this at all will cause the bot to use
     * the default help builder.
     *
     * @param  helpConsumer
     *         A consumer to accept a {@link com.greazi.discordbotfoundation.commands.CommandEvent CommandEvent}
     *         when a help command is called.
     *
     * @return This builder
     */
    public CommandClientBuilder setHelpConsumer(Consumer<CommandEvent> helpConsumer)
    {
        this.helpConsumer = helpConsumer;
        return this;
    }

    /**
     * Sets the word used to trigger the command list.
     * <br>Setting this to {@code null} or not setting this at all will set the help word
     * to {@code "help"}.
     *
     * @param  helpWord
     *         The word to trigger the help command
     *
     * @return This builder
     */
    public CommandClientBuilder setHelpWord(String helpWord)
    {
        this.helpWord = helpWord;
        return this;
    }

    /**
     * Sets the bot's support server invite.
     *
     * @param  serverInvite
     *         The support server invite
     *
     * @return This builder
     */
    public CommandClientBuilder setServerInvite(String serverInvite)
    {
        this.serverInvite = serverInvite;
        return this;
    }

    /**
     * Sets the emojis for success, warning, and failure.
     *
     * @param  success
     *         Emoji for success
     * @param  warning
     *         Emoji for warning
     * @param  error
     *         Emoji for failure
     *
     * @return This builder
     */
    public CommandClientBuilder setEmojis(String success, String warning, String error)
    {
        this.success = success;
        this.warning = warning;
        this.error = error;
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.entities.Activity Game} to use when the bot is ready.
     * <br>Can be set to {@code null} for JDA Utilities to not set it.
     *
     * @param  activity
     *         The Game to use when the bot is ready
     *
     * @return This builder
     */
    public CommandClientBuilder setActivity(Activity activity)
    {
        this.activity = activity;
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.entities.Activity Game} the bot will use as the default:
     * 'Playing <b>Type [prefix]help</b>'
     *
     * @return This builder
     */
    public CommandClientBuilder useDefaultGame()
    {
        this.activity = Activity.playing("default");
        return this;
    }

    /**
     * Sets the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} the bot will use once Ready
     * This defaults to ONLINE
     *
     * @param  status
     *         The status to set
     *
     * @return This builder
     */
    public CommandClientBuilder setStatus(OnlineStatus status)
    {
        this.status = status;
        return this;
    }

    /**
     * Adds a {@link SimpleCommand Command} and registers it to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     *
     * @param  simpleCommand
     *         The command to add
     *
     * @return This builder
     */
    public CommandClientBuilder addCommand(SimpleCommand simpleCommand)
    {
        simpleCommands.add(simpleCommand);
        return this;
    }

    /**
     * Adds and registers multiple {@link SimpleCommand Command}s to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     * <br>This is the same as calling {@link com.greazi.discordbotfoundation.commands.CommandClientBuilder#addCommand(SimpleCommand)} multiple times.
     *
     * @param  simpleCommands
     *         The Commands to add
     *
     * @return This builder
     */
    public CommandClientBuilder addCommands(SimpleCommand... simpleCommands)
    {
        for(SimpleCommand simpleCommand : simpleCommands)
            this.addCommand(simpleCommand);
        return this;
    }

    /**
     * Adds a {@link SimpleSlashCommand SimpleCommand} and registers it to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     *
     * @param  command
     *         The SimpleCommand to add
     *
     * @return This builder
     */
    public CommandClientBuilder addSlashCommand(SimpleSlashCommand command)
    {
        slashCommands.add(command);
        return this;
    }

    /**
     * Adds and registers multiple {@link SimpleSlashCommand SimpleCommand}s to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     * <br>This is the same as calling {@link com.greazi.discordbotfoundation.commands.CommandClientBuilder#addSlashCommand(SimpleSlashCommand)} multiple times.
     *
     * @param  commands
     *         The Commands to add
     *
     * @return This builder
     */
    public CommandClientBuilder addSlashCommands(SimpleSlashCommand... commands)
    {
        for(SimpleSlashCommand command: commands)
            this.addSlashCommand(command);
        return this;
    }

    /**
     * Forces Guild Only for SlashCommands.
     * Setting this to null disables the feature, but it is off by default.
     *
     * @param guildId the guild ID.
     * @return This Builder
     */
    public CommandClientBuilder forceGuildOnly(String guildId)
    {
        this.forcedGuildId = guildId;
        return this;
    }

    /**
     * Forces Guild Only for SlashCommands.
     * Setting this to null disables the feature, but it is off by default.
     *
     * @param guildId the guild ID.
     * @return This Builder
     */
    public CommandClientBuilder forceGuildOnly(long guildId)
    {
        this.forcedGuildId = String.valueOf(guildId);
        return this;
    }

    /**
     * Whether or not to manually upsert slash commands.
     * This is designed if you want to handle upserting, instead of doing it every boot.
     * False by default.
     *
     * @param manualUpsert your option.
     * @return This Builder
     */
    public CommandClientBuilder setManualUpsert(boolean manualUpsert)
    {
        this.manualUpsert = manualUpsert;
        return this;
    }

    /**
     * Adds an annotated command module to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     *
     * <p>For more information on annotated command modules, see
     * {@link com.greazi.discordbotfoundation.commands.annotation the annotation package} documentation.
     *
     * @param  module
     *         The annotated command module to add
     *
     * @return This builder
     *
     * @see    AnnotatedModuleCompiler
     * @see    com.greazi.discordbotfoundation.commands.annotation.JDACommand
     */
    public CommandClientBuilder addAnnotatedModule(Object module)
    {
        this.simpleCommands.addAll(compiler.compile(module));
        return this;
    }

    /**
     * Adds multiple annotated command modules to the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl} for this session.
     * <br>This is the same as calling {@link com.greazi.discordbotfoundation.commands.CommandClientBuilder#addAnnotatedModule(Object)} multiple times.
     *
     * <p>For more information on annotated command modules, see
     * {@link com.greazi.discordbotfoundation.commands.annotation the annotation package} documentation.
     *
     * @param  modules
     *         The annotated command modules to add
     *
     * @return This builder
     *
     * @see    AnnotatedModuleCompiler
     * @see    com.greazi.discordbotfoundation.commands.annotation.JDACommand
     */
    public CommandClientBuilder addAnnotatedModules(Object... modules)
    {
        for(Object command : modules)
            addAnnotatedModule(command);
        return this;
    }

    /**
     * Sets the {@link com.greazi.discordbotfoundation.commands.AnnotatedModuleCompiler AnnotatedModuleCompiler}
     * for this CommandClientBuilder.
     *
     * <p>If not set this will be the default implementation found {@link
     * com.greazi.discordbotfoundation.commands.impl.AnnotatedModuleCompilerImpl here}.
     *
     * @param  compiler
     *         The AnnotatedModuleCompiler to use
     *
     * @return This builder
     *
     * @see    AnnotatedModuleCompiler
     * @see    com.greazi.discordbotfoundation.commands.annotation.JDACommand
     */
    public CommandClientBuilder setAnnotatedCompiler(AnnotatedModuleCompiler compiler)
    {
        this.compiler = compiler;
        return this;
    }

    /**
     * Sets the <a href="https://www.carbonitex.net/discord/bots">Carbonitex</a> key for this bot's listing.
     *
     * <p>When set, the {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}
     * will automatically update it's Carbonitex listing with relevant information such as server count.
     *
     * @param  key
     *         A Carbonitex key
     *
     * @return This builder
     */
    public CommandClientBuilder setCarbonitexKey(String key)
    {
        this.carbonKey = key;
        return this;
    }

    /**
     * Sets the <a href="https://discord.bots.gg/">Discord Bots</a> API key for this bot's listing.
     *
     * <p>When set, the {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}
     * will automatically update it's Discord Bots listing with relevant information such as server count.
     *
     * <p>This will also retrieve the bot's total guild count in the same request, which can be accessed
     * via {@link com.greazi.discordbotfoundation.commands.CommandClient#getTotalGuilds()}.
     *
     * @param  key
     *         A Discord Bots API key
     *
     * @return This builder
     */
    public CommandClientBuilder setDiscordBotsKey(String key)
    {
        this.botsKey = key;
        return this;
    }

    /**
     * This method has been deprecated as the new(ish) ratelimit system is more complex than we'd like to
     * implement in JDA-Utils. Considering using some other library which correctly handles the ratelimits
     * for this service.
     *
     * @param  key
     *         A Discord Bot List API key
     *
     * @return This builder
     */
    @Deprecated
    public CommandClientBuilder setDiscordBotListKey(String key)
    {
        // this.botsOrgKey = key;
        return this;
    }

    /**
     * Sets the {@link com.greazi.discordbotfoundation.commands.CommandListener CommandListener} for the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}.
     *
     * @param  listener
     *         The CommandListener for the CommandClientImpl
     *
     * @return This builder
     */
    public CommandClientBuilder setListener(CommandListener listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Sets the {@link java.util.concurrent.ScheduledExecutorService ScheduledExecutorService} for the
     * {@link com.greazi.discordbotfoundation.commands.impl.CommandClientImpl CommandClientImpl}.
     *
     * @param  executor
     *         The ScheduledExecutorService for the CommandClientImpl
     *
     * @return This builder
     */
    public CommandClientBuilder setScheduleExecutor(ScheduledExecutorService executor)
    {
        this.executor = executor;
        return this;
    }

    /**
     * Sets the Command Client to shut down internals automatically when a
     * {@link net.dv8tion.jda.api.events.ShutdownEvent ShutdownEvent} is received.
     *
     * @param shutdownAutomatically
     *        {@code false} to disable calling the shutdown method when a ShutdownEvent is received
     * @return This builder
     */
    public CommandClientBuilder setShutdownAutomatically(boolean shutdownAutomatically)
    {
        this.shutdownAutomatically = shutdownAutomatically;
        return this;
    }

    /**
     * Sets the internal size of the client's {@link com.greazi.discordbotfoundation.commons.utils.FixedSizeCache FixedSizeCache}
     * used for caching and pairing the bot's response {@link net.dv8tion.jda.api.entities.Message Message}s with
     * the calling Message's ID.
     *
     * <p>Higher cache size means that decay of cache contents will most likely occur later, allowing the deletion of
     * responses when the call is deleted to last for a longer duration. However this also means larger memory usage.
     *
     * <p>Setting {@code 0} or negative will cause the client to not use linked caching <b>at all</b>.
     *
     * @param  linkedCacheSize
     *         The maximum number of paired responses that can be cached, or {@code <1} if the
     *         built {@link com.greazi.discordbotfoundation.commands.CommandClient CommandClient}
     *         will not use linked caching.
     *
     * @return This builder
     */
    public CommandClientBuilder setLinkedCacheSize(int linkedCacheSize)
    {
        this.linkedCacheSize = linkedCacheSize;
        return this;
    }

    /**
     * Sets the {@link com.greazi.discordbotfoundation.commands.GuildSettingsManager GuildSettingsManager}
     * for the CommandClientImpl built using this builder.
     *
     * @param  manager
     *         The GuildSettingsManager to set.
     *
     * @return This builder
     */
    public CommandClientBuilder setGuildSettingsManager(GuildSettingsManager manager)
    {
        this.manager = manager;
        return this;
    }
}
