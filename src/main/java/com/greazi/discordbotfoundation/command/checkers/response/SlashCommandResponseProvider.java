package com.greazi.discordbotfoundation.command.checkers.response;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for providing {@link SlashCommandResponse slash command responses} for usual
 * messages, such as errors, success, warnings, etc...
 */
public interface SlashCommandResponseProvider {
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for a <strong>success</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    SlashCommandResponse success(@Nullable String message);
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for an <strong>error</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    SlashCommandResponse error(@Nullable String message);
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for a <strong>warning</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    SlashCommandResponse warning(@Nullable String message);
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for an <strong>info</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    SlashCommandResponse info(@Nullable String message);
    
    // -------------------------
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for a <strong>success</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    default SlashCommandResponse success() {
        return success(null);
    }
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for an <strong>error</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    default SlashCommandResponse error() {
        return error(null);
    }
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for a <strong>warning</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    default SlashCommandResponse warning() {
        return warning(null);
    }
    
    /**
     * Provide a {@link SlashCommandResponse slash command response} for an <strong>info</strong>
     * message.
     * @return The provided {@link SlashCommandResponse slash command response}.
     */
    default SlashCommandResponse info() {
        return info(null);
    }
}
