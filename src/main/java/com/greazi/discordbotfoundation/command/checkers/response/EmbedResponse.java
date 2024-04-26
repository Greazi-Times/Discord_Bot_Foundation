package com.greazi.discordbotfoundation.command.checkers.response;

import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

/**
 * Represent an embed slash command response, here to shows an error/warning/info/etc...
 * using an embed message.
 */
public class EmbedResponse implements SlashCommandResponse {

    //region Public API

    /**
     * Create an embed response with an error type.
     * @return the embed response instance.
     */
    public static EmbedResponse error(@NotNull String title,
                                      @NotNull String message) {
        return new EmbedResponse(EmbedType.ERROR, title, message);
    }

    /**
     * Create an embed response with a warning type.
     * @return the embed response instance.
     */
    public static EmbedResponse warning(@NotNull String title,
                                        @NotNull String message) {
        return new EmbedResponse(EmbedType.WARNING, title, message);
    }

    /**
     * Create an embed response with an info type.
     * @return the embed response instance.
     */
    public static EmbedResponse info(@NotNull String title,
                                     @NotNull String message) {
        return new EmbedResponse(EmbedType.INFO, title, message);
    }

    /**
     * Create an embed response with a success type.
     * @return the embed response instance.
     */
    public static EmbedResponse success(@NotNull String title,
                                        @NotNull String message) {
        return new EmbedResponse(EmbedType.SUCCESS, title, message);
    }

    //endregion

    private final EmbedType type;
    private final String title;
    private final String message;
    private boolean ephemeral = false;

    private EmbedResponse(EmbedType type, String title, String message) {
        Checks.notNull(type, "Type");
        Checks.notEmpty(title, "Title");
        Checks.notEmpty(message, "Message");

        this.type = type;
        this.title = title;
        this.message = message;
    }

    @Override
    public void respond(@NotNull SlashCommandInteractionEvent event) {
        final SimpleEmbedBuilder builder = new SimpleEmbedBuilder();
        switch (type) {
            case ERROR -> builder.error();
            case WARNING -> builder.warning();
            case INFO -> builder.info();
            case SUCCESS -> builder.success();
        }

        builder.setTitle(title);
        builder.setDescription(message);

        event.replyEmbeds(builder.build()).setEphemeral(ephemeral).queue();
    }

    @Override
    public boolean isSuccessful() {
        return switch (type) {
            case ERROR, WARNING -> false;
            case INFO, SUCCESS -> true;
        };
    }

    /**
     * Makes the current embed response instance as ephemeral.
     * @return the current embed response instance, for chaining.
     */
    public EmbedResponse ephemeral() {
        ephemeral = true;
        return this;
    }

    /**
     * Change the ephemeral state of the current embed response instance.
     * @param ephemeral The new ephemeral state.
     * @return the current embed response instance, for chaining.
     */
    public EmbedResponse ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }

    public enum EmbedType {
        ERROR,
        WARNING,
        INFO,
        SUCCESS
    }
}
