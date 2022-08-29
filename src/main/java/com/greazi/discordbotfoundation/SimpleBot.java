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
import com.greazi.discordbotfoundation.handlers.selectmenu.SelectMenuHandler;
import com.greazi.discordbotfoundation.handlers.selectmenu.SimpleSelectMenu;
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

import javax.security.auth.login.LoginException;
import java.util.HashMap;

// TODO: ALL FILES!!!!
//    Check all files for comments and add them if needed

// TODO: Create an events adder method that adds all events to the event manager

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
	private static CronHandler cronHandler;

	private boolean enabled;

	// ----------------------------------------------------------------------------------------
	// Instance specific
	// ----------------------------------------------------------------------------------------

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
	// Main methods
	// ----------------------------------------------------------------------------------------

	/**
	 * The main method that is the beginning of the bot
	 * <p>
	 * By default, it will start the bot with the {@link #registerJda(String, Activity)} method
	 */
	public SimpleBot() {
		this.enabled = false;

		// A debugger that sends a message to the console when the bot is starting
		Debugger.debug("Startup", "Starting the bot! SimpleBot();104");

		if (getStartupLogo() != null) {
			Common.logNoPrefix(getStartupLogo());
		}

		// Load the settings from the config file
		SimpleSettings.init();

		// Set the instance of the bot
		instance = this;

		// Load way before the bot starts to avoid any issues
		onPreLoad();

		// Check the activity for the bot
		final Activity activityType;
		final String activity = SimpleSettings.Activity.Message();
		switch (SimpleSettings.Activity.Type().toLowerCase()) {
			case "watching":
				activityType = Activity.watching(activity);
				break;
			case "playing":
				activityType = Activity.playing(activity);
				break;
			case "streaming":
				activityType = Activity.streaming(activity, getDeveloperWebsite());
				break;
			case "listening":
				activityType = Activity.listening(activity);
				break;
			default:
				activityType = Activity.watching(getName());
		}

		// Initialize the bot
		registerJda(SimpleSettings.Bot.Token(), activityType);

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

		// TODO: Create a check if the bot is fully enabled
		setEnabled();

		// A check if the bot is truly enabled !!!NOT YET FINISHED!!!
		if (enabled) {
			// Message that the bot has started
			Common.success("Bot is ready");
		} else {
			Common.error("The bot failed to enable something is wrong!");
		}

		for (final Command command : getJDA().retrieveCommands().complete()) {
			Common.log(command.getName() + " " + command.getDescription());
		}
	}

	/**
	 * The pre start of the bot. Register the bot and do some simple checks
	 */
	public final void setupCommandManager() {
		Debugger.debug("Startup", "Starting the bot! onPreStart();105");

		slashCommandHandler = new SlashCommandHandler();
		buttonHandler = new ButtonHandler();
		modalHandler = new ModalHandler();
		menuHandler = new SelectMenuHandler();
		consoleCommandHandler = new ConsoleCommandHandler();
		cronHandler = new CronHandler();
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

		// Close old SQL connection safely before opening a new one
		/*try {
			sqlManager.getConnection().close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		// Create a new SQL connection
		sqlManager = new SqlManager();*/

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

		// Run the onReloadableStart() method
		onReloadableStart();

		// Register commands to JDA
		slashCommandHandler.registerCommands();

		// Register cron jobs
		cronHandler.scheduleJobs();

		// A boolean that says the bot is loaded and enabled
		enabled = true;

		// A log option to show how many things are registered
		Common.log("Loaded a total of " + ConsoleColor.CYAN + getSlashCommandHandler().getTotal() + ConsoleColor.RESET + " slash commands " + ConsoleColor.CYAN + getSlashCommandHandler().getGuildTotal() + ConsoleColor.RESET + " main guild and " + ConsoleColor.CYAN + getSlashCommandHandler().getPublicTotal() + ConsoleColor.RESET + " public");
		Common.log("Loaded a total of " + ConsoleColor.CYAN + getConsoleCommandHandler().getTotal() + ConsoleColor.RESET + " console commands");
		Common.log("Loaded a total of " + ConsoleColor.CYAN + getSelectMenuHandler().getTotal() + ConsoleColor.RESET + " menus");
		Common.log("Loaded a total of " + ConsoleColor.CYAN + getButtonHandler().getTotal() + ConsoleColor.RESET + " buttons");
		Common.log("Loaded a total of " + ConsoleColor.CYAN + getModalHandler().getTotal() + ConsoleColor.RESET + " modals");
	}

	/**
	 * Registration of the bot itself
	 *
	 * @param token    = The token of the bot
	 * @param activity = The activity status of the bot
	 */
	private static void registerJda(final String token, final Activity activity) {
		Debugger.debug("Startup", "Registering JDA! registerJda();189");
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

			// Set self to the bot
			self = jda.getSelfUser();
		} catch (LoginException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void stop() {
		final Thread t = new Thread(() -> {
			System.out.println(ConsoleColor.RED + "Stopping JDA..." + ConsoleColor.RESET);
			jda.shutdown();

			int jdaShutdownTimeout = 0;
			while (jda.getStatus() != JDA.Status.SHUTDOWN) {
				if (jdaShutdownTimeout > 15) {
					System.out.println(ConsoleColor.RED + "Killing JDA..." + ConsoleColor.RESET);
					jda.shutdownNow();
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				jdaShutdownTimeout++;
			}

			System.out.println(ConsoleColor.RED + "Stopping cron jobs..." + ConsoleColor.RESET);
			cronHandler.stop();

			int cronShutdownTimeout = 0;
			while (!cronHandler.isShutdown()) {
				if (cronShutdownTimeout > 15) {
					System.out.println(ConsoleColor.RED + "Killing cron jobs..." + ConsoleColor.RESET);
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				cronShutdownTimeout++;
			}

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(ConsoleColor.RED + "Stopping bot..." + ConsoleColor.RESET);
			System.exit(0);
		});
		t.start();

		onBotStop();
	}

	// ----------------------------------------------------------------------------------------
	// Registration methods   <- Used to register your commands, buttons, menus, modals and console commands
	// ----------------------------------------------------------------------------------------

	/**
	 * Register a new command in the slash command list
	 *
	 * @param command SimpleSlashCommand
	 */
	protected final void registerCommand(final SimpleSlashCommand command) {
		getSlashCommandHandler().addCommand(command);
	}

	/**
	 * Register new commands in the slash command list at once
	 *
	 * @param commands SimpleSlashCommands
	 */
	protected final void registerCommands(final SimpleSlashCommand... commands) {
		for (final SimpleSlashCommand command : commands) {
			getSlashCommandHandler().addCommand(command);
		}
	}

	/**
	 * Register a new button in the button list
	 *
	 * @param button SimpleButton
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerButton(final SimpleButton button) {
		getButtonHandler().addButtonListener(button);
	}

	/**
	 * Register new buttons in the button list at once
	 *
	 * @param buttons SimpleButton
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerButtons(final SimpleButton... buttons) {
		for (final SimpleButton button : buttons) {
			getButtonHandler().addButtonListener(button);
		}
	}

	/**
	 * Register a new menu in the menu list
	 *
	 * @param menu SimpleSelectMenu
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerMenu(final SimpleSelectMenu menu) {
		getSelectMenuHandler().addMenuListener(menu);
	}

	/**
	 * Register new menus in the menu list at once
	 *
	 * @param menus SimpleSelectMenu
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerMenus(final SimpleSelectMenu... menus) {
		for (final SimpleSelectMenu menu : menus) {
			getSelectMenuHandler().addMenuListener(menu);
		}
	}

	/**
	 * Register a new modal in the modal list
	 *
	 * @param modal SimpleModal
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerModal(final SimpleModal modal) {
		getModalHandler().addModalListener(modal);
	}

	/**
	 * Register new modals in the modal list at once
	 *
	 * @param modals SimpleModal
	 * @deprecated Use build() instead
	 */
	@Deprecated(since = "2.0.0", forRemoval = true)
	protected final void registerModals(final SimpleModal... modals) {
		for (final SimpleModal modal : modals) {
			getModalHandler().addModalListener(modal);
		}
	}

	/**
	 * Register a new console command in the console command list
	 *
	 * @param command SimpleConsoleCommand
	 */
	protected final void registerConsoleCommand(final SimpleConsoleCommand command) {
		getConsoleCommandHandler().addCommand(command);
	}

	/**
	 * Register new console commands in the console commands list
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
	// Main setters
	// ----------------------------------------------------------------------------------------

	/**
	 * Set the bot as status enabled
	 */
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
	 * Retrieve the select menu handler
	 *
	 * @return SelectMenu handler
	 */
	private static SelectMenuHandler getSelectMenuHandler() {
		return menuHandler;
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
	 * Get a modal from the modal from the modal handler
	 *
	 * @param button_id The ID of the button
	 * @return SimpleButton
	 */
	public static SimpleButton getButton(final String button_id) {
		return getButtonHandler().getButton(button_id);
	}

	public static SimpleSelectMenu getSelectMenu(final String menu_id) {
		return getSelectMenuHandler().getMenu(menu_id);
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

	// TODO: Fix this so it works properly with a @Override

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
