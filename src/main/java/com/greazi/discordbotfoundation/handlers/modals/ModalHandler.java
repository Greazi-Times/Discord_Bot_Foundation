package com.greazi.discordbotfoundation.handlers.modals;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class ModalHandler extends ListenerAdapter {

    public ModalHandler(){
        Debugger.debug("Modals", "Modals main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    private final HashMap<String, SimpleModal> modalList = new HashMap<>();

    public ModalHandler addModalListener(SimpleModal module) {
        modalList.put(module.getId(), module);
        return this;
    }

    public SimpleModal getModal(String modal_id){
        return modalList.get(modal_id);
    }

    @Override
    @SubscribeEvent
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Debugger.debug("Modals", "A modal has been submitted");

        // Retrieve the modal class from the command that has been run
        SimpleModal module = modalList.get(event.getModalId());

        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - modal not found")
                    .text("The modal you used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        Debugger.debug("Modals", "Found event; " + module);

        if (module.isMainGuildOnly() && !Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.Bot.MainGuild())){
            return;
        }

        Debugger.debug("Modals", "  Executing modal logic");
        module.execute(event);
    }

}
