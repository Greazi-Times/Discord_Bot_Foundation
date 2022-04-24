/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.modal;


import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.command.SimpleSlashCommand;
import com.greazi.discordbotfoundation.command.SlashCommandHandler;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ModalHandler {

    private final HashMap<String, SimpleSlashCommand> cmdList = new HashMap<>();

    public SlashCommandHandler() {
        Debugger.debug("ModalHandler", "ModalHandler main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    public SlashCommandHandler addCommand(SimpleSlashCommand module) {
        Debugger.debug("ModalHandler", "Start of addCommand(...);27");

        SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());
        Debugger.debug("ModalHandler", "Retrieved SlashCommandData; " + module.getCommand() + ", " + module.getDescription());

        Debugger.debug("ModalHandler", "  Retrieving subcommands for; " + module.getCommand());
        List<SubcommandData> moduleSubcommands = module.getSubCommands();
        for (SubcommandData var : moduleSubcommands) {
            command.addSubcommands(var);
            Debugger.debug("ModalHandler", "    - " + var);
        }

        Debugger.debug("ModalHandler", "  Retrieving subcommand groups for; " + module.getCommand());
        List<SubcommandGroupData> moduleSubcommandGroup = module.getSubcommandGroup();
        for (SubcommandGroupData var : moduleSubcommandGroup) {
            command.addSubcommandGroups(var);
            Debugger.debug("ModalHandler", "    - " + var);
        }

        Debugger.debug("ModalHandler", "  Checking if subcommands exists for; " + module.getCommand());
        if (module.getSubCommands().isEmpty() && module.getSubcommandGroup().isEmpty() && !module.getOptions().isEmpty()) {
            Debugger.debug("ModalHandler", "    No subcommands getting options;");
            List<OptionData> moduleOptions = module.getOptions();
            for (OptionData var : moduleOptions) {
                command.addOptions(var);
                Debugger.debug("ModalHandler", "      - " + var);
            }
        }
        Debugger.debug("ModalHandler", "  Setting other info;",
                "    Default enabled; " + module.getDefaultEnabled(),
                "    Description; " + module.getDescription());
        command.setDefaultEnabled(module.getDefaultEnabled());
        command.setDescription(module.getDescription());

        Debugger.debug("ModalHandler", "  Adding the whole command; " + command.getName());
        slashCommands.add(command);
        cmdList.put(module.getCommand(), module);

        return this;
    }

    public void registerCommands() {
        Debugger.debug("ModalHandler", "Start of registerCommands()");
        if (slashCommands.isEmpty()) return;

        SimpleBot.getGuild().updateCommands()
                .addCommands(slashCommands)
                .queue(commands -> {
                    commands.forEach(cmd -> {
                        Debugger.debug("ModalHandler", "  Getting command from cmdList " + cmd.getName());
                        SimpleSlashCommand module = cmdList.get(cmd.getName());

                        Debugger.debug("ModalHandler", "  Adding privilages to " + cmd.getName());
                        List<CommandPrivilege> commandPrivileges = new ArrayList<>();
                        module.getDisabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.disableRole(role.getId())));
                        module.getDisabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.disableUser(user.getId())));
                        module.getEnabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.enableRole(role.getId())));
                        module.getEnabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.enableUser(user.getId())));

                        Debugger.debug("SlashCommandHandler", "  Updating the command " + cmd.getName());
                        SimpleBot.getGuild().updateCommandPrivilegesById(cmd.getId(), commandPrivileges);
                    });
                });
    }

    @Override
    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Debugger.debug("ModalHandler", "A slash command event has been triggered onSlashCommandInteraction(...);96");
        // Retrieve the command class from the command that has been run
        SimpleSlashCommand module = cmdList.get(event.getName());

        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - command not found")
                    .text("The command that you have used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        Debugger.debug("ModalHandler", "  Found event; " + module);

        if (module.getGuildOnly() && !Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.getInstance().getMainGuild())) {
            return;
        }

        if (event.getTextChannel().isNSFW() && !module.getNsfwOnly()) {
            return;
        }

        Debugger.debug("ModalHandler", "  Executing command logic");
        module.execute(event);
    }
}