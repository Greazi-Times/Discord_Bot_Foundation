package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlashCommandHandler extends ListenerAdapter {

    public SlashCommandInteractionEvent event;

    private static final HashMap<String, SimpleCommand> commandList = new HashMap<>();

    public SlashCommandHandler() {
        Common.log("Starting up SlashCommandHandler...");

        SimpleBot.getJda().addEventListener(this);
    }

    @Override
    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        Common.log("Slash command interaction received: " + event.getName());
        final String commandName = event.getName();
        final SimpleCommand simpleCommand = commandList.get(commandName);
        simpleCommand.execute(event, simpleCommand);
    }

    public static void register(final SimpleCommand simpleCommand) {
        final String commandName = simpleCommand.getCommandData().getName();

        commandList.put(commandName, simpleCommand);
    }

    /**
     * Register all slash commands to JDA
     */
    public static void registerCommands() {

        final List<CommandData> slashCommands = new ArrayList<>();
        int guildOnly = 0;
        int publicOnly = 0;

        for (final SimpleCommand simpleCommand : commandList.values()) {
            slashCommands.add(simpleCommand.getCommandData());
            if (simpleCommand.isMainGuildOnly()) {
                guildOnly++;
            } else {
                publicOnly++;
            }
        }

        SimpleBot.getJda().updateCommands().addCommands(slashCommands).queue();

        Common.log("Registered " + guildOnly + " guild-only slash commands and " + publicOnly + " public slash commands");
    }
}
