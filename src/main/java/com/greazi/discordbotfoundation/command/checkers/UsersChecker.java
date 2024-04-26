package com.greazi.discordbotfoundation.command.checkers;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponse;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponseProvider;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Check if the user is allowed or disallowed to use the command.
 */
public class UsersChecker implements SlashCommandChecker {

    //region Public API

    /**
     * Create a new users checker with the given allowed and disallowed users.
     * @param allowedUsers The list of allowed users.
     * @param disallowedUsers The list of disallowed users.
     * @return The users checker builder.
     */
    public static UsersChecker create(List<String> allowedUsers, List<String> disallowedUsers) {
        Checks.noneNull(allowedUsers, "Allowed Users");
        Checks.noneNull(disallowedUsers, "Disallowed Users");

        if (allowedUsers.isEmpty() && disallowedUsers.isEmpty())
            throw new IllegalArgumentException("At least one list must be non-empty.");

        return new UsersChecker(allowedUsers, disallowedUsers);
    }

    /**
     * Create a new users checker with the given allowed users.
     * @param allowedUsers The list of allowed users.
     * @return The users checker builder.
     */
    public static UsersChecker createAllowed(List<String> allowedUsers) {
        return create(allowedUsers, List.of());
    }

    /**
     * Create a new users checker with the given disallowed users.
     * @param disallowedUsers The list of disallowed users.
     * @return The users checker builder.
     */
    public static UsersChecker createDisallowed(List<String> disallowedUsers) {
        return create(List.of(), disallowedUsers);
    }

    //region Core

    private final List<String> allowedUsers;
    private final List<String> disallowedUsers;
    private UsersChecker(List<String> allowedUsers, List<String> disallowedUsers) {
        this.allowedUsers = allowedUsers;
        this.disallowedUsers = disallowedUsers;
    }

    @NotNull
    @Override
    public SlashCommandResponse check(@NotNull SimpleCommand command, @NotNull SlashCommandInteractionEvent context) {
        final User user = context.getUser();
        final String userId = user.getId();
        final SlashCommandResponseProvider provider = command.getResponseProvider();

        if (allowedUsers.contains(userId))
            return provider.success();

        if (disallowedUsers.contains(userId))
            return provider.error();

        return provider.success();
    }

    //endregion
}
