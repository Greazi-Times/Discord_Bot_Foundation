package com.greazi.discordbotfoundation.handlers.selectmenu;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SelectMenuHandler extends ListenerAdapter {

    public SelectMenuHandler(){
        Debugger.debug("SelectMenus", "SelectMenus main method");
        SimpleBot.getJDA().addEventListener(this);
    }

    private final HashMap<String, SimpleSelectMenu> menuList = new HashMap<>();

    public SelectMenuHandler addMenuListener(SimpleSelectMenu module) {
        menuList.put(module.getId(), module);
        return this;
    }

    public SimpleSelectMenu getMenu(String modal_id){
        return menuList.get(modal_id);
    }

    @Override
    @SubscribeEvent
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        SimpleSelectMenu module = menuList.get(event.getSelectMenu().getId());
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
        module.execute(event);
    }

}
