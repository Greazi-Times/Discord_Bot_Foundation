package com.greazi.discordbotfoundation.command.checkers.response;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represent an <strong>internal</strong> slash command response, e.g. knowing the state of the
 * command's pre-execution before its actual execution.
 * Although default responses are available, you can create your own to fit your needs!
 * @see EmbedResponse
 */
public interface SlashCommandResponse {

    /**
     * Respond to the slash command event.
     * @param event The slash command event.
     */
    void respond(@NotNull SlashCommandInteractionEvent event);

    /**
     * <p>Whether the slash command pre-execution was successful, and the command can be executed.</p>
     * <h2><strong>If this returns true, {@link #respond(SlashCommandInteractionEvent)} will never be called!</strong></h2>
     * @return True if the command can be executed, false otherwise.
     */
    boolean isSuccessful();
}
