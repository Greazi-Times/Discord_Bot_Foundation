package com.greazi.discordbotfoundation.command;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SlashCommandHandler extends ListenerAdapter {

    public SlashCommandInteractionEvent event;

    public SlashCommandHandler() {
        Common.log("Starting up SlashCommandHandler...");

        SimpleBot.getJda().addEventListener(this);
    }

    @Override
    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        final String commandId = event.getCommandId();
        Debugger.debug("SlashCommandHandler", "Slash command received: " + commandId);

        this.execute(event);
    }

    protected abstract void execute(@NotNull final SlashCommandInteractionEvent event);
}
