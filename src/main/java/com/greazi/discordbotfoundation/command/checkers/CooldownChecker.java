package com.greazi.discordbotfoundation.command.checkers;

import com.greazi.discordbotfoundation.command.SimpleCommand;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponse;
import com.greazi.discordbotfoundation.command.checkers.response.SlashCommandResponseProvider;
import com.greazi.discordbotfoundation.utils.expiringmap.ExpiringMap;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A cooldown checker that prevents users from using a command too frequently.
 * You can customize the cooldown time and add bypasses for users, roles, and permissions.
 * Feel free to extend this class to add more bypasses or customize the behavior further!
 */
public class CooldownChecker implements SlashCommandChecker {

    //region Public API

    /**
     * Create a new cooldown checker with the given cooldown time. You can further customize the checker
     * by adding bypasses for users, roles, and permissions.
     * @param cooldown The cooldown time in seconds.
     * @return The cooldown checker builder.
     */
    public static CooldownChecker create(int cooldown) {
        Checks.notNegative(cooldown, "Cooldown");

        return new CooldownChecker(cooldown, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Adds a user ID to the bypass list. This user will not be affected by the cooldown.
     * @param userId The user ID to bypass.
     * @return The cooldown checker builder.
     */
    public CooldownChecker bypassUser(String userId) {
        Checks.notEmpty(userId, "User ID");

        bypassedUsers.add(userId);
        return this;
    }

    /**
     * Adds a role ID to the bypass list. Members with this role will not be affected by the cooldown.
     * @param roleId The role ID to bypass.
     * @return The cooldown checker builder.
     */
    public CooldownChecker bypassRole(String roleId) {
        Checks.notEmpty(roleId, "Role ID");

        bypassedRoles.add(roleId);
        return this;
    }

    /**
     * Adds a permission to the bypass list. Members with this permission will not be affected by the cooldown.
     * @param permission The permission to bypass.
     * @return The cooldown checker builder.
     */
    public CooldownChecker bypassPermission(Permission permission) {
        Checks.notNull(permission, "Permission");

        bypassedPermissions.add(permission);
        return this;
    }

    //endregion

    //region Core

    private final ExpiringMap<User, Long> cooldownMap =
            ExpiringMap.builder().expiration(30, TimeUnit.MINUTES).build();
    private final int cooldown;

    // By-passes
    private final List<String> bypassedUsers;
    private final List<String> bypassedRoles;
    private final List<Permission> bypassedPermissions;

    private CooldownChecker(int cooldown, List<String> bypassedUsers, List<String> bypassedRoles, List<Permission> bypassedPermissions) {
        this.cooldown = cooldown;
        this.bypassedUsers = bypassedUsers;
        this.bypassedRoles = bypassedRoles;
        this.bypassedPermissions = bypassedPermissions;
    }

    @NotNull
    @Override
    public SlashCommandResponse check(@NotNull SimpleCommand command, @NotNull SlashCommandInteractionEvent context) {
        final User user = context.getUser();
        final long now = System.currentTimeMillis();
        final SlashCommandResponseProvider provider = command.getResponseProvider();

        if (bypassedUsers.contains(user.getId()))
            return provider.success();

        if (context.getMember() != null && bypassedRoles.stream().anyMatch(role -> context.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(role))))
            return provider.success();

        if (context.getMember() != null && bypassedPermissions.stream().anyMatch(permission -> context.getMember().hasPermission(permission)))
            return provider.success();

        final long lastExecution = cooldownMap.getOrDefault(user, 0L);
        if (lastExecution + cooldown > now) {
            final long remaining = (lastExecution + cooldown - now) / 1000;
            return provider.error("You are on cooldown! Please wait " + remaining + " seconds before using this command again.");
        }

        cooldownMap.put(user, now);
        return provider.success();
    }

    //endregion

}
