package com.greazi.discordbotfoundation.module;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.Cooldown;
import com.greazi.discordbotfoundation.utils.ProjectUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
        SimpleBot.getJDA().updateCommands().queue();
        CommandListUpdateAction commands = SimpleBot.getGuild().updateCommands();

        for (Class<?> each : ProjectUtil.getClasses("com.greazi.SimpleBot.module")) {
            if (SimpleCommand.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    SimpleCommand module = (SimpleCommand)each.getConstructor(SimpleBot.class).newInstance(SimpleBot.getBot());
                    if(module.setCommand() == null)
                        continue;

                    cmdModules.add(module);

                    CommandData cmdData = new CommandData(module.setCommand(), module.setDescription() == null ? "No description set." : module.setDescription())
                            .addOptions(module.setOptions())
                            .setDefaultEnabled(module.setCommandPrivileges().length == 0);

                    commands.addCommands(cmdData);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else if (SimpleModule.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    SimpleModule module = (SimpleModule) each.getConstructor(SimpleBot.class).newInstance(SimpleBot.getBot());
                    module.enable();

                    modules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        commands.addCommands(
            new CommandData("ticket", "Manage tickets.")
                .addSubcommands(
                    new SubcommandData("add", "Add a member to a ticket.")
                        .addOptions(
                            new OptionData(OptionType.USER, "member", "Member to add.", true)
                    ),
                    new SubcommandData("remove", "Remove a member from a ticket.")
                        .addOptions(
                            new OptionData(OptionType.USER, "member", "Member to remove.", true)
                    ),
                    new SubcommandData("transcript", "Force make a ticket transcript."),
                    new SubcommandData("close", "Close a ticket.")
                        .addOptions(
                            new OptionData(OptionType.STRING, "reason", "Reason to close the ticket. (Optional)")
                    )
                )
        ).queue(cmds -> {
            cmds.forEach(command -> {
                CommandPrivilege[] privilege = cmdModules.stream().filter(c -> c.setCommand().equals(command.getName())).map(SimpleCommand::setCommandPrivileges).findFirst().orElse(new CommandPrivilege[]{});

                if (privilege.length > 0)
                    SimpleBot.getGuild().updateCommandPrivilegesById(command.getId(), Arrays.asList(privilege)).queue();
            });
        });

        SimpleBot.getJDA().addEventListener(modules.toArray());
        SimpleBot.getJDA().addEventListener(cmdModules.toArray());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(SimpleModule::onDisable)));
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