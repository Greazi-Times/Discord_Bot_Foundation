package com.greazi.discordbotfoundation;

import com.greazi.discordbotfoundation.command.CommandClient;
import com.greazi.discordbotfoundation.command.CommandClientBuilder;
import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.managers.members.MemberStorage;
import com.greazi.discordbotfoundation.mysql.MySQL;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.List;
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

	public CommandClientBuilder commandBuilder = new CommandClientBuilder();

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

	public SimpleBot(){
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
			Common.log.info("Enabling Mysql");
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
				Common.log.error("Error enabling mysql: "+e.getMessage());
				e.printStackTrace();
			}


			Common.log.info("Mysql Enabled");
			if (mySQL.isConnected() && simpleSettings.isStoreMembersEnabled()){
				Common.log.info("Enabling MemberStorage");
				memberStorage = new MemberStorage();
				Common.log.info("MemberStorage Enabled");
			}
		}

		Common.log.info("Running onPreStart()");

		onStartup();
	}

	public void onStartup() {
		Common.log.info("Running onStartup()");
		onBotStart();

		guild = jda.getGuildById(SimpleSettings.getInstance().getMainGuild());

		onReloadablesStart();
	}

	public void onReload() {
		// disable modules and commands
		// start modules and commands
	}

	public void loadCommands() {
		Common.log.info("Loading commands....");
		addCommands();
		Common.log.info("Added commands");
		CommandClient commandClient = commandBuilder.build();
		Common.log.info("Commands have been build");
		jda.addEventListener(commandClient);
		Common.log.info("JDA event listener has been added, DONE!");
	}

	public void loadModules() {

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
			Common.log.warn("Trying to get member storage while it is not enabled");
		}
		return memberStorage;
	}

	/*protected void registerCommand(Class commandClass) {
		Common.log.info("Running registerCommand(Class commandClass)");
		try {
			SimpleCommand createCommand = (SimpleCommand) commandClass.getDeclaredConstructor().newInstance();

			Common.log.info(createCommand.getName() + "Name");
			Common.log.info(createCommand.getHelp() + "Description");

			CommandData cmdData = new CommandData(createCommand.getName(), createCommand.getHelp() == null ? "No description set." : createCommand.getHelp())
					.addOptions(createCommand.getHelp())
					.setDefaultEnabled(createCommand.getUserPermissions().length == 0);

			getJDA().addEventListener(cmdData);
		} catch(NoSuchMethodException | SecurityException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
			exception.printStackTrace();
		}

	}*/

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

	protected void addCommands() {
	}

	protected void addModules() {
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
