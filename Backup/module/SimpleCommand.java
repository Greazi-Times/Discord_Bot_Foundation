/*
 * Copyright (c) 2022. Greazi All rights reservered
 */

package com.greazi.discordbotfoundation.module;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Cooldown;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.HashMap;

public abstract class SimpleCommand {

    private final HashMap<String, Cooldown> cooldowns = new HashMap<>();
    protected SimpleBot bot;

    public SimpleCommand(SimpleBot bot) {
        this.bot = bot;
    }

    public abstract String setCommand();

    public abstract String setDescription();

    public abstract CommandPrivilege[] setCommandPrivileges();

    public abstract OptionData[] setOptions();

    public abstract int setCooldown();

    public HashMap<String, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public abstract void onCommand(TextChannel channel, Member member, SlashCommandInteractionEvent slashCommandEvent);

}