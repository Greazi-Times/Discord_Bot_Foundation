package com.greazi.discordbotfoundation.module;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Cooldown;
import com.greazi.discordbotfoundation.utils.ProjectUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModulesManager {

    private final List<SimpleCommand> cmdModules = new ArrayList<>();
    private final List<SimpleModule> modules = new ArrayList<>();

    public void load() {

    }

    public void logLoad() {
        int successfulAmountModules = (int)modules.stream().filter(SimpleModule::isEnabled).count();

        Common.log.info("Modules:");
        Common.log.info("  » All: " + (modules.size() + cmdModules.size()));
        Common.log.info("  » Modules: " + modules.size());
        Common.log.info("  » Command: " + cmdModules.size());
        Common.log.info("  » Success: " + successfulAmountModules);
    }

    @SubscribeEvent
    public void onSlashCommand(SlashCommandEvent e) {
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