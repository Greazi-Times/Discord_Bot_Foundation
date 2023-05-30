package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.command.core.AboutCommand;
import com.greazi.discordbotfoundation.command.core.StopCommand;
import com.greazi.discordbotfoundation.command.general.PingCommand;
import com.greazi.discordbotfoundation.console.ClearConsoleCommand;
import com.greazi.discordbotfoundation.console.HelpConsoleCommand;
import com.greazi.discordbotfoundation.console.StopConsoleCommand;
import com.greazi.discordbotfoundation.constants.Constants;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.handlers.buttons.ButtonHandler;
import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.handlers.commands.SlashCommandHandler;
import com.greazi.discordbotfoundation.handlers.console.ConsoleCommandHandler;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import com.greazi.discordbotfoundation.handlers.crons.CronHandler;
import com.greazi.discordbotfoundation.handlers.modals.ModalHandler;
import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.selectmenu.entity.EntitySelectMenuHandler;
import com.greazi.discordbotfoundation.handlers.selectmenu.string.StringSelectMenuHandler;
import com.greazi.discordbotfoundation.handlers.selectmenu.string.SimpleStringSelectMenu;
import com.greazi.discordbotfoundation.mysql.SqlManager;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.HashMap;

/**
 * A basic discord bot that represents the discord bot library
 */
public abstract class SimpleBot {

    // ----------------------------------------------------------------------------------------
    // Static
    // ----------------------------------------------------------------------------------------

    // Instances
    private static volatile SimpleBot instance;
    public static JDA jda;

    // Important variables
    private static Guild mainGuild;
    private static SelfUser self;
    private boolean enabled;

    // Handlers
    private static SlashCommandHandler slashCommandHandler;
    private static ButtonHandler buttonHandler;
    private static ModalHandler modalHandler;
    private static StringSelectMenuHandler stringSelectMenuHandler;
    private static EntitySelectMenuHandler entitySelectMenuHandler;
    private static ConsoleCommandHandler consoleCommandHandler;
    private static CronHandler cronHandler;
    private static SqlManager sqlManager;

    // ----------------------------------------------------------------------------------------
    // Instance specific
    // ----------------------------------------------------------------------------------------

    /**
     * Returns the instance of {@link SimpleBot}.
     * <p>
     * You can override this in your own {@link SimpleBot}
     * implementation, so you will get the instance of that, directly.
     * It is not recommended but if needed you can.
     *
     * @return Main instance of the Discord bot
     */
    public static SimpleBot getInstance() {
        return instance;
    }

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

    /**
     * The main method that starts the bot
     * <p>
     * A Discord bot needs the {@link #registerJda(String, Activity)} method to register.
     * Before the bot registers it will set some things up and than register the bot to ensure
     * a function Discord bot.
     */
    public SimpleBot() {
        // Set the bot to startup
        this.enabled = false;

        // Check if the bot has a startup logo
        if (getStartupLogo() != null) {
            Common.logNoPrefix(getStartupLogo());
        }

        // Load even before JDA is regsitered
        onPreLoad();

        // Load all the settings
        SimpleSettings.init();

        // Setting the instance
        instance = this;

        // Check the activity for the bot
        final Activity activityType;
        final String activity = SimpleSettings.Activity.Message();
        Debugger.debug("Activity", "Activity is " + activity);
        switch (SimpleSettings.Activity.Type().toLowerCase()) {
            case "watching" -> {
                activityType = Activity.watching(activity);
                Debugger.debug("Activity", "Activity type is watching");
            }
            case "playing" -> {
                activityType = Activity.playing(activity);
                Debugger.debug("Activity", "Activity type is playing");
            }
            case "streaming" -> {
                activityType = Activity.streaming(activity, getDeveloperWebsite());
                Debugger.debug("Activity", "Activity type is streaming");
            }
            case "listening" -> {
                activityType = Activity.listening(activity);
                Debugger.debug("Activity", "Activity type is listening");
            }
            default -> {
                activityType = Activity.watching(getName());
                Debugger.debug("Activity", "Activity type is default");
            }
        }

        // Register the bot by Discord
        registerJda(SimpleSettings.Bot.Token(), activityType);

        // Load the {@link SqlManager}
        sqlManager = new SqlManager();

        // Get the main guild from the settings
        final long mainGuildId = SimpleSettings.Bot.MainGuild();

        // Check if the main Guild is configured
        if (mainGuildId == 0L) {
            Common.error(
                    "It seems that you haven't set the main guild ID in the config file!",
                    "Now using the first guild as the main guild!"
            );
            mainGuild = jda.getGuilds().get(0);
        } else {
            if (mainGuild == null) {
                Guild guild = jda.getGuildById(mainGuildId);
                if (guild != null) {
                    mainGuild = guild;
                    Common.log("Main guild set to: " + ConsoleColor.CYAN + guild.getName() + ConsoleColor.BLACK_BRIGHT + " (" + guild.getId() + ")");
                } else {
                    Common.error(
                            "The main guild ID is not valid! Please check the config file!",
                            "Now using the first guild as the main guild!"
                    );
                }
            } else {
                Common.log("Main guild is already set to: " + ConsoleColor.CYAN + mainGuild.getName() + ConsoleColor.BLACK_BRIGHT + " (" + mainGuild.getId() + ")");
            }
        }

        // Setup the managers for the bot
        setupManagers();

        // Load the static commands
        registerCommands(
                new PingCommand(),
                new AboutCommand()
        );
        if (SimpleSettings.Stop.Enabled()) {
            registerCommand(new StopCommand());
        }

        // Load the static console commands
        registerConsoleCommands(
                new HelpConsoleCommand(),
                new ClearConsoleCommand(),
                new StopConsoleCommand()
        );

        // Load the bots matrix
        onBotStart();

        // Run the method that gets called to load things on startup and reloads
        onReloadableStart();

        // Register all the commands
        slashCommandHandler.registerCommands();

        // TODO: Make the reload system work

        // Enable the bot
        // TODO: Add a check to all managers to see if the bot is enabled
        //       before running command, button, menu, and modal code.
        // TODO: Change how this method works. only set enabled to true if
        //       it is really done with everything and no errors accured.
        //       Adding multiple booleans in this class could do this.
        //       (separate checks for all systems)
        setEnabled();

        // A check to see if the bot is succecfully enabled
        if (enabled) {
            Common.success("Bot is ready");
        } else {
            Common.error("The bot failed to enable something is wrong!");
        }

        // A debug message to see what command are registered
        if (Debugger.isDebugged("SlashCommand")) {
            for (final Command command : getJDA().retrieveCommands().complete()) {
                Debugger.debug("SlashCommand", command.getName() + "" + command.getDescription());
            }
        }

        // A log option to show how many things are registered
        Common.log("Loaded a total of " + ConsoleColor.CYAN + getSlashCommandHandler().getTotal() + ConsoleColor.RESET + " slash commands " + ConsoleColor.CYAN + getSlashCommandHandler().getGuildTotal() + ConsoleColor.RESET + " main guild and " + ConsoleColor.CYAN + getSlashCommandHandler().getPublicTotal() + ConsoleColor.RESET + " public");
        Common.log("Loaded a total of " + ConsoleColor.CYAN + getConsoleCommandHandler().getTotal() + ConsoleColor.RESET + " console commands");
        Common.log("Loaded a total of " + ConsoleColor.CYAN + getStringSelectMenuHandler().getTotal() + ConsoleColor.RESET + " menus");
        Common.log("Loaded a total of " + ConsoleColor.CYAN + getButtonHandler().getTotal() + ConsoleColor.RESET + " buttons");
        Common.log("Loaded a total of " + ConsoleColor.CYAN + getModalHandler().getTotal() + ConsoleColor.RESET + " modals");
    }

    /**
     * The registration of the bot by Discord
     *
     * @param token The token from the https://discord.com/developers/applications
     * @param activity The activity of the bot
     */
    private static void registerJda(final String token, final Activity activity) {
        // Log message when starting to register the bot
        Common.log("Registering JDA...");
        
        // Registering the bot by Discord
        try {
            jda = JDABuilder.createDefault(token)
                    .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
                    .setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setActivity(activity)
                    .setEventManager(new AnnotatedEventManager())
                    .build().awaitReady();

            // Set selfUser so it can be used later
            self = jda.getSelfUser();

            onBotLoad();

            // Log message when the bot is registered
            Common.log("JDA registered");
        } catch (final InterruptedException ex) {
            Common.throwError(ex, "Failed to register JDA");
        }
    }

    /**
     * Initilization of all managers and handlers
     */
    public final void setupManagers() {
        // Log message to let know managers are starting
        Common.log("Setting up the system managers");

        slashCommandHandler = new SlashCommandHandler();

        buttonHandler = new ButtonHandler();
        modalHandler = new ModalHandler();

        stringSelectMenuHandler = new StringSelectMenuHandler();
        entitySelectMenuHandler = new EntitySelectMenuHandler();

        consoleCommandHandler = new ConsoleCommandHandler();

        cronHandler = new CronHandler();

        // Log message to let know managers are setup
        Common.log("System managers have been set up");
    }

    // TODO: Fix the reload system

    /**
     * Reload the bot, this method stops all systems and
     * load the important things again.
     * Database, Settings, cronHandlers
     */
    private void reload() {
        Common.log("Reload has been started")

        // Check if the bot is enabled or not.
        if (enabled) {
            Common.log("Stopping systems to reload configuration");

            // TODO: Add system stoppers

            // Disable the bot when everything is disabled
            enabled = false
        }

        // Load things before settings are loaded
        onBotPreReload()

        // Load after settings have loaded
        onBotReload()

        // TODO: Add enable methods

        // Close old SQL connection safely before opening a new one
		/*try {
			sqlManager.getConnection().close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		// Create a new SQL connection
		sqlManager = new SqlManager();*/

        // Load when everything has been setup and final things need to happen
        onReloadableStart();

        // Register cron jobs
        cronHandler.scheduleJobs();

        // A boolean that says the bot is loaded and enabled
        enabled = true;
    }

    /**
     * Stop the bot, this method will stop the bot and will
     * disable the Discord bot.
     * Using this method will ensure the bot stops and saves
     * all its data before stopping everything.
     */
    public void stop() {
        // Log message to let know the bot is stopping
        Common.log("Stopping the bot")

        // Stopping cron jobs
        cronHandler.stop();

        // Stop code that needs to be run when stopping the bot.
        onBotStop();

        // Create a new thread for timing purposes
        new Thread(() -> {
            // Log message to let know when the bot is stopping
            Common.log("Stopping JDA");

            // Starting to shutdown JDA
            jda.shutdown();

            // Set a loop for the time-out
            int jdaShutdownTimeout = 0;
            while (jda.getStatus() != JDA.Status.SHUTDOWN) {
                // Check if the limit of 15 sec is reached
                if (jdaShutdownTimeout > 15) {
                    Common.warning("JDA shutdown timeout reached, forcing shutdown");
                    jda.shutdownNow();
                    break;
                }

                // Wait a second
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                // Counting 1 up for the timeout counter
                jdaShutdownTimeout++;
            }

            // Log message to let know JDA has been stoped
            Common.log("JDA stopped");

        }).start();

        // Log message to let know the bot is not finished shutting down
        Common.log("The bot is succefully been shutdown. Good bye!")

        // Stopping the jar
        System.exit(0);
    }

    // ----------------------------------------------------------------------------------------
    // Registration methods   <- Used to register your commands, buttons, menus, modals and console commands
    // ----------------------------------------------------------------------------------------

    /**
     * Register a new slash command
     *
     * @param command SimpleSlashCommand
     */
    protected final void registerCommand(final SimpleSlashCommand command) {
        getSlashCommandHandler().addCommand(command);
    }

    /**
     * Register new slash commands at once
     *
     * @param commands SimpleSlashCommands
     */
    protected final void registerCommands(final SimpleSlashCommand... commands) {
        for (final SimpleSlashCommand command : commands) {
            getSlashCommandHandler().addCommand(command);
        }
    }

    /**
     * Register a new console command
     *
     * @param command SimpleConsoleCommand
     */
    protected final void registerConsoleCommand(final SimpleConsoleCommand command) {
        getConsoleCommandHandler().addCommand(command);
    }

    /**
     * Register new console commands at once
     *
     * @param commands SimpleConsoleCommand
     */
    protected final void registerConsoleCommands(final SimpleConsoleCommand... commands) {
        for (final SimpleConsoleCommand command : commands) {
            getConsoleCommandHandler().addCommand(command);
        }
    }

    // ----------------------------------------------------------------------------------------
    // Delegate methods    <-- Methods that can be used to load your stuff
    // ----------------------------------------------------------------------------------------

    /**
     * Called way before everything starts (Not recommended to use)
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
    // Foundation handle settings
    // ----------------------------------------------------------------------------------------

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
    // Main setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the bot as status enabled
     */
    // Needs to be deprecated
    private void setEnabled() {
        this.enabled = true;
    }

    // ----------------------------------------------------------------------------------------
    // Main getters
    // ----------------------------------------------------------------------------------------

    /**
     * Retrieve the JDA instance
     *
     * @return JDA instance
     */
    public static JDA getJDA() {
        return jda;
    }

    /**
     * Retrieve the bot instance
     *
     * @return Bot instance
     */
    public static SimpleBot getBot() {
        return instance;
    }

    /**
     * Retrieve the bots main guild
     *
     * @return Main guild
     */
    public static Guild getMainGuild() {
        return mainGuild;
    }

    /**
     * Retrieve the bot its self as a User
     *
     * @return User The bot it self
     */
    public static SelfUser getSelf() {
        return self;
    }

    /**
     * Retrieve the slash command handler
     *
     * @return Slash command handler
     */
    private static SlashCommandHandler getSlashCommandHandler() {
        return slashCommandHandler;
    }

    /**
     * Retrieve the button handler
     *
     * @return Button handler
     */
    public static ButtonHandler getButtonHandler() {
        return buttonHandler;
    }

    /**
     * Retrieve the modal handler
     *
     * @return Modal handler
     */
    public static ModalHandler getModalHandler() {
        return modalHandler;
    }

    /**
     * Retrieve the string select menu handler
     *
     * @return StringSelectMenu handler
     */
    public static StringSelectMenuHandler getStringSelectMenuHandler() {
        return stringSelectMenuHandler;
    }

    /**
     * Retrieve the entity select menu handler
     *
     * @return EntitySelectMenu handler
     */
    public static EntitySelectMenuHandler getEntitySelectMenuHandler() {
        return entitySelectMenuHandler;
    }

    /**
     * Retrieve the console command handler
     *
     * @return Console command handler
     */
    private static ConsoleCommandHandler getConsoleCommandHandler() {
        return consoleCommandHandler;
    }

    /**
     * Retrieve the cron jobs handler
     *
     * @return Console cron jobs handler
     */
    public static CronHandler getCronHandler() {
        return cronHandler;
    }

    /**
     * Retrieve the sql manager
     *
     * @return Sql manager
     */
    public static SqlManager getSqlManager() {
        return sqlManager;
    }

    /**
     * Get a modal from the modal from the modal handler
     *
     * @param button_id The ID of the button
     * @return SimpleButton
     */
    public static SimpleButton getButton(final String button_id) {
        return getButtonHandler().getButton(button_id);
    }

    public static SimpleStringSelectMenu getSelectMenu(final String menu_id) {
        return getStringSelectMenuHandler().getMenu(menu_id);
    }

    /**
     * Get a modal from the modal from the modal handler
     *
     * @param modal_id The ID of the modal
     * @return SimpleModal
     */
    public static SimpleModal getModal(final String modal_id) {
        return getModalHandler().getModal(modal_id);
    }

    /**
     * Get a console command from the console command handler
     *
     * @param consoleCommand The console command
     * @return Console Command
     */
    public static SimpleConsoleCommand getConsoleCommand(final String consoleCommand) {
        return getConsoleCommandHandler().getCommand(consoleCommand);
    }

    /**
     * Get all console commands from the console command handler
     *
     * @return Console Command HashMap
     */
    public static HashMap<String, SimpleConsoleCommand> getConsoleCommands() {
        return getConsoleCommandHandler().getCommandList();
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
     * Retrieve the version of the bot
     *
     * @return Version
     */
    public static String getVersion() {
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

    /**
     * Check if the bot is enabled
     *
     * @return Bot enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
