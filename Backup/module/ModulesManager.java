/*
 * Copyright (c) 2022. Greazi All rights reservered
 */

package com.greazi.discordbotfoundation.module;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.objects.Cooldown;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ModulesManager {

    private final List<SimpleCommand> cmdModules = new ArrayList<>();
    private final List<SimpleModule> modules = new ArrayList<>();

    public void load() {

    }

    public void logLoad() {
        int successfulAmountModules = (int)modules.stream().filter(SimpleModule::isEnabled).count();

        Common.log("Modules:");
        Common.log("  » All: " + (modules.size() + cmdModules.size()));
        Common.log("  » Modules: " + modules.size());
        Common.log("  » Command: " + cmdModules.size());
        Common.log("  » Success: " + successfulAmountModules);
    }

    @SubscribeEvent
    public void onSlashCommand(SlashCommandInteractionEvent e) {
        SimpleCommand cmd = cmdModules.stream().filter(c -> c.setCommand().equalsIgnoreCase(e.getName())).findFirst().orElse(null);
        if(cmd == null || e.getMember() == null || e.getUser().isBot())
            return;

        if(cmd.setCooldown() > 0 && cmd.getCooldowns().containsKey(e.getMember().getId())) {
            Cooldown cooldown = cmd.getCooldowns().get(e.getMember().getId());
            if(cooldown.isCooldownRemaining()) {
                e.deferReply(true).queue();

                e.reply("**Woah there... slow down!** There's still **" + cooldown.getRemainingCooldown() + "** seconds left on your cooldown!").queue();
                return;
            }

            cmd.getCooldowns().remove(e.getMember().getId());
        }

        cmd.onCommand(e.getTextChannel(), e.getMember(), e);
    }

    public List<SimpleModule> getModules() {
        return modules;
    }

    public List<SimpleCommand> getCommandModules() {
        return cmdModules;
    }
}