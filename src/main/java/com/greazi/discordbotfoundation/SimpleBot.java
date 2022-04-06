package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.command.SimpleSlashCommand;
import com.greazi.discordbotfoundation.command.SlashCommandHandler;
import com.greazi.discordbotfoundation.command.general.PingCommand;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.color.Color;
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

// TODO Check if all comments are correct

/**
 * A basic discord bot that represents the discord bot library
 */
public abstract class SimpleBot {

	// ----------------------------------------------------------------------------------------
	// Static
	// ----------------------------------------------------------------------------------------

	private static volatile SimpleBot instance;
	public static JDA jda;

	private static Guild guild;
	private static SelfUser self;
	private static SlashCommandHandler slashCommandHandler;

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
	public static final boolean hasInstance() {
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
	public SimpleBot(){
		// A debugger that is used to debug the startup of the bot
		Debugger.debug("Startup", "Starting the bot! SimpleBot();104");

		// Set the instance of the bot
		instance = this;

		// Load methods that need to be loaded before the bot starts
		onBotLoad();

		// Register the JDA
		registerJda(SimpleSettings.getInstance().getToken(), SimpleSettings.getInstance().getActivity());

		// TODO Redesign everything from this part on
		// Load methods that need to be loaded after the bot starts
		onPreStart();
	}

	/**
	 * The pre start of the bot. Register the bot and do some simple checks
	 */
	public final void onPreStart() {
		Debugger.debug("Startup", "Starting the bot! onPreStart();105");
		guild = jda.getGuildById(SimpleSettings.getInstance().getMainGuild());

		jda.addEventListener(new SimpleSlashCommand() {
			@Override
			protected void execute(SlashCommandInteractionEvent event) {

			}
		});
		slashCommandHandler = new SlashCommandHandler();

		onStartup();
	}

	public void onStartup() {
		Debugger.debug("Startup", "Starting the bot! onStartup();157");
		onBotStart();

		guild = jda.getGuildById(SimpleSettings.getInstance().getMainGuild());

		onReload();
	}

	public void onReload() {
		Debugger.debug("Startup", "Starting the bot! onReload();167");
		onReloadableStart();

		slashCommandHandler.registerCommands();

		// Load the static commands
		getSlashCommandHandler().addCommand(new PingCommand());


		Common.success("bot is ready");
		enabled = true;
	}

	/**
	 * Registration of the bot itself
	 *
	 * @param token = The token of the bot
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
		} catch(LoginException | InterruptedException ex) {
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
	 * This is invoked when you do `/reload`  TODO add a reload command link that runs all reload methods
	 */
	protected void onReloadableStart() {
		Debugger.debug("Startup", "Running onReloadableStart();246");

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

	public static JDA getJDA() {
		return jda;
	}

	public static SimpleBot getBot() {
		return instance;
	}

	public static Guild getGuild() {
		return guild;
	}

	public static SelfUser getSelf() {
		return self;
	}

	public static SlashCommandHandler getSlashCommandHandler() {
		return slashCommandHandler;
	}

	// ----------------------------------------------------------------------------------------
	// Additional features
	// ----------------------------------------------------------------------------------------

	public static String getVersion() {
		return "1.0.0";
	}

	public static String getName() {
		return SimpleSettings.getInstance().getName();
	}

	public String getDeveloper() {
		return "Greazi";
	}

	public String getEmbedAuthorImage() {
		return "https://i.imgur.com/ddzfapZ.png";
	}

	public String getLink() {
		return "https://greazi.com";
	}

	public boolean isEnabled() {
		return enabled;
	}
}
