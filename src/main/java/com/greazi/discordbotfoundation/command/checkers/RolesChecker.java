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
 * Check if the member has the required roles to execute the command.
 */
public class RolesChecker implements SlashCommandChecker {

    //region Public API

    /**
     * Create a new roles checker that allows only the specified roles to execute the command.
     * @param allowedRolesIds The list of roles ids that are allowed to execute the command.
     * @return The roles checker instance.
     */
    public static RolesChecker createAllowed(@NotNull List<String> allowedRolesIds) {
        return create(allowedRolesIds, new ArrayList<>());
    }

    /**
     * Create a new roles checker that disallows the specified roles to execute the command.
     * @param disallowedRolesIds The list of roles ids that are disallowed to execute the command.
     * @return The roles checker instance.
     */
    public static RolesChecker createDisallowed(@NotNull List<String> disallowedRolesIds) {
        return create(new ArrayList<>(), disallowedRolesIds);
    }

    /**
     * Create a new roles checker that allows and disallows the specified roles to execute the command.
     * @param allowedRolesIds The list of roles ids that are allowed to execute the command.
     * @param disallowedRolesIds The list of roles ids that are disallowed to execute the command.
     * @return The roles checker instance.
     */
    public static RolesChecker create(@NotNull List<String> allowedRolesIds,
                                      @NotNull List<String> disallowedRolesIds) {
        Checks.notEmpty(allowedRolesIds, "allowedRolesIds");
        Checks.noneNull(allowedRolesIds, "allowedRolesIds");
        Checks.notEmpty(disallowedRolesIds, "disallowedRolesIds");
        Checks.noneNull(disallowedRolesIds, "disallowedRolesIds");

        return new RolesChecker(allowedRolesIds, disallowedRolesIds);
    }

    //endregion

    private final List<String> allowedRolesIds;
    private final List<String> disallowedRolesIds;
    private RolesChecker(List<String> allowedRolesIds, List<String> disallowedRolesIds) {
        this.allowedRolesIds = allowedRolesIds;
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

        final var memberRoles = context.getMember().getRoles();
        for (Role role : memberRoles) {
            if (disallowedRolesIds.contains(role.getId())) {
                return provider.error("You have a disallowed role to use this command.");
            }
        }

        for (String roleId : allowedRolesIds) {
            if (memberRoles.stream().noneMatch(role -> role.getId().equals(roleId))) {
                return provider.error("You don't have the required roles to use this command.");
            }
        }

        return provider.success();
    }

}
