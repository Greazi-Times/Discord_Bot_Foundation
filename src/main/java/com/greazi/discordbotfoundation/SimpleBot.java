package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.console.ClearCommand;
import com.greazi.discordbotfoundation.console.HelpCommand;
import com.greazi.discordbotfoundation.handlers.buttons.ButtonHandler;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.handlers.commands.SlashCommandHandler;
import com.greazi.discordbotfoundation.command.general.PingCommand;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.handlers.console.ConsoleCommandHandler;
import com.greazi.discordbotfoundation.handlers.modals.ModalHandler;
import com.greazi.discordbotfoundation.handlers.selectmenu.SelectMenuHandler;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
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

import javax.security.auth.login.LoginException;

// TODO: ALL FILES!!!!
//    Check all files for comments and add them if needed

/**
 * A basic discord bot that represents the discord bot library
 */
public abstract class SimpleBot {

    // ----------------------------------------------------------------------------------------
    // Static
    // ----------------------------------------------------------------------------------------

    private static volatile SimpleBot instance;
    public static JDA jda;

    private static Guild mainGuild;
    private static SelfUser self;
    private static SlashCommandHandler slashCommandHandler;
    private static ButtonHandler buttonHandler;
    private static ModalHandler modalHandler;
    private static SelectMenuHandler menuHandler;
    private static ConsoleCommandHandler consoleCommandHandler;

    private boolean enabled;

    /**
     * Returns the instance of {@link SimpleBot}.
     * <p>
     * It is recommended to override this in your own {@link SimpleBot}
     * implementation so you will get the instance of that, directly.
     *
     * @return this instance
     */
    public static SimpleBot getInstance() {
        return instance;
    }

    /**
     * Get if the instance that is used across the library has been set. Normally it
     * is always set, except for testing.
     *
     * @return if the instance has been set.
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    // ----------------------------------------------------------------------------------------
    // Instance specific
    // ----------------------------------------------------------------------------------------

    // TODO Add this method for the reload of the bot

    /**
     * For your convenience, event listeners and timed tasks may be set here to stop/unregister
     * them automatically on reload
     */
    /*private final Reloadables reloadables = new Reloadables();*/

    /**
     * An internal flag to indicate whether we are calling the {@link #onReloadableStart()}
     * block. We register things using {@link #} <-- reloadable during this block
     */
    private boolean startingReloadables = false;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * The main method that is the beginning of the bot
     * <p>
     * By default, it will start the bot with the {@link #registerJda(String, String)} method
     */
    public SimpleBot() {
        // A debugger that sends a message to the console when the bot is starting
        Debugger.debug("Startup", "Starting the bot! SimpleBot();104");

        // Load the settings from the config file
        SimpleSettings.init();

        // Set the instance of the bot
        instance = this;

        // Load way before the bot starts to avoid any issues
        onPreLoad();

        // Initialize the bot
        registerJda(SimpleSettings.Bot.Token(), SimpleSettings.Activity.Message());

        // Set the main guild of the bot
        if (SimpleSettings.Bot.MainGuild() != null) {
            mainGuild = jda.getGuildById(SimpleSettings.Bot.MainGuild());
        } else {
            Common.error("Main Guild not set in settings.yml");
            mainGuild = jda.getGuilds().get(0);
        }

        // Load after the bot has been initialized
        onBotLoad();

        // Set up the command manager that handles slash commands
        setupCommandManager();

		// This is a method that will be ren everytime the bot is reloaded
		// In here you add all the commands and events
		/** {@link #onReloadableStart()} */
		onReload();

		// Message that the bot has started
		Common.success("Bot is ready");
	}

    /**
     * The pre start of the bot. Register the bot and do some simple checks
     */
    public final void setupCommandManager() {
        Debugger.debug("Startup", "Starting the bot! onPreStart();105");

        jda.addEventListener(new SimpleSlashCommand() {
            @Override
            protected void execute(SlashCommandInteractionEvent event) {

            }
        });
        slashCommandHandler = new SlashCommandHandler();
        buttonHandler = new ButtonHandler();
        modalHandler = new ModalHandler();
        menuHandler = new SelectMenuHandler();
        consoleCommandHandler = new ConsoleCommandHandler();
    }

    /**
     * A method that is called when the bot is reloaded
     */
    public void onReload() {
        Debugger.debug("Startup", "Starting the bot! onReload();167");

        // Check if the bot is enabled before doing anything
        if (enabled) {
            Common.error("The bot is already enabled!");
            return;
        }

        // Run the onReloadableStart() method
        onReloadableStart();

        // Register all the commands
        slashCommandHandler.registerCommands();

        // Load the static commands
        getSlashCommandHandler().addCommand(new PingCommand());

        // A boolean that says the bot is loaded and enabled
        enabled = true;

        // Load the static console commands
        getConsoleCommandHandler().addCommand(new HelpCommand()).addCommand(new ClearCommand());
    }

    /**
     * Registration of the bot itself
     *
     * @param token    = The token of the bot
     * @param activity = The activity status of the bot
     */
    private static void registerJda(String token, String activity) {
        Debugger.debug("Startup", "Registering JDA! registerJda();149");
        try {
            jda = JDABuilder.createDefault(token)
                    .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
                    .setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setActivity(Activity.watching(activity))
                    .setEventManager(new AnnotatedEventManager())
                    .build().awaitReady();

            // Set self to the bot
            self = jda.getSelfUser();
        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
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
     * Called just before the bot starts
     */
    protected void onBotLoad() {
    }

    //Copyright

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
    public static Guild getGuild() {
        return mainGuild;
    }

    /**
     * Retrieve the bot members self
     *
     * @return Self
     */
    public static SelfUser getSelf() {
        return self;
    }

    /**
     * Retrieve the slash command handler
     *
     * @return Slash command handler
     */
    public static SlashCommandHandler getSlashCommandHandler() {
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
     * Retrieve the select menu handler
     *
     * @return SelectMenu handler
     */
    public static SelectMenuHandler getSelectMenuHandler() {
        return menuHandler;
    }

    /**
     * Retrieve the console command handler
     *
     * @return Console command handler
     */
    public static ConsoleCommandHandler getConsoleCommandHandler() {
        return consoleCommandHandler;
    }

    // ----------------------------------------------------------------------------------------
    // Additional features
    // ----------------------------------------------------------------------------------------

    /**
     * Retrieve the version of the bot
     *
     * @return Version
     */
    public static String getVersion() {
        return "1.0.0";
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
    public String getDeveloper() {
        return "Greazi";
    }

    /**
     * !!! THIS WILL BE MOVED WITH THE NEW SETTINGS SYSTEM !!!
     * <p>
     * Retrieve the main embed image of the bot
     *
     * @return Embed image
     */
    public String getEmbedAuthorImage() {
        return "https://i.imgur.com/ddzfapZ.png";
    }

    /**
     * Get the embed author link
     *
     * @return Author link
     */
    public String getLink() {
        return "https://greazi.com";
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
