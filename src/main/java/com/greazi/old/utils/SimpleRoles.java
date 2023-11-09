/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleRoles {

    /**
     * Add a role to a member
     *
     * @param member The member to add the role to
     * @param role   The role to add
     */
    public static void addRole(final @NotNull Member member, final Role role) {
        if (hasRole(member, role)) return;
        member.getGuild().addRoleToMember(member.getUser(), role).queue();
    }

    /**
     * Add a role from its ID to a member
     *
     * @param member The member to add the role to
     * @param roleId The ID of the role to add
     */
    public static void addRole(@NotNull final Member member, final long roleId) {
        addRole(member, member.getGuild().getRoleById(roleId));
    }

    /**
     * Add a role by name to a member
     *
     * @param member   The member to add the role to
     * @param roleName The name of the role to add
     */
    public static void addRole(final @NotNull Member member, final String roleName) {
        member.getGuild().getRolesByName(roleName, true).forEach(role -> addRole(member, role));
    }

    /**
     * Add multiple roles at once
     *
     * @param member The member to add the roles to
     * @param roles  The roles to add
     */
    public static void addRoles(final Member member, @NotNull final Role... roles) {
        for (final Role role : roles) {
            addRole(member, role);
        }
    }

    /**
     * Add multiple roles from their IDs at once
     *
     * @param member  The member to add the roles to
     * @param roleIds The IDs of the roles to add
     */
    public static void addRoles(final Member member, @NotNull final long... roleIds) {
        for (final long roleId : roleIds) {
            addRole(member, member.getGuild().getRoleById(roleId));
        }
    }

    /**
     * Add multiple roles by name at once to a member
     *
     * @param member    The member to add the role to
     * @param roleNames The name of the role to add
     */
    public static void addRoles(final Member member, @NotNull final String... roleNames) {
        for (final String roleName : roleNames) {
            member.getGuild().getRolesByName(roleName, true).forEach(role -> addRole(member, role));
        }
    }

    public static void removeRole(final @NotNull Member member, final Role role) {
        member.getGuild().removeRoleFromMember(member.getUser(), role).queue();
    }

    public static void removeRole(@NotNull final Member member, final long roleId) {
        removeRole(member, member.getGuild().getRoleById(roleId));
    }

    public static void removeRole(final @NotNull Member member, final String roleName) {
        member.getGuild().getRolesByName(roleName, true).forEach(role -> removeRole(member, role));
    }

    public static void removeRoles(final Member member, @NotNull final Role... roles) {
        for (final Role role : roles) {
            member.getGuild().removeRoleFromMember(member.getUser(), role).queue();
        }
    }

    public static void removeRoles(final Member member, @NotNull final long... roleIds) {
        for (final long roleId : roleIds) {
            removeRole(member, member.getGuild().getRoleById(roleId));
        }
    }

    public static void removeRoles(final Member member, @NotNull final String... roleNames) {
        for (final String roleName : roleNames) {
            member.getGuild().getRolesByName(roleName, true).forEach(role -> removeRole(member, role));
        }
    }

    public static boolean hasRole(@NotNull final Member member, @NotNull final Role role) {
        final List<Role> roles = member.getRoles();
        for (final Role memberRole : roles) {
            if (memberRole.getIdLong() == role.getIdLong()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(final Member member, final long roleId) {
        return hasRole(member, getRoleById(member.getGuild(), roleId));
    }

    public static boolean hasRole(final Member member, final String roleName) {
        return hasRole(member, getRoleByName(member.getGuild(), roleName));
    }

    public static boolean hasRoles(final Member member, @NotNull final Role... roles) {
        for (final Role role : roles) {
            if (!hasRole(member, role)) return false;
        }
        return true;
    }

    public static boolean hasRoles(final Member member, @NotNull final long... roleIds) {
        for (final long roleId : roleIds) {
            if (!hasRole(member, roleId)) return false;
        }
        return true;
    }

    public static boolean hasRoles(final Member member, @NotNull final String... roleNames) {
        for (final String roleName : roleNames) {
            if (!hasRole(member, roleName)) return false;
        }
        return true;
    }

    /**
     * Get a role from its ID
     *
     * @param roleId The ID of the role to get
     * @return The role
     */
    public static Role getRoleById(@NotNull final Guild guild, final long roleId) {
        return guild.getRoleById(roleId);
    }

    /**
     * Get a role by name
     *
     * @param roleName The name of the role to get
     * @return The role
     */
    public static Role getRoleByName(@NotNull final Guild guild, final String roleName) {
        return guild.getRolesByName(roleName, true).get(0);
    }

    public static List<Role> getAllMemberRoles(@NotNull final Member member) {
        return member.getRoles();
    }

}
