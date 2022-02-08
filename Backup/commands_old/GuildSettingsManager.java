package com.greazi.discordbotfoundation.commands;

import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;

/**
 * An implementable frame for classes that handle Guild-Specific
 * settings.
 *
 * <p>Standard implementations should be able to simply provide a
 * type of {@link java.lang.Object Object} provided a non-null
 * {@link net.dv8tion.jda.api.entities.Guild Guild}. Further
 * customization of the implementation is allowed on the developer
 * end.
 *
 * @param  <T>
 *         The specific type of the settings object.

 *
 * @implNote
 *         Unless in the event of a major breaking change to
 *         JDA, there is no chance of implementations of this
 *         interface being required to implement additional
 *         methods.
 *         <br>If in the future it is decided to add a method
 *         to this interface, the method will have a default
 *         implementation that doesn't require developer additions.
 */
public interface GuildSettingsManager<T>
{
    /**
     * Gets settings for a specified {@link net.dv8tion.jda.api.entities.Guild Guild}
     * as an object of the specified type {@code T}, or {@code null} if the guild has no
     * settings.
     *
     * @param  guild
     *         The guild to get settings for.
     *
     * @return The settings object for the guild, or {@code null} if the guild has no settings.
     */
    @Nullable
    T getSettings(Guild guild);

    /**
     * Called when JDA has fired a {@link net.dv8tion.jda.api.events.ReadyEvent ReadyEvent}.
     *
     * <p>Developers should implement this method to create or initialize resources when starting their bot.
     */
    default void init() {}

    /**
     * Called when JDA has fired a {@link net.dv8tion.jda.api.events.ShutdownEvent ShutdownEvent}.
     *
     * <p>Developers should implement this method to free up or close resources when shutting their bot.
     */
    default void shutdown() {}
}
