/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.commands;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * The slash command handler that handles the whole slash command event.
 * Uses the information of {@link SimpleSlashCommand}
 */
public class SlashCommandHandler extends ListenerAdapter {

    // The hashmap and list of tall the slash commands
    private final HashMap<String, SimpleSlashCommand> cmdList = new HashMap<>();
    private final List<SlashCommandData> slashCommands = new ArrayList<>();

    /**
     * The main slash command handler
     */
    public SlashCommandHandler(){
        Debugger.debug("SlashCommand", "Slash Command main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    /**
     * Add a slash command to the SlashCommand list
     * @param module The SlashCommand module
     * @return this
     */
    public SlashCommandHandler addCommand(SimpleSlashCommand module) {
        // Retrieve the slash command data
        SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());

        // add sub commands
        List<SubcommandData> moduleSubcommands = module.getSubCommands();
        for (SubcommandData var : moduleSubcommands) {
            command.addSubcommands(var);
        }

        // Add sub command groups
        List<SubcommandGroupData> moduleSubcommandGroup = module.getSubcommandGroup();
        for (SubcommandGroupData var : moduleSubcommandGroup) {
            command.addSubcommandGroups(var);
        }

        // Get options if sub commands are empty
        if (module.getSubCommands().isEmpty() && module.getSubcommandGroup().isEmpty() && !module.getOptions().isEmpty()){
            List<OptionData> moduleOptions = module.getOptions();
            for (OptionData var : moduleOptions) {
                command.addOptions(var);
            }
        }

        // Set default enabled
        command.setDefaultEnabled(module.getDefaultEnabled());
        // Set description
        command.setDescription(module.getDescription());


        // Add the slash command
        slashCommands.add(command);
        // Add the slash command to the hashmap
        cmdList.put(module.getCommand(), module);
        
        return this;
    }

    /**
     *  Register all slash commands to JDA
     */
    public void registerCommands() {
        // Check if the slash commands isn't empty
        if (slashCommands.isEmpty()) return;

        // TODO: Add a check for main guild
        //      Once main guild == true add to main guild only if not add to all guilds
        
        // Add all slash commands to the main guild
        SimpleBot.getGuild().updateCommands()
                .addCommands(slashCommands)
                .queue(commands -> {
                    commands.forEach(cmd -> {
                        SimpleSlashCommand module = cmdList.get(cmd.getName());

                        List<CommandPrivilege> commandPrivileges = new ArrayList<>();
                        module.getDisabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.disableRole(role.getId())));
                        module.getDisabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.disableUser(user.getId())));
                        module.getEnabledRoles().forEach(role -> commandPrivileges.add(CommandPrivilege.enableRole(role.getId())));
                        module.getEnabledUsers().forEach(user -> commandPrivileges.add(CommandPrivilege.enableUser(user.getId())));

                        SimpleBot.getGuild().updateCommandPrivilegesById(cmd.getId(), commandPrivileges);
                    });
                });
    }

    /**
     * The main event listener for the slash command interaction event
     * @param event SlashCommandInteractionEvent
     */
    @Override
    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Debugger.debug("SlashCommand", "A Slash Command has been used");

        // Log who used a slash command
        Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Slash Command: " + ConsoleColor.CYAN + event.getCommandString() + ConsoleColor.RESET);

        // Retrieve the command class from the command that has been run
        SimpleSlashCommand module = cmdList.get(event.getName());

        // If the module doesn't exist in the bot return an error
        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - command not found")
                    .text("The command that you have used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        // Check if main guild is enabled
        if (!Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.Bot.MainGuild()) && module.getGuildOnly()){
            return;
        }

        // Check if NSFW channel is enabled
        if (!event.getTextChannel().isNSFW() && module.getNsfwOnly()){
            return;
        }

        Debugger.debug("SlashCommand", "Executing command logic");        
        // Run the command logic
        module.execute(event);
    }
}
