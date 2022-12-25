package com.greazi.discordbotfoundation.handlers.modals;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
     * The main modal handler method
     */
    public ModalHandler() {
        Debugger.debug("Modal", "Modals main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    /**
     * Add the modal listener
     *
     * @param module
     * @return this
     */
    public ModalHandler addModalListener(final SimpleModal module) {
        modalList.put(module.getId(), module);
        return this;
    }

    public void removeModalListener(final SimpleModal module) {
        modalList.remove(module.getId());
    }

    /**
     * Get a modal by id
     *
     * @param modal_id
     * @return The modal
     */
    public SimpleModal getModal(final String modal_id) {
        return modalList.get(modal_id);
    }

    /**
     * The event listener for the modals
     *
     * @param event ModalInteractionEvent
     */
    @Override
    @SubscribeEvent
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        Debugger.debug("Modal", "A modal has been submitted");

        // Log who used a modal
        Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Modal: " + ConsoleColor.CYAN + event.getId() + ConsoleColor.RESET);

        // Retrieve the modal class from the command that has been run
        final SimpleModal module = modalList.get(event.getModalId());

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

        // Get the guild of the button and the main guild of the bot
        final Guild guild = event.getGuild();
        final Guild mainGuild = SimpleBot.getMainGuild();
        assert guild != null : "Event guild is null!";

        // Check if the button is for the main guild only
        if (!guild.getId().equals(mainGuild.getId()) && module.isMainGuildOnly()) {
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

        Debugger.debug("Modal", "Executing modal logic");
        // Execute modal logic
        module.execute(event);
    }

    /**
     * Get the total amount of modals registered
     *
     * @return Total amount of modals
     */
    public int getTotal() {
        return modalList.size();
    }
}
