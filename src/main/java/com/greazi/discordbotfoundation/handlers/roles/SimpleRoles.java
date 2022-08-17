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

	public static void addRole(@NotNull Member member, @NotNull Role role) {
		member.getRoles().add(role);
	}

	public static void addRole(@NotNull Member member, Role @NotNull  ... roles) {
		for (Role role : roles)
			member.getRoles().add(role);
	}

	public static void addRole(@NotNull Member member, @NotNull int role) {
		member.getRoles().add(getRole(role));
	}

	public static void addRole(@NotNull Member member, int @NotNull ... role) {
		for(int r : role) {
			member.getRoles().add(getRole(r));
		}
	}

	public static void addRole(@NotNull Member member, @NotNull String role) {
		member.getRoles().add(getRole(role));
	}

	public static void addRole(@NotNull Member member, String @NotNull ... role) {
		for(String r : role) {
			member.getRoles().add(getRole(r));
		}
	}

	public static Query<Role> getRoles(String... names) {
		List<Role> roles = Arrays.stream(names).flatMap(name -> SimpleBot.getGuild().getRolesByName(name, true).stream()).collect(Collectors.toList());

		return new Query<>(roles);
	}

	public static Role getRole(long id) {
		return SimpleBot.getGuild().getRoleById(id);
	}

	public static Role getRole(String name) {
		return SimpleBot.getGuild().getRolesByName(name, false).get(0);
	}

	public static Role getRoleIgnoreCase(String name) {
		return SimpleBot.getGuild().getRolesByName(name, true).get(0);
	}
}
