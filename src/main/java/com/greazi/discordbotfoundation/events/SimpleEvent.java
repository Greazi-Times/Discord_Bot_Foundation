package com.greazi.discordbotfoundation.events;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SimpleEvent extends ListenerAdapter {

	public SimpleEvent() {
		SimpleBot.getJDA().addEventListener(this);
	}
}
