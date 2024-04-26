package com.greazi.discordbotfoundation.command.checkers.response;

import org.jetbrains.annotations.Nullable;

/**
 * Default {@link SlashCommandResponseProvider command provider} implementation,
 * using {@link com.greazi.discordbotfoundation.command.checkers.response.EmbedResponse embed response} as default responses.
 */
public class DefaultCommandProvider implements SlashCommandResponseProvider {

    /**
     * The default command provider for ephemeral responses.
     */
    public static final DefaultCommandProvider EPHEMERAL = new DefaultCommandProvider(true);

    /**
     * The default command provider for non-ephemeral responses.
     */
    public static final DefaultCommandProvider NORMAL = new DefaultCommandProvider(false);

    private final boolean ephemeral;
    private DefaultCommandProvider(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    @Override
    public SlashCommandResponse success(@Nullable String message) {
        return EmbedResponse.success("Success", message != null ? message : "The command was executed successfully.").ephemeral(ephemeral);
    }

    @Override
    public SlashCommandResponse error(@Nullable String message) {
        return EmbedResponse.error("Error!", message != null ? message : "An error occurred while executing the command.").ephemeral(ephemeral);
    }

    @Override
    public SlashCommandResponse warning(@Nullable String message) {
        return EmbedResponse.warning("Warning!", message != null ? message : "A warning occurred while executing the command.").ephemeral(ephemeral);
    }

    @Override
    public SlashCommandResponse info(@Nullable String message) {
        return EmbedResponse.info("Information", message != null ? message : "An info occurred while executing the command.").ephemeral(ephemeral);
    }
}
