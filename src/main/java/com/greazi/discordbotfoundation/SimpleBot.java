package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.module.ModulesManager;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import jdk.nashorn.internal.objects.annotations.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

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
// TODO Channel handler
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

	private static Guild guild;
	private static Member self;

	private static ModulesManager modulesManager;

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
	 * block. We register things using {@link #} <-- reloadable during this block
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
		registerJda(SimpleSettings.getInstance().getToken(), SimpleSettings.getInstance().getActivity());
		// TODO add methode to start the onPreStart() and on Startup()
	}

	/**
	 * The pre start of the bot. Register the bot and do some simple checks
	 */
	public final void onPreStart() {
		/*registerJda(SimpleSettings.getInstance().getToken(), SimpleSettings.getInstance().getActivity());*/



		onBotStart();
	}

	public  void onStartup() {
		onReloadablesStart();

		modulesManager = new ModulesManager();
		Common.log.info("Loading modules..");
		modulesManager.load();
		jda.addEventListener(modulesManager);
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

	public static JDA getJDA() {
		return jda;
	}

	public static SimpleBot getBot() {
		return instance;
	}

	public static Guild getGuild() {
		return guild;
	}

	public static Member getSelf() {
		return self;
	}

	// ----------------------------------------------------------------------------------------
	// Delegate methods    <-- Methods that can be used to load your stuff
	// ----------------------------------------------------------------------------------------

	/**
	 * Called before the bot is started (Not recommended to use)
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
	 * This is invoked when you do `/reload`  TODO add a reload command link
	 */
	protected void onReloadablesStart() {
	}

	// ----------------------------------------------------------------------------------------
	// Additional features
	// ----------------------------------------------------------------------------------------

	public String getName() {
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
}
