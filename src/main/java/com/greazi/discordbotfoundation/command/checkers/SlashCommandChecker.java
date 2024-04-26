package com.greazi.discordbotfoundation.command.checkers;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponse;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for checking if a slash command context is valid to execute the actual command's code.
 */
public interface SlashCommandChecker {

    @NotNull
    SlashCommandResponse check(@NotNull SimpleCommand command,
                               @NotNull SlashCommandInteractionEvent context);

}
