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
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The slash command handler that handles the whole slash command event.
 * Uses the information of {@link SimpleSlashCommand}
 */
public class SlashCommandHandler extends ListenerAdapter {

    // The hashmap and list of tall the slash commands
    private final HashMap<String, SimpleSlashCommand> cmdList = new HashMap<>();
    private final List<SlashCommandData> publicSlashCommands = new ArrayList<>();
    private final List<SlashCommandData> mainGuildSlashCommands = new ArrayList<>();

    /**
     * The main slash command handler
     */
    public SlashCommandHandler() {
        Common.log("Initializing slash command handler");
        SimpleBot.getJDA().addEventListener(this);
    }

    /**
     * Add a slash command to the SlashCommand list
     *
     * @param module The SlashCommand module
     * @return this
     */
    public SlashCommandHandler addCommand(@NotNull final SimpleSlashCommand module) {
        // Retrieve the slash command data
        final SlashCommandData command = Commands.slash(module.getCommand(), module.getDescription());

        // add sub command groups
        for (final SubcommandGroupData subcommandGroup : module.getSubcommandGroup()) {
            command.addSubcommandGroups(subcommandGroup);
        }

        // add sub commands
        for (final SubcommandData subcommand : module.getSubCommands()) {
            command.addSubcommands(subcommand);
        }

        if (module.getSubCommands().isEmpty() && module.getSubcommandGroup().isEmpty()) {
            command.addOptions(module.getOptions());
        }

        command.setDefaultPermissions(DefaultMemberPermissions.enabledFor(module.getPermissions()));

        // TODO: When api has an update for the slash command system update it to that system
        //       Check it out here: https://github.com/DV8FromTheWorld/JDA/pull/2113
        // Add the slash command
        if (module.getGuildOnly()) {
            mainGuildSlashCommands.add(command);
            Debugger.debug("SlashCommandRegistration", "Guild only for: " + command.getName());
        } else {
            publicSlashCommands.add(command);
            Debugger.debug("SlashCommandRegistration", "Public command for: " + command.getName());
        }

        // Add it to our internal list
        cmdList.put(module.getCommand(), module);

        return this;
    }

    /**
     * Register all slash commands to JDA
     */
    public void registerCommands() {
//        for(Command command : SimpleBot.getJDA().retrieveCommands().complete()) {
//            SimpleBot.getJDA().deleteCommandById(command.getIdLong());
//        }

        // Check if the slash commands isn't empty
        if (mainGuildSlashCommands.isEmpty()) Common.warning("Be aware no main guild slash commands can be found!");
        if (publicSlashCommands.isEmpty()) Common.warning("Be aware no public slash commands can be found!");

        Debugger.debug("SlashCommand", "Registering slash commands");

        // Add all slash commands to the main guild
        SimpleBot.getMainGuild().updateCommands()
                .addCommands(mainGuildSlashCommands)
                .queue();

        // Add the commands to all the guilds PUBLIC!!
        SimpleBot.getJDA().updateCommands()
                .addCommands(publicSlashCommands)
                .queue();
    }

    /**
     * The main event listener for the slash command interaction event
     *
     * @param event SlashCommandInteractionEvent
     */
    @Override
    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        Debugger.debug("SlashCommand", "A Slash Command has been used");

        // Log who used a slash command
        Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Slash Command: " + ConsoleColor.CYAN + event.getCommandString() + ConsoleColor.RESET);

        // Retrieve the command class from the command that has been run
        final SimpleSlashCommand module = cmdList.get(event.getName());

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

        // Get the guild of the button and the main guild of the bot
        final Guild guild = event.getGuild();
        final Guild mainGuild = SimpleBot.getMainGuild();
        assert guild != null : "Event guild is null!";

        // Check if the button is for the main guild only
        if (!guild.getId().equals(mainGuild.getId()) && module.getGuildOnly()) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - Button main guild only")
                    .text(
                            "The button you used is only usable in the main guild of this bot!",
                            "If you feel like this is a problem please contact a admin!"
                    )
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        final Member member = event.getMember();
        final User user = event.getUser();

        // Run the command logic
        module.onCommand(event);
    }

    // TODO: Make the total count better so it will show the actually registered commands with JDA instead of the count
    //       of the lists that are made

    /**
     * Get the total amount of slash commands registered
     *
     * @return Total amount of slash commands
     */
    public int getTotal() {
        return cmdList.size();
    }

    /**
     * Get the total amount of public registered slash commands
     *
     * @return Total amount of public slash commands
     */
    public int getPublicTotal() {
        return publicSlashCommands.size();
    }

    /**
     * Get the total amount of private registered slash commands
     *
     * @return Total amount of private slash commands
     */
    public int getGuildTotal() {
        return mainGuildSlashCommands.size();
    }
}
