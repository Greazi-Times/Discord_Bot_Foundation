package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.command.SimpleSlashCommand;
import com.greazi.discordbotfoundation.command.SlashCommandHandler;
import com.greazi.discordbotfoundation.managers.members.MemberStorage;
import com.greazi.discordbotfoundation.mysql.MySQL;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

/**
 * To-Do-List for the whole project.
 *
 * ** Priority
 * -- Can be done later
 */
// TODO Exception handler --
// TODO Debug system --
// TODO module system **
// TODO Proper settings system **
// TODO Channel Util
// TODO Roll Util
// TODO Button Util
// TODO Menu Util --
// TODO Reload handler --
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
	private static MySQL mySQL;
	private static MemberStorage memberStorage;
	private static SlashCommandHandler slashCommandHandler;

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

	/**
	 * For your convenience, event listeners and timed tasks may be set here to stop/unregister
	 * them automatically on reload
	 */
	/*private final Reloadables reloadables = new Reloadables();*/

	/** TODO check this part
	 * An internal flag to indicate whether we are calling the {@link #onReloadableStart()}
	 * block. We register things using {@link #} <-- reloadable during this block
	 */
	private boolean startingReloadables = false;

	// ----------------------------------------------------------------------------------------
	// Main methods
	// ----------------------------------------------------------------------------------------

	public SimpleBot(){
		// Check if the settings file is configured
		if(SimpleSettings.getInstance().isSettingsConfigured()) {
			Common.warning("The settings file hasn't been configured. Stopping the bot now!");
			return;
		}

		instance = this;
		registerJda(SimpleSettings.getInstance().getToken(), SimpleSettings.getInstance().getActivity());
		onPreStart();
	}

	/**
	 * The pre start of the bot. Register the bot and do some simple checks
	 */
	public final void onPreStart() {
		guild = jda.getGuildById(SimpleSettings.getInstance().getMainGuild());

		SimpleSettings simpleSettings = SimpleSettings.getInstance();
		if (simpleSettings.isMysqlEnabled()){
			Common.log("Enabling Mysql");
			try{
				mySQL = new MySQL(
						simpleSettings.getMySqlHost(),
						simpleSettings.getMySqlPort(),
						simpleSettings.getMySqlUsername(),
						simpleSettings.getMySqlPassword(),
						simpleSettings.getMySqlDatabase()
				);

				onBotLoad();

			}catch (Exception e){
				Common.log("Error enabling mysql: "+e.getMessage());
				e.printStackTrace();
			}


			Common.log("Mysql Enabled");
			if (mySQL.isConnected() && simpleSettings.isStoreMembersEnabled()){
				Common.log("Enabling MemberStorage");
				memberStorage = new MemberStorage();
				Common.log("MemberStorage Enabled");
			}
		}

		/*new SimpleSlashCommand() {
			@Override
			protected void execute(SlashCommandInteractionEvent event) {

			}
		};*/

		Common.log("Running onPreStart()");

		onStartup();
	}

	public void onStartup() {
		Common.log("Running onStartup()");
		onBotStart();

		guild = jda.getGuildById(SimpleSettings.getInstance().getMainGuild());

		slashCommandHandler = new SlashCommandHandler();

		onReloadableStart();
		onReload();
	}

	public void onReload() {
		onReloadableStart();

		//slashCommandHandler.registerCommands();
	}

	/**
	 * Registration of the bot itself
	 *
	 * @param token = The token of the bot
	 * @param activity = The activity status of the bot
	 */
	private static void registerJda(String token, String activity) {
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
		} catch(LoginException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public static MemberStorage getMemberStorage() {
		if (!SimpleSettings.getInstance().isStoreMembersEnabled()){
			Common.warning("Trying to get member storage while it is not enabled");
		}
		return memberStorage;
	}

	// ----------------------------------------------------------------------------------------
	// Delegate methods    <-- Methods that can be used to load your stuff
	// ----------------------------------------------------------------------------------------

	/**
	 * Called before the bot is started (Not recommended to use)
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
	 * This is invoked when you do `/reload`  TODO add a reload command link
	 */
	protected void onReloadableStart() {

	}

	// ----------------------------------------------------------------------------------------
	// Foundation handle settings
	// ----------------------------------------------------------------------------------------

	/**
	 * Should every message be divided by \n by an own method (tends to work more
	 * then split("\n"))
	 *
	 * @return
	 */
	public boolean enforeNewLine() {
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

	public static Member getSelf() {
		return self;
	}

	public static MySQL getMySQL() {
		return mySQL;
	}

	public static SlashCommandHandler getSlashCommandHandler() {
		return slashCommandHandler;
	}

	// ----------------------------------------------------------------------------------------
	// Additional features
	// ----------------------------------------------------------------------------------------

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
}
