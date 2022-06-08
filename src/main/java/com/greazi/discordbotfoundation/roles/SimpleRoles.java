package com.greazi.discordbotfoundation.roles;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Query;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleRoles {

	public Query<Role> getRoles(String... names) {
		List<Role> roles = Arrays.stream(names).flatMap(name -> SimpleBot.getGuild().getRolesByName(name, true).stream()).collect(Collectors.toList());

		return new Query<>(roles);
	}

	public Role getRole(long id) {
		return SimpleBot.getGuild().getRoleById(id);
	}
}
