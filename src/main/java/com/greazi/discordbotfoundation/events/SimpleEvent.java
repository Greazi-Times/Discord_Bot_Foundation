package com.greazi.discordbotfoundation.events;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SimpleEvent extends ListenerAdapter {

	public SimpleEvent() {
		SimpleBot.getJDA().addEventListener(this);
	}
}
