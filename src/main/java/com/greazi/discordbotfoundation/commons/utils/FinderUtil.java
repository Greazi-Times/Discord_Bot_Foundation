package com.greazi.discordbotfoundation.commons.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A series of query based utilities for finding entities, either globally across all accessible
 * {@link net.dv8tion.jda.api.entities.Guild Guild}s, or locally to a specified Guild.
 *
 * <p>All methods use a similar priority hierarchy and return an immutable {@link List List} based on the results.
 * <br>The hierarchy is as follows:
 * <ul>
 *     <li>Special Cases: Specifics of these are described per individual method documentation.
 *     <br>Note that successful results from these are typically
 *     {@link Collections#singletonList(Object) a singleton list}.</li>
 *     <li>Direct ID: Query is a number with 17 or more digits, resembling an
 *     {@link net.dv8tion.jda.api.entities.ISnowflake Snowflake} ID.</li>
 *     <li>Exact Match: Query provided is an exact match (case sensitive and complete) to one or more entities.</li>
 *     <li>Wrong Case: Query provided is a case-insensitive, but exact, match to the entirety of one or more entities.</li>
 *     <li>Starting With: Query provided is an case-insensitive match to the beginning of one or more entities.</li>
 *     <li>Contains: Query provided is a case-insensitive match to a part of one or more entities.</li>
 * </ul>
 * All queries return the highest List in this hierarchy that contains one or more entities, and only of these
 * kind of results (IE: the "exact" list will never contain any results from a successful "starting with" match,
 * unless by chance they could technically be the same result).<p>
 *
 * <b>Shard Manager Usage</b>
 *
 * <br>Methods that query an instance of {@link net.dv8tion.jda.api.JDA JDA} always have two implementations:
 * <ul>
 *     <li><b>Global:</b> Queries a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} if one
 *                        is available, or JDA if one is not.</li>
 *
 *     <li><b>Shard:</b>  Always queries the provided instance, and never a ShardManager, even if one
 *                        is available.</li>
 * </ul>
 *
 * <p>Many of these utilities were inspired by and ported to JDA 3.X from
 * <a href="https://github.com/jagrosh/Spectra/blob/master/src/spectra/utils/FinderUtil.java">Spectra's FinderUtil</a>
 * originally written by <a href="https://github.com/jagrosh/">jagrosh</a> in 2.X.
 */
@SuppressWarnings("Duplicates")
public final class FinderUtil {
	public final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}"); // ID
	public final static Pattern FULL_USER_REF = Pattern.compile("(\\S.{0,30}\\S)\\s*#(\\d{4})"); // $1 -> username, $2 -> discriminator
	public final static Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>"); // $1 -> ID
	public final static Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d{17,20})>"); // $1 -> ID
	public final static Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>"); // $1 -> ID
	public final static Pattern EMOTE_MENTION = Pattern.compile("<:(.{2,32}):(\\d{17,20})>");

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for {@link net.dv8tion.jda.api.entities.User User}s.<p>
	 * <p>
	 * If a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available this will query across that
	 * instead of the JDA instance.
	 *
	 * <p>The following special cases are applied in order of listing before the standard search is done:
	 * <ul>
	 *     <li>User Mention: Query provided matches an @user mention (more specifically {@literal <@userID>}).</li>
	 *     <li>Full User Reference: Query provided matches a full Username#XXXX reference.
	 *     <br><b>NOTE:</b> this can return a list with more than one entity.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Users found by the query from the provided JDA instance.
	 */
	public static List<User> findUsers(final String query, final JDA jda) {
		return jdaUserSearch(query, jda, true);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for {@link net.dv8tion.jda.api.entities.User User}s.<p>
	 * <p>
	 * This only queries the instance of JDA, regardless of whether or not a
	 * {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available.
	 *
	 * <p>The following special cases are applied in order of listing before the standard search is done:
	 * <ul>
	 *     <li>User Mention: Query provided matches an @user mention (more specifically {@literal <@userID>}).</li>
	 *     <li>Full User Reference: Query provided matches a full Username#XXXX reference.
	 *     <br><b>NOTE:</b> this can return a list with more than one entity.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Users found by the query from the provided JDA instance.
	 */
	public static List<User> findShardUsers(final String query, final JDA jda) {
		return jdaUserSearch(query, jda, false);
	}

	private static List<User> jdaUserSearch(final String query, final JDA jda, final boolean useShardManager) {
		final Matcher userMention = USER_MENTION.matcher(query);
		final Matcher fullRefMatch = FULL_USER_REF.matcher(query);

		final ShardManager manager = useShardManager ? jda.getShardManager() : null;

		if (userMention.matches()) {
			final User user = manager != null ? manager.getUserById(userMention.group(1)) :
					jda.getUserById(userMention.group(1));
			if (user != null)
				return Collections.singletonList(user);
		} else if (fullRefMatch.matches()) {
			final String lowerName = fullRefMatch.group(1).toLowerCase(Locale.ROOT);
			final String discrim = fullRefMatch.group(2);
			final List<User> users = (manager != null ? manager.getUserCache() : jda.getUserCache())
					.stream().filter(user -> user.getName().toLowerCase(Locale.ROOT).equals(lowerName)
							&& user.getDiscriminator().equals(discrim))
					.collect(Collectors.toList());
			if (!users.isEmpty())
				return users;
		} else if (DISCORD_ID.matcher(query).matches()) {
			final User user = (manager != null ? manager.getUserById(query) : jda.getUserById(query));
			if (user != null)
				return Collections.singletonList(user);
		}

		final ArrayList<User> exact = new ArrayList<>();
		final ArrayList<User> wrongcase = new ArrayList<>();
		final ArrayList<User> startswith = new ArrayList<>();
		final ArrayList<User> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		(manager != null ? manager.getUserCache() : jda.getUserCache()).forEach(user -> {
			final String name = user.getName();
			if (name.equals(query))
				exact.add(user);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(user);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(user);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(user);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for a banned {@link net.dv8tion.jda.api.entities.User
	 * User}.
	 *
	 * <p>The following special cases are applied in order of listing before the standard search is done:
	 * <ul>
	 *     <li>User Mention: Query provided matches an @user mention (more specifically {@literal <@userID>}).</li>
	 *     <li>Full User Reference: Query provided matches a full Username#XXXX reference.
	 *     <br><b>NOTE:</b> this can return a list with more than one entity.</li>
	 * </ul>
	 *
	 * <p><b>WARNING</b>
	 *
	 * <p>Unlike the other finder methods, this one has two very unique features that set it apart from the rest:
	 * <ul>
	 *     <li><b>1)</b> In order to get a list of bans that is usable, this method initial retrieves it by usage of
	 *     {@link net.dv8tion.jda.api.requests.RestAction#complete() Guild#getBans().complete()}. Because of this,
	 *     as would be the same expected effect from the other utility methods, this will block the thread it is called
	 *     in. The difference, however, comes in that this method may have slight variations in return speed, especially
	 *     when put under higher usage over a shorter period of time.</li>
	 *     <li><b>2) This method can return {@code null}</b> if and only if an {@link Exception Exception} is
	 *     thrown while initially getting banned Users via {@link net.dv8tion.jda.api.entities.Guild#retrieveBanList()
	 *     Guild#getBanList()}.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search for banned Users from
	 * @return A possibly-empty {@link List List} of Users found by the query from the provided JDA instance,
	 * or {@code null} if an {@link Exception Exception} is thrown while initially getting banned Users.
	 * @see net.dv8tion.jda.api.entities.Guild#retrieveBanList() Guild#getBanList()
	 */
	public static List<User> findBannedUsers(String query, final Guild guild) {
		final List<User> bans;
		try {
			bans = guild.retrieveBanList().complete().stream()
					.map(Guild.Ban::getUser)
					.collect(Collectors.toList());
		} catch (final Exception e) {
			return null;
		}
		String discrim = null;
		final Matcher userMention = USER_MENTION.matcher(query);
		if (userMention.matches()) {
			final String id = userMention.group(1);
			final User user = guild.getJDA().getUserById(id);
			if (user != null && bans.contains(user))
				return Collections.singletonList(user);
			for (final User u : bans)
				if (u.getId().equals(id))
					return Collections.singletonList(u);
		} else if (FULL_USER_REF.matcher(query).matches()) {
			discrim = query.substring(query.length() - 4);
			query = query.substring(0, query.length() - 5).trim();
		} else if (DISCORD_ID.matcher(query).matches()) {
			final User user = guild.getJDA().getUserById(query);
			if (user != null && bans.contains(user))
				return Collections.singletonList(user);
			for (final User u : bans)
				if (u.getId().equals(query))
					return Collections.singletonList(u);
		}
		final ArrayList<User> exact = new ArrayList<>();
		final ArrayList<User> wrongcase = new ArrayList<>();
		final ArrayList<User> startswith = new ArrayList<>();
		final ArrayList<User> contains = new ArrayList<>();
		final String lowerQuery = query.toLowerCase(Locale.ROOT);
		for (final User u : bans) {
			// If a discrim is specified then we skip all users without it.
			if (discrim != null && !u.getDiscriminator().equals(discrim))
				continue;

			if (u.getName().equals(query))
				exact.add(u);
			else if (exact.isEmpty() && u.getName().equalsIgnoreCase(query))
				wrongcase.add(u);
			else if (wrongcase.isEmpty() && u.getName().toLowerCase(Locale.ROOT).startsWith(lowerQuery))
				startswith.add(u);
			else if (startswith.isEmpty() && u.getName().toLowerCase(Locale.ROOT).contains(lowerQuery))
				contains.add(u);
		}
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for {@link net.dv8tion.jda.api.entities.Member Member}s.
	 *
	 * <p>The following special cases are applied in order of listing before the standard search is done:
	 * <ul>
	 *     <li>User Mention: Query provided matches an @user mention (more specifically {@literal <@userID> or <@!userID>}).</li>
	 *     <li>Full User Reference: Query provided matches a full Username#XXXX reference.
	 *     <br><b>NOTE:</b> this can return a list with more than one entity.</li>
	 * </ul>
	 *
	 * <p>Unlike {@link FinderUtil#findUsers(String, JDA) FinderUtil.findUsers(String, JDA)},
	 * this method queries based on two different names: user name and effective name (excluding special cases in which it
	 * queries solely based on user name).
	 * <br>Each standard check looks at the user name, then the member name, and if either one's criteria is met the Member
	 * is added to the returned list. This is important to note, because the returned list may contain exact matches for
	 * User's name as well as exact matches for a Member's effective name, with nothing guaranteeing the returns will be
	 * exclusively containing matches for one or the other.
	 * <br>Information on effective name can be found in {@link net.dv8tion.jda.api.entities.Member#getEffectiveName()
	 * Member#getEffectiveName()}.
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly empty {@link List List} of Members found by the query from the provided Guild.
	 */
	public static List<Member> findMembers(final String query, final Guild guild) {
		final Matcher userMention = USER_MENTION.matcher(query);
		final Matcher fullRefMatch = FULL_USER_REF.matcher(query);
		if (userMention.matches()) {
			final Member member = guild.getMemberById(userMention.group(1));
			if (member != null)
				return Collections.singletonList(member);
		} else if (fullRefMatch.matches()) {
			final String lowerName = fullRefMatch.group(1).toLowerCase(Locale.ROOT);
			final String discrim = fullRefMatch.group(2);
			final List<Member> members = guild.getMemberCache().stream()
					.filter(member -> member.getUser().getName().toLowerCase(Locale.ROOT).equals(lowerName)
							&& member.getUser().getDiscriminator().equals(discrim))
					.collect(Collectors.toList());
			if (!members.isEmpty())
				return members;
		} else if (DISCORD_ID.matcher(query).matches()) {
			final Member member = guild.getMemberById(query);
			if (member != null)
				return Collections.singletonList(member);
		}
		final ArrayList<Member> exact = new ArrayList<>();
		final ArrayList<Member> wrongcase = new ArrayList<>();
		final ArrayList<Member> startswith = new ArrayList<>();
		final ArrayList<Member> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		guild.getMemberCache().forEach(member -> {
			final String name = member.getUser().getName();
			final String effName = member.getEffectiveName();
			if (name.equals(query) || effName.equals(query))
				exact.add(member);
			else if ((name.equalsIgnoreCase(query) || effName.equalsIgnoreCase(query)) && exact.isEmpty())
				wrongcase.add(member);
			else if ((name.toLowerCase(Locale.ROOT).startsWith(lowerquery) || effName.toLowerCase(Locale.ROOT).startsWith(lowerquery)) && wrongcase.isEmpty())
				startswith.add(member);
			else if ((name.toLowerCase(Locale.ROOT).contains(lowerquery) || effName.toLowerCase(Locale.ROOT).contains(lowerquery)) && startswith.isEmpty())
				contains.add(member);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}s.<p>
	 * <p>
	 * If a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available this will query across that
	 * instead of the JDA instance.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Channel Mention: Query provided matches a #channel mention (more specifically {@literal <#channelID>})</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of TextChannels found by the query from the provided JDA instance.
	 */
	public static List<TextChannel> findTextChannels(final String query, final JDA jda) {
		return jdaTextChannelSearch(query, jda, true);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}s.<p>
	 * <p>
	 * This only queries the instance of JDA, regardless of whether or not a
	 * {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Channel Mention: Query provided matches a #channel mention (more specifically {@literal <#channelID>})</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of TextChannels found by the query from the provided JDA instance.
	 */
	public static List<TextChannel> findShardTextChannels(final String query, final JDA jda) {
		return jdaTextChannelSearch(query, jda, false);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for
	 * {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}s.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Channel Mention: Query provided matches a #channel mention (more specifically {@literal <#channelID>})</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly-empty {@link List List} of TextChannels found by the query from the provided Guild.
	 */
	public static List<TextChannel> findTextChannels(final String query, final Guild guild) {
		final Matcher channelMention = CHANNEL_MENTION.matcher(query);
		if (channelMention.matches()) {
			final TextChannel tc = guild.getTextChannelById(channelMention.group(1));
			if (tc != null)
				return Collections.singletonList(tc);
		} else if (DISCORD_ID.matcher(query).matches()) {
			final TextChannel tc = guild.getTextChannelById(query);
			if (tc != null)
				return Collections.singletonList(tc);
		}

		return genericTextChannelSearch(query, guild.getTextChannelCache());
	}

	private static List<TextChannel> jdaTextChannelSearch(final String query, final JDA jda, final boolean useShardManager) {
		final Matcher channelMention = CHANNEL_MENTION.matcher(query);

		final ShardManager manager = useShardManager ? jda.getShardManager() : null;

		if (channelMention.matches()) {
			final TextChannel tc = manager != null ? manager.getTextChannelById(channelMention.group(1)) :
					jda.getTextChannelById(channelMention.group(1));
			if (tc != null)
				return Collections.singletonList(tc);
		} else if (DISCORD_ID.matcher(query).matches()) {
			final TextChannel tc = manager != null ? manager.getTextChannelById(query) : jda.getTextChannelById(query);
			if (tc != null)
				return Collections.singletonList(tc);
		}

		return genericTextChannelSearch(query, manager != null ? manager.getTextChannelCache() : jda.getTextChannelCache());
	}

	private static List<TextChannel> genericTextChannelSearch(final String query, final SnowflakeCacheView<TextChannel> cache) {
		final ArrayList<TextChannel> exact = new ArrayList<>();
		final ArrayList<TextChannel> wrongcase = new ArrayList<>();
		final ArrayList<TextChannel> startswith = new ArrayList<>();
		final ArrayList<TextChannel> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		cache.forEach((tc) -> {
			final String name = tc.getName();
			if (name.equals(query))
				exact.add(tc);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(tc);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(tc);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(tc);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}s.<p>
	 * <p>
	 * If a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available this will query across that
	 * instead of the JDA instance.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of VoiceChannels found by the query from the provided JDA instance.
	 */
	public static List<VoiceChannel> findVoiceChannels(final String query, final JDA jda) {
		return jdaVoiceChannelSearch(query, jda, true);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}s.<p>
	 * <p>
	 * This only queries the instance of JDA, regardless of whether or not a
	 * {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of VoiceChannels found by the query from the provided JDA instance.
	 */
	public static List<VoiceChannel> findShardVoiceChannels(final String query, final JDA jda) {
		return jdaVoiceChannelSearch(query, jda, false);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for
	 * {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}s.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly-empty {@link List List} of VoiceChannels found by the query from the provided Guild.
	 */
	public static List<VoiceChannel> findVoiceChannels(final String query, final Guild guild) {
		if (DISCORD_ID.matcher(query).matches()) {
			final VoiceChannel vc = guild.getVoiceChannelById(query);
			if (vc != null)
				return Collections.singletonList(vc);
		}
		return genericVoiceChannelSearch(query, guild.getVoiceChannelCache());
	}

	private static List<VoiceChannel> jdaVoiceChannelSearch(final String query, final JDA jda, final boolean useShardManager) {
		final ShardManager manager = useShardManager ? jda.getShardManager() : null;

		if (DISCORD_ID.matcher(query).matches()) {
			final VoiceChannel vc = manager != null ? manager.getVoiceChannelById(query) : jda.getVoiceChannelById(query);
			if (vc != null)
				return Collections.singletonList(vc);
		}

		return genericVoiceChannelSearch(query, manager != null ? manager.getVoiceChannelCache() : jda.getVoiceChannelCache());
	}

	private static List<VoiceChannel> genericVoiceChannelSearch(final String query, final SnowflakeCacheView<VoiceChannel> cache) {
		final ArrayList<VoiceChannel> exact = new ArrayList<>();
		final ArrayList<VoiceChannel> wrongcase = new ArrayList<>();
		final ArrayList<VoiceChannel> startswith = new ArrayList<>();
		final ArrayList<VoiceChannel> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		cache.forEach((vc) -> {
			final String name = vc.getName();
			if (name.equals(query))
				exact.add(vc);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(vc);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(vc);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(vc);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.Category Categories}.<p>
	 * <p>
	 * If a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available this will query across that
	 * instead of the JDA instance.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Categories found by the query from the provided JDA instance.
	 */
	public static List<Category> findCategories(final String query, final JDA jda) {
		return jdaCategorySearch(query, jda, true);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.Category Categories}.<p>
	 * <p>
	 * This only queries the instance of JDA, regardless of whether or not a
	 * {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Categories found by the query from the provided JDA instance.
	 */
	public static List<Category> findShardCategories(final String query, final JDA jda) {
		return jdaCategorySearch(query, jda, false);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for
	 * {@link net.dv8tion.jda.api.entities.Category Categories}.
	 *
	 * <p>The standard search does not follow any special cases.
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly-empty {@link List List} of Categories found by the query from the provided Guild.
	 */
	public static List<Category> findCategories(final String query, final Guild guild) {
		if (DISCORD_ID.matcher(query).matches()) {
			final Category cat = guild.getCategoryById(query);
			if (cat != null)
				return Collections.singletonList(cat);
		}

		return genericCategorySearch(query, guild.getCategoryCache());
	}

	private static List<Category> jdaCategorySearch(final String query, final JDA jda, final boolean useShardManager) {
		final ShardManager manager = useShardManager ? jda.getShardManager() : null;

		if (DISCORD_ID.matcher(query).matches()) {
			final Category cat = manager != null ? manager.getCategoryById(query) : jda.getCategoryById(query);
			if (cat != null)
				return Collections.singletonList(cat);
		}

		return genericCategorySearch(query, jda.getCategoryCache());
	}

	private static List<Category> genericCategorySearch(final String query, final SnowflakeCacheView<Category> cache) {
		final ArrayList<Category> exact = new ArrayList<>();
		final ArrayList<Category> wrongcase = new ArrayList<>();
		final ArrayList<Category> startswith = new ArrayList<>();
		final ArrayList<Category> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		cache.forEach(cat -> {
			final String name = cat.getName();
			if (name.equals(query))
				exact.add(cat);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(cat);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(cat);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(cat);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for {@link net.dv8tion.jda.api.entities.Role Role}s.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Role Mention: Query provided matches a @role mention (more specifically {@literal <@&roleID>})</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly-empty {@link List List} of Roles found by the query from the provided Guild.
	 */
	public static List<Role> findRoles(final String query, final Guild guild) {
		final Matcher roleMention = ROLE_MENTION.matcher(query);
		if (roleMention.matches()) {
			final Role role = guild.getRoleById(roleMention.group(1));
			if (role != null)
				return Collections.singletonList(role);
		} else if (DISCORD_ID.matcher(query).matches()) {
			final Role role = guild.getRoleById(query);
			if (role != null)
				return Collections.singletonList(role);
		}
		final ArrayList<Role> exact = new ArrayList<>();
		final ArrayList<Role> wrongcase = new ArrayList<>();
		final ArrayList<Role> startswith = new ArrayList<>();
		final ArrayList<Role> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		guild.getRoleCache().forEach((role) -> {
			final String name = role.getName();
			if (name.equals(query))
				exact.add(role);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(role);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(role);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(role);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.emoji.Emoji Emoji}s.<p>
	 * <p>
	 * If a {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available this will query across that
	 * instead of the JDA instance.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Emote Mention: Query provided matches a :emote: mention (more specifically {@literal <:emoteName:emoteID>}).
	 *     <br>Note: This only returns here if the emote is <b>valid</b>. Validity being the ID retrieves a non-null
	 *     Emote and that the {@link net.dv8tion.jda.api.entities.emoji.Emoji#getName() name} of the Emote is equal to the
	 *     name found in the query.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Emotes found by the query from the provided JDA instance.
	 */
	public static List<RichCustomEmoji> findEmotes(final String query, final JDA jda) {
		return jdaFindEmotes(query, jda, true);
	}

	/**
	 * Queries a provided instance of {@link net.dv8tion.jda.api.JDA JDA} for
	 * {@link net.dv8tion.jda.api.entities.emoji.Emoji Emoji}s.<p>
	 * <p>
	 * This only queries the instance of JDA, regardless of whether or not a
	 * {@link net.dv8tion.jda.api.sharding.ShardManager ShardManager} is available.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Emote Mention: Query provided matches a :emote: mention (more specifically {@literal <:emoteName:emoteID>}).
	 *     <br>Note: This only returns here if the emote is <b>valid</b>. Validity being the ID retrieves a non-null
	 *     Emote and that the {@link net.dv8tion.jda.api.entities.emoji.Emoji#getName() name} of the Emote is equal to the
	 *     name found in the query.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param jda   The instance of JDA to search from
	 * @return A possibly-empty {@link List List} of Emotes found by the query from the provided JDA instance.
	 */
	public static List<RichCustomEmoji> findShardEmotes(final String query, final JDA jda) {
		return jdaFindEmotes(query, jda, false);
	}

	/**
	 * Queries a provided {@link net.dv8tion.jda.api.entities.Guild Guild} for
	 * {@link net.dv8tion.jda.api.entities.emoji.Emoji Emoij}s.
	 *
	 * <p>The following special case is applied before the standard search is done:
	 * <ul>
	 *     <li>Emote Mention: Query provided matches a :emote: mention (more specifically {@literal <:emoteName:emoteID>}).
	 *     <br>Note: This only returns here if the emote is <b>valid</b>. Validity being the ID retrieves a non-null
	 *     Emote and that the {@link net.dv8tion.jda.api.entities.emoji.Emoji#getName() name} of the Emote is equal to the
	 *     name found in the query.</li>
	 * </ul>
	 *
	 * @param query The String query to search by
	 * @param guild The Guild to search from
	 * @return A possibly-empty {@link List List} of Emotes found by the query from the provided Guild.
	 */
	public static List<RichCustomEmoji> findEmotes(final String query, final Guild guild) {
		final Matcher mentionMatcher = EMOTE_MENTION.matcher(query);
		if (DISCORD_ID.matcher(query).matches()) {
			final RichCustomEmoji emote = guild.getEmojiById(query);
			if (emote != null)
				return Collections.singletonList(emote);
		} else if (mentionMatcher.matches()) {
			final String emoteName = mentionMatcher.group(1);
			final String emoteId = mentionMatcher.group(2);
			final RichCustomEmoji emote = guild.getEmojiById(emoteId);
			if (emote != null && emote.getName().equals(emoteName))
				return Collections.singletonList(emote);
		}

		return genericEmoteSearch(query, guild.getEmojiCache());
	}

	private static List<RichCustomEmoji> jdaFindEmotes(final String query, final JDA jda, final boolean useShardManager) {
		final Matcher mentionMatcher = EMOTE_MENTION.matcher(query);

		final ShardManager manager = useShardManager ? jda.getShardManager() : null;

		if (DISCORD_ID.matcher(query).matches()) {
			final RichCustomEmoji emote = manager != null ? manager.getEmojiById(query) : jda.getEmojiById(query);
			if (emote != null)
				return Collections.singletonList(emote);
		} else if (mentionMatcher.matches()) {
			final String emoteName = mentionMatcher.group(1);
			final String emoteId = mentionMatcher.group(2);
			final RichCustomEmoji emote = manager != null ? manager.getEmojiById(emoteId) : jda.getEmojiById(emoteId);
			if (emote != null && emote.getName().equals(emoteName))
				return Collections.singletonList(emote);
		}

		return genericEmoteSearch(query, jda.getEmojiCache());
	}

	private static List<RichCustomEmoji> genericEmoteSearch(final String query, final SnowflakeCacheView<RichCustomEmoji> cache) {
		final ArrayList<RichCustomEmoji> exact = new ArrayList<>();
		final ArrayList<RichCustomEmoji> wrongcase = new ArrayList<>();
		final ArrayList<RichCustomEmoji> startswith = new ArrayList<>();
		final ArrayList<RichCustomEmoji> contains = new ArrayList<>();
		final String lowerquery = query.toLowerCase(Locale.ROOT);
		cache.forEach(emote -> {
			final String name = emote.getName();
			if (name.equals(query))
				exact.add(emote);
			else if (name.equalsIgnoreCase(query) && exact.isEmpty())
				wrongcase.add(emote);
			else if (name.toLowerCase(Locale.ROOT).startsWith(lowerquery) && wrongcase.isEmpty())
				startswith.add(emote);
			else if (name.toLowerCase(Locale.ROOT).contains(lowerquery) && startswith.isEmpty())
				contains.add(emote);
		});
		if (!exact.isEmpty())
			return Collections.unmodifiableList(exact);
		if (!wrongcase.isEmpty())
			return Collections.unmodifiableList(wrongcase);
		if (!startswith.isEmpty())
			return Collections.unmodifiableList(startswith);
		return Collections.unmodifiableList(contains);
	}

	// Prevent instantiation
	private FinderUtil() {
	}
}
