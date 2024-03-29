package com.greazi.discordbotfoundation.handlers.selectmenu.string;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The selection menu andler that handles the whole selection menu event.
 * Uses the information of {@link SimpleStringSelectMenu}
 */
public class StringSelectMenuHandler extends ListenerAdapter {

    /**
     * The HasMap for the menus
     */
    private final HashMap<String, SimpleStringSelectMenu> menuList = new HashMap<>();

    /**
     * The select menu handler
     */
    public StringSelectMenuHandler() {
        SimpleBot.getJDA().addEventListener(this);
    }

    /**
     * Add a new Menu Listener
     *
     * @param module
     * @return this
     */
    public StringSelectMenuHandler addMenuListener(final SimpleStringSelectMenu module) {
        menuList.put(module.getId(), module);
        return this;
    }

    public void removeMenuListener(final SimpleStringSelectMenu module) {
        menuList.remove(module.getId());
    }

    /**
     * Get a menu by its ID
     *
     * @param modal_id
     * @return The menu from an id
     */
    public SimpleStringSelectMenu getMenu(final String modal_id) {
        return menuList.get(modal_id);
    }

    /**
     * The event listener for menus
     *
     * @param event SelectMenuInteractionEvent
     */
    @SubscribeEvent
    public void onSelectMenuInteraction(@NotNull final StringSelectInteractionEvent event) {

        // Get the list of menus
        final SimpleStringSelectMenu module = menuList.get(event.getSelectMenu().getId());

        Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Menu: " + ConsoleColor.CYAN + event.getSelectMenu().getId() + ConsoleColor.RESET);

        // Check if the select menu exists
        if (module == null) {
            event.replyEmbeds(new SimpleEmbedBuilder("ERROR - menu not found")
                    .text("The select menu you used does not exist or hasn't been activated!",
                            "Please contact an admin and report this error!")
                    .error()
                    .setFooter("")
                    .build()).setEphemeral(true).queue();
            return;
        }

        // Execute the menu logic
        module.execute(event);
    }

    /**
     * Get the total amount of menus registered
     *
     * @return Total amount of menus
     */
    public int getTotal() {
        return menuList.size();
    }
}
