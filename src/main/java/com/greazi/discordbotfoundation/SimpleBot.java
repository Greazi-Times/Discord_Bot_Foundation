package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.settings.SimpleYaml;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Objects;

/**
 * To-Do-List for the whole project.
 *
 * ** Priority
 * -- Can be done later
 */
// TODO Exception handler --
// TODO Debug system --
// TODO module system **
// TODO command system **
// TODO Proper settings system **
// TODO Channel Util
// TODO Roll Util
// TODO Embed Util **
// TODO Button Util **
// TODO Menu Util --
// TODO Reload handler --
// TODO MySQL base --
// TODO Common class
//      This will handle a basic logger and message sender as well as some basic stuff


/**
 * A basic discord bot that represents the discord bot library
 */
public abstract class SimpleBot {

	// ----------------------------------------------------------------------------------------
	// Static
	// ----------------------------------------------------------------------------------------

	private static volatile SimpleBot instance;

	public static JDA jda;

	private static final Logger log = LoggerFactory.getLogger(SimpleBot.class);

	/**
	 * Returns the instance of {@link SimpleBot}.
	 * <p>
	 * It is recommended to override this in your own {@link SimpleBot}
	 * implementation so you will get the instance of that, directly.
	 *
	 * @return this instance
	 */
	public static SimpleBot getInstance() {
		if (instance == null) {
			try {
				instance = SimpleBot.getInstance();

			} catch (final IllegalStateException ex) {
				ex.printStackTrace();
			}
			Objects.requireNonNull(instance, "Cannot get a new instance! Have you reloaded?");
		}

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

	/**
	 * For your convenience, event listeners and timed tasks may be set here to stop/unregister
	 * them automatically on reload
	 */
	/*private final Reloadables reloadables = new Reloadables();*/

	/** TODO check this part
	 * An internal flag to indicate whether we are calling the {@link #onReloadablesStart()}
	 * block. We register things using {@link #reloadables} during this block
	 */
	private boolean startingReloadables = false;

	// ----------------------------------------------------------------------------------------
	// Main methods
	// ----------------------------------------------------------------------------------------

	/**
	 * The main start system of the bot
	 * @param args
	 */
	public static void main(String[] args) {

		if (!SimpleYaml.settingsFileExists()) {
			// TODO Make a proper default file maker
			log.error("There was no settings file detected! Creating a new one....");
			log.error("");
			log.error("The Bot is shutting down due to not having a proper settings file.");
			log.error("Please SimpleSettingsure the file before starting the bot again!");
		}


		/*// Check if the SimpleSettings file is SimpleSettingsured correctly if not stop the startup
		if (!SimpleSettings.getInstance().isSimpleSettingsured()) {
			log.error("Your settings file is not SimpleSettingsured or misses some key values. Check the Settings.json");
			return;
		}*/


		onPreStart();
	}

	/**
	 * The pre start of the bot. Register the bot and do some simple checks
	 */
	public static final void onPreStart() {
		/*registerJda(SimpleSettings.getInstance().getToken(), SimpleSettings.getInstance().getActivity());*/
		onBotLoad();
	}

	public final void onStartup() {
		onReloadablesStart();
	}

	public final void onReload() {

		// disable modules and commands
		// start modules and commands
	}

	/**
	 * Registration of the bot itself
	 *
	 * @param token = The token of the bot
	 * @param activity = The activity status of the bot
	 */
	private static final void registerJda(String token, String activity) {
		try {
			jda = JDABuilder.createDefault(token)
					.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
					.setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
					.enableCache(CacheFlag.ONLINE_STATUS)
					.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setChunkingFilter(ChunkingFilter.ALL)
					.setActivity(Activity.watching(activity))
					.setEventManager(new AnnotatedEventManager())
					.build().awaitReady();
		} catch(LoginException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------------------
	// Delegate methods    <-- Methods that can be used to load your stuff
	// ----------------------------------------------------------------------------------------

	/**
	 * Called before the bot is started (Not recommended to use)
	 */
	protected static void onBotLoad() {
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
	 * This is invoked when you do `/reload`  TODO add a reload command link
	 */
	protected void onReloadablesStart() {
	}

	// ----------------------------------------------------------------------------------------
	// Additional features
	// ----------------------------------------------------------------------------------------

}
