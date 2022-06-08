package com.greazi.discordbotfoundation.commands;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A basic frame that is optionally implementable by objects returned from
 * {@link GuildSettingsManager#getSettings(net.dv8tion.jda.api.entities.Guild)
 * GuildSettingsManager#getSettings(Guild)}.
 *
 * <p>This interface allows the specification of any of the following functions:
 * <ul>
 *     <li>Guild Specific Prefixes (via {@link com.greazi.discordbotfoundation.commands.GuildSettingsProvider#getPrefixes()})</li>
 * </ul>
 *
 * Note that all of these functions are <b>OPTIONAL</b> to implement, and instructions
 * are available in method documentation on how to implement properly.
 * <br>Additionally, as stated before, the interface itself does not need to be
 * implemented for an object to be returned handled by a GuildSettingsManager.
 *
 * @since  2.0
 * @author Kaidan Gustave
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
public interface GuildSettingsProvider
{
    /**
     * Gets a {@link java.util.Collection Collection} of String prefixes available
     * for the Guild represented by this implementation.
     *
     * <p>An empty Collection or {@code null} may be returned to signify the Guild
     * doesn't have any guild specific prefixes, or that this feature is not supported
     * by this implementation.
     *
     * @return A Collection of String prefixes for the Guild represented by this
     *         implementation, or {@code null} to signify it has none or that the
     *         feature is not supported by the implementation.
     */
    @Nullable
    default Collection<String> getPrefixes()
    {
        return null;
    }
}
