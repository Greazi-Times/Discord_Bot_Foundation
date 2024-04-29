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
import java.util.Optional;

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

        if (simpleCommand == null) {
            Common.error("Command not found: " + commandName);
            return;
        }

        if (simpleCommand.isCoreCommand()) {
            simpleCommand.execute(event);
        } else {
            final Optional<SimpleCommand> sub = simpleCommand
                    .getSubcommandsList()
                    .stream()
                    .filter(subcommand -> subcommand.getCommand().equals(event.getSubcommandName()))
                    .findFirst();

            if (sub.isEmpty()) {
                Common.error("Subcommand not found: " + event.getSubcommandName());
                return;
            }

            sub.get().execute(event);
        }

    }

    public static void register(final SimpleCommand simpleCommand) {
        commandList.put(simpleCommand.getCommand(), simpleCommand);
    }

    /**
     * Register all slash commands to JDA
     */
    public static void registerCommands() {

        final List<CommandData> slashCommands = new ArrayList<>();
        int guildOnly = 0;
        int publicOnly = 0;

        for (final SimpleCommand simpleCommand : commandList.values()) {
            slashCommands.add(simpleCommand.buildSlashCommand());
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
