package com.greazi.discordbotfoundation.command.checkers.response;

import org.jetbrains.annotations.Nullable;

/**
 * Default {@link SlashCommandResponseProvider command provider} implementation,
 * using {@link com.greazi.discordbotfoundation.command.checkers.response.EmbedResponse embed response} as default responses.
 */
public class DefaultCommandProvider implements SlashCommandResponseProvider {

    @Override
    public SlashCommandResponse success(@Nullable String message) {
        return EmbedResponse.success("Success", message != null ? message : "The command was executed successfully.");
    }

    @Override
    public SlashCommandResponse error(@Nullable String message) {
        return EmbedResponse.error("Error!", message != null ? message : "An error occurred while executing the command.");
    }

    @Override
    public SlashCommandResponse warning(@Nullable String message) {
        return EmbedResponse.warning("Warning!", message != null ? message : "A warning occurred while executing the command.");
    }

    @Override
    public SlashCommandResponse info(@Nullable String message) {
        return EmbedResponse.info("Information", message != null ? message : "An info occurred while executing the command.");
    }
}
