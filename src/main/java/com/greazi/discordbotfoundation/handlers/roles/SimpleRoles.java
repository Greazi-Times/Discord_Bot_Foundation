/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.roles;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Query;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Finish this

public class SimpleRoles {

	public static void addRole(@NotNull final Member member, @NotNull final Role role) {
		member.getRoles().add(role);
	}

	public static void addRole(@NotNull final Member member, final Role @NotNull ... roles) {
		for (final Role role : roles)
			member.getRoles().add(role);
	}

	public static void addRole(@NotNull final Member member, @NotNull final int role) {
		member.getRoles().add(getRole(role));
	}

	public static void addRole(@NotNull final Member member, final int @NotNull ... role) {
		for (final int r : role) {
			member.getRoles().add(getRole(r));
		}
	}

	public static void addRole(@NotNull final Member member, @NotNull final String role) {
		member.getRoles().add(getRole(role));
	}

	public static void addRole(@NotNull final Member member, final String @NotNull ... role) {
		for (final String r : role) {
			member.getRoles().add(getRole(r));
		}
	}

	public static Query<Role> getRoles(final String... names) {
		final List<Role> roles = Arrays.stream(names).flatMap(name -> SimpleBot.getMainGuild().getRolesByName(name, true).stream()).collect(Collectors.toList());

		return new Query<>(roles);
	}

	public static Role getRole(final long id) {
		return SimpleBot.getMainGuild().getRoleById(id);
	}

	public static Role getRole(final String name) {
		return SimpleBot.getMainGuild().getRolesByName(name, false).get(0);
	}

	public static Role getRoleIgnoreCase(final String name) {
		return SimpleBot.getMainGuild().getRolesByName(name, true).get(0);
	}
}
