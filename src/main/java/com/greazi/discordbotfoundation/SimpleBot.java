package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.command.PingCommand;
import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.SlashCommandHandler;
import com.greazi.discordbotfoundation.constants.Constants;
import com.greazi.discordbotfoundation.debug.BotException;
import com.greazi.discordbotfoundation.mysql.SqlManager;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleBot {

    /**
     * The JDA instance
     */
    @Getter
    private static JDA jda;

    /**
     * The instance of the bot
     */
    @Getter
    private static volatile SimpleBot instance;

    /**
     * The main guild of the bot
     */
    @Getter
    private static Guild mainGuild;

    /**
     * The bot itself as a user
     */
    @Getter
    private static SelfUser selfUser;

    /**
     * If the bot is enabled or not
     */
    @Getter
    private boolean enabled;

    /**
     * The SQL manager
     */
    @Getter
    private static SqlManager sqlManager;

    // ----------------------------------------------------------------------------------------
    // Instance specific
    // ----------------------------------------------------------------------------------------

    /**
     * Check if the instance has been set before proceeding with
     * your tasks.
     *
     * @return boolean to see if there is an instance
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    public static void main(final String[] args) {
        new SimpleBot() {
            @Override
            protected void onBotStart() {

            }
        };
    }

    /**
     * The main method that starts the bot
     * <p>
     * Registering the discord bot to Discord and starting up all
     * managers.
     */
    public SimpleBot() {
        this.enabled = false;

        // Setting the instance on the start to make sure we have an instance to work with
        this.instance = this;

        // If there is a startup logo, print it and send some more information
        if (getStartupLogo() != null) {
            logFramed(getStartupLogo());
        } else {
            Common.consoleLine();
        }
        if (getFoundedYear() != -1) {
            log(getName() + " developed by " + getDeveloperName() + ", founded in " + getFoundedYear());
            log("Version: " + getVersion() + " | Foundation version: " + getFoundationVersion());
            Common.consoleLine();
        } else {
            log(getName() + " developed by " + getDeveloperName());
            log("Version: " + getVersion() + " | Foundation version: " + getFoundationVersion());
            Common.consoleLine();
        }

        // A method that loads before everything else
        onPreLoad();

        // Load the settings file
        SimpleSettings.init();

        // Create the bot activity
        final Activity activityType;
        final String activity = SimpleSettings.Activity.Message();
        switch (SimpleSettings.Activity.Type().toLowerCase()) {
            case "watching" -> {
                activityType = Activity.watching(activity);
            }
            case "playing" -> {
                activityType = Activity.playing(activity);
            }
            case "streaming" -> {
                activityType = Activity.streaming(activity, getDeveloperWebsite());
            }
            case "listening" -> {
                activityType = Activity.listening(activity);
            }
            default -> {
                activityType = Activity.watching(getName());
            }
        }

        // Registering the bot to Discord using JDA
        log("Registering the bot to Discord");
        try {
            jda = JDABuilder.createDefault(SimpleSettings.Bot.Token())
                    .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
                    .setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setActivity(activityType)
                    .setEventManager(new AnnotatedEventManager())
                    .build().awaitReady();

            log("Successfully registered the bot with Discord");
        } catch (final InterruptedException e) {
            throw new BotException(e, "Failed to register the bot to Discord");
        }

        // Setting the selfUser
        selfUser = jda.getSelfUser();

        // Set the main guild
        setMainGuild();

        // Load our SQL manager
        sqlManager = new SqlManager();

        // Call the onBotLoad method
        onBotLoad();

        // Register the ping command
        SimpleCommand.register(new PingCommand());

        // Call the onBotStart method
        onBotStart();

        // Call the onReloadableStart method
        onReloadableStart();

        SimpleCommand.registerAll();

        getJda().addEventListener(new SlashCommandHandler() {
            @Override
            protected void execute(@NotNull final SlashCommandInteractionEvent event) {

            }
        });

        getJda().addEventListener(new PingCommand());

        // Set the enabled to true
        this.enabled = true;

        // Log that the bot is ready
        log(getName() + " has successfully started and is fully loaded");
    }

    // ----------------------------------------------------------------------------------------
    // Setup methods
    // ----------------------------------------------------------------------------------------

    /**
     * Register the JDA instance
     */
    private static void setMainGuild() {
        final long mainGuildId = SimpleSettings.Bot.MainGuild();

        if (mainGuildId == 0L) {
            warn("No main guild set in the settings file");
            return;
        }
        final Guild guild = jda.getGuildById(mainGuildId);
        if (guild == null) {
            warn("The main guild could not be found");
            return;
        } else {
            mainGuild = guild;
        }
    }


    // ----------------------------------------------------------------------------------------
    // Logging
    // ----------------------------------------------------------------------------------------

    private static void log(final String message) {
        Common.log(message);
    }

    private static void log(final String... messages) {
        Common.log(messages);
    }

    private static void logFramed(final String message) {
        Common.logFramed(message);
    }

    private static void logFramed(final String... messages) {
        Common.logFramed(messages);
    }

    private static void warn(final String message) {
        Common.warning(message);
    }

    private static void error(final String message) {
        Common.error(message);
    }

    private static void error(final String... messages) {
        Common.error(messages);
    }

    /**
     * Should every message be divided by \n by an own method (tends to work more
     * than split("\n"))
     *
     * @return If the system need to force a new line with \n
     */
    public boolean enforceNewLine() {
        return false;
    }

    // ----------------------------------------------------------------------------------------
    // Delegate methods
    // ----------------------------------------------------------------------------------------

    /**
     * Called way before everything starts
     * <p>
     * This method runs before everything else, meaning that nothing is loaded yet.
     * Settings, MySQL, JDA, etc. are not loaded yet!
     */
    protected void onPreLoad() {
    }


    /**
     * Called the moment JDA is registerd
     */
    protected void onBotLoad() {
    }

    /**
     * The main loading method, called when we are ready to load
     */
    protected abstract void onBotStart();

    /**
     * The main method called when we are about to shut down
     */
    protected void onBotStop() {
    }

    /**
     * Invoked before settings were reloaded.
     */
    protected void onBotPreReload() {
    }

    /**
     * Invoked after settings were reloaded.
     */
    protected void onBotReload() {
    }

    /**
     * Register your commands, events, tasks and files here.
     * <p>
     * This is invoked when you do `/reload`
     */
    protected void onReloadableStart() {
    }


    // ----------------------------------------------------------------------------------------
    // Additional features
    // ----------------------------------------------------------------------------------------

    /**
     * The start-up fancy logo
     *
     * @return null by default
     */
    protected String[] getStartupLogo() {
        return null;
    }

    /**
     * Get the year of foundation displayed on the about command
     *
     * @return -1 by default, or the founded year
     */
    public int getFoundedYear() {
        return -1;
    }

    /**
     * Get the version of the bot
     *
     * @return 1.0.0 by default, or the version
     */
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * Retrieve the version of the foundation
     *
     * @return Version
     */
    public static String getFoundationVersion() {
        return Constants.Version.FOUNDATION;
    }

    /**
     * Retrieve the name of the bot
     *
     * @return Name
     */
    public static String getName() {
        return SimpleSettings.Bot.Name();
    }

    /**
     * Retrieve the developer of the bot
     *
     * @return Developer
     */
    public String getDeveloperName() {
        return SimpleSettings.Developer.Name();
    }

    /**
     * Get the embed author link
     *
     * @return Author link
     */
    public String getDeveloperWebsite() {
        return SimpleSettings.Developer.Website();
    }
}
