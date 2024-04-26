package com.greazi.discordbotfoundation.command.checkers;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponse;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Check if the channel is allowed/disallowed to execute the command.
 */
public class ChannelsChecker implements SlashCommandChecker {

    //region Public API

    /**
     * Create a new channels checker that allows only the specified channels to execute the command.
     * @param allowedChannelsIds The list of roles ids that are allowed to execute the command.
     * @return The roles checker instance.
     */
    public static ChannelsChecker createAllowed(@NotNull List<String> allowedChannelsIds) {
        return create(allowedChannelsIds, new ArrayList<>());
    }

    /**
     * Create a new channels checker that disallows the specified channels to execute the command.
     * @param disallowedRolesIds The list of roles ids that are disallowed to execute the command.
     * @return The roles checker instance.
     */
    public static ChannelsChecker createDisallowed(@NotNull List<String> disallowedRolesIds) {
        return create(new ArrayList<>(), disallowedRolesIds);
    }

    /**
     * Create a new channels checker that allows and disallows the specified channels to execute the command.
     * @param allowedChannelsIds The list of roles ids that are allowed to execute the command.
     * @param disallowedRolesIds The list of roles ids that are disallowed to execute the command.
     * @return The roles checker instance.
     */
    public static ChannelsChecker create(@NotNull List<String> allowedChannelsIds,
                                      @NotNull List<String> disallowedRolesIds) {
        Checks.notEmpty(allowedChannelsIds, "allowedChannelsIds");
        Checks.noneNull(allowedChannelsIds, "allowedChannelsIds");
        Checks.notEmpty(disallowedRolesIds, "disallowedRolesIds");
        Checks.noneNull(disallowedRolesIds, "disallowedRolesIds");

        return new ChannelsChecker(allowedChannelsIds, disallowedRolesIds);
    }

    //endregion

    private final List<String> allowedChannelsIds;
    private final List<String> disallowedRolesIds;
    private ChannelsChecker(List<String> allowedChannelsIds, List<String> disallowedRolesIds) {
        this.allowedChannelsIds = allowedChannelsIds;
        this.disallowedRolesIds = disallowedRolesIds;
    }

    @NotNull
    @Override
    public SlashCommandResponse check(@NotNull SimpleCommand command,
                                      @NotNull SlashCommandInteractionEvent context) {
        final var provider = command.getResponseProvider();
        if (context.getMember() == null) {
            return provider.error("You must be in a guild to use this command.");
        }

        if (allowedChannelsIds.isEmpty() && disallowedRolesIds.isEmpty())
            return provider.success();

        final var channelId = context.getChannel().getId();
        if (!allowedChannelsIds.isEmpty() && !allowedChannelsIds.contains(channelId))
            return provider.error("You are not in the allowed channel to use this command.");

        if (!disallowedRolesIds.isEmpty() && disallowedRolesIds.contains(channelId))
            return provider.error("You are in a disallowed channel to use this command.");

        return provider.success();
    }

}
