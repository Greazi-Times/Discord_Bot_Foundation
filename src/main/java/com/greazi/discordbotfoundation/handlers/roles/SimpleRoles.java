/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.roles;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

public class SimpleRoles {

	/**
	 * Add a role to a member
	 *
	 * @param member The member to add the role to
	 * @param role   The role to add
	 */
	public static void addRole(final @NotNull Member member, final Role role) {
		member.getRoles().add(role);
	}

	/**
	 * Add multiple roles at once
	 *
	 * @param member The member to add the roles to
	 * @param roles  The roles to add
	 */
	public static void addRoles(final Member member, final Role @NotNull ... roles) {
		for (final Role role : roles) {
			member.getRoles().add(role);
		}
	}

	/**
	 * Add a role from its ID to a member
	 *
	 * @param member The member to add the role to
	 * @param roleId The ID of the role to add
	 */
	public static void addRole(@NotNull final Member member, final long roleId) {
		member.getRoles().add(member.getGuild().getRoleById(roleId));
	}

	/**
	 * Add multiple roles from their IDs at once
	 *
	 * @param member  The member to add the roles to
	 * @param roleIds The IDs of the roles to add
	 */
	public static void addRoles(final Member member, final long @NotNull ... roleIds) {
		for (final long roleId : roleIds) {
			member.getRoles().add(member.getGuild().getRoleById(roleId));
		}
	}

	/**
	 * Add a role by name to a member
	 *
	 * @param member   The member to add the role to
	 * @param roleName The name of the role to add
	 */
	public static void addRole(final @NotNull Member member, final String roleName) {
		member.getRoles().add(member.getGuild().getRolesByName(roleName, true).get(0));
	}

	/**
	 * Add multiple roles by name at once to a member
	 *
	 * @param member    The member to add the role to
	 * @param roleNames The name of the role to add
	 */
	public static void addRoles(final Member member, final String @NotNull ... roleNames) {
		for (final String role : roleNames) {
			member.getRoles().add(member.getGuild().getRolesByName(role, true).get(0));
		}
	}

	/**
	 * Get a role from its ID
	 *
	 * @param member The member to get the role from
	 * @param roleId The ID of the role to get
	 * @return The role
	 */
	public static Role getRoleById(final long roleId) {
		return SimpleBot.getMainGuild().getRoleById(roleId);
	}

	/**
	 * Get a role by name
	 *
	 * @param member   The member to get the role from
	 * @param roleName The name of the role to get
	 * @return The role
	 */
	public static Role getRoleByName(final String roleName) {
		return SimpleBot.getMainGuild().getRolesByName(roleName, true).get(0);
	}

}
