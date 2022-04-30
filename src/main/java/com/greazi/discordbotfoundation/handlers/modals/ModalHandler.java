package com.greazi.discordbotfoundation.handlers.modals;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * The modal handler that handles the whole modal event.
 * Uses the information of {@link SimpleModal}
 */
public class ModalHandler extends ListenerAdapter {

    /**
     * The HashMap with all modals
     */
    private final HashMap<String, SimpleModal> modalList = new HashMap<>();

    /**
     *  The main modal handler method
     */
    public ModalHandler(){
        Debugger.debug("Modal", "Modals main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    /**
     * Add the modal listener
     * @param module
     * @return this
     */
    public ModalHandler addModalListener(SimpleModal module) {
        modalList.put(module.getId(), module);
        return this;
    }

    /**
     * Get a modal by id
     * @param modal_id
     * @return The modal
     */
    public SimpleModal getModal(String modal_id){
        return modalList.get(modal_id);
    }

    /**
     * The event listener for the modals
     * @param event ModalInteractionEvent
     */
    @Override
    @SubscribeEvent
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Debugger.debug("Modal", "A modal has been submitted");

        // Log who used a modal
        Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Modal: " + ConsoleColor.CYAN + event.getId() + ConsoleColor.RESET);

        // Retrieve the modal class from the command that has been run
        SimpleModal module = modalList.get(event.getModalId());

        // Check if the modal exists
        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - modal not found")
                    .text("The modal you used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        Debugger.debug("Modal", "Found event; " + module);

        // Check if the modal is main Guild only
        if (module.isMainGuildOnly() && !Objects.requireNonNull(event.getGuild()).getId().equals(SimpleSettings.Bot.MainGuild())){
            return;
        }

        Debugger.debug("Modal", "Executing modal logic");
        // Execute modal logic
        module.execute(event);
    }

}
