/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.buttons;

import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;


// TODO: Make a proper debug message return
public class ButtonHandler extends ListenerAdapter {

    private final HashMap<String, SimpleButton> buttonList = new HashMap<>();

    public ButtonHandler addButtonListener(SimpleButton module) {

        buttonList.put(module.getbutton(), module);

        return this;
    }


    // TODO: Add a method that check disabled and enabled methods before running the button's code
    @Override
    @SubscribeEvent
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Debugger.debug("Buttons", "A button has been pressed");
        // Retrieve the command class from the command that has been run
        SimpleButton module = buttonList.get(event.getId());

        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - button not found")
                    .text("The button you used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        Debugger.debug("Buttons", "Found event; " + module);

        if (module.getGuildOnly() && !Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.Bot.MainGuild())){
            return;
        }

        if (event.getTextChannel().isNSFW() && !module.getNsfwOnly()){
            return;
        }

        Debugger.debug("Button", "  Executing command logic");
        module.execute(event);
    }
}
