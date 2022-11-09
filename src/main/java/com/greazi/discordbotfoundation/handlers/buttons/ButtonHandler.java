/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.buttons;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The button handler that handles the whole button event.
 * Uses the information of {@link SimpleButton}
 */
public class ButtonHandler extends ListenerAdapter {

	public ButtonHandler() {
		Debugger.debug("Button", "Buttons main method");
		SimpleBot.getJDA().addEventListener(this);
	}

	/**
	 * A HasMap of all the buttons that have been added
	 */
	private final HashMap<String, SimpleButton> buttonList = new HashMap<>();

	/**
	 * Add the button to the button list
	 *
	 * @param module The button module
	 */
	public void addButtonListener(final SimpleButton module) {
		Debugger.debug("Button", "Adding new button: " + module.getId());
		buttonList.put(module.getId(), module);
	}

	/**
	 * Get a button by id
	 *
	 * @param button_id The button id
	 * @return this {@link SimpleButton}
	 */
	public SimpleButton getButton(final String button_id) {
		return buttonList.get(button_id);
	}

	/**
	 * The main event listener for the {@link ButtonInteractionEvent} event of JDA
	 *
	 * @param event ButtonInteractionEvent
	 */
	@Override
	@SubscribeEvent
	public void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
		Common.log("User, " + ConsoleColor.CYAN + event.getMember().getEffectiveName() + ConsoleColor.RESET + " used Button: " + ConsoleColor.CYAN + event.getButton().getId() + ConsoleColor.RESET);

		// Retrieve the button class from the button that has been pressed
		final SimpleButton module = buttonList.get(event.getButton().getId());

		// Check if the button exists
		if (module == null) {
			event.replyEmbeds(new SimpleEmbedBuilder("ERROR - Button not found")
					.text(
							"The button you used does not exist or hasn't been activated!",
							"Please contact an admin and report this error!"
					)
					.error()
					.setFooter("")
					.build()).setEphemeral(true).queue();
			return;
		}

		// TODO: FIX THIS ASAP
		//if (event.getGuild() == SimpleBot.getMainGuild()) {
		//	event.replyEmbeds(new SimpleEmbedBuilder("ERROR - Button main guild only")
		//			.text(
		//					"The button you used is only usable in the main guild of this bot!",
		//					"If you feel like this is a problem please contact a admin!"
		//			)
		//			.error()
		//			.setFooter("")
		//			.build()).setEphemeral(true).queue();
		//	return;
		//}

		// If the button hasn't been pressed inside the main guild
		//if (!Objects.requireNonNull(event.getGuild()).getId().equals(SimpleBot.getMainGuild()) && module.getGuildOnly()) {
		//	event.replyEmbeds(new SimpleEmbedBuilder("ERROR - Button main guild only")
		//			.text(
		//					"The button you used is only usable in the main guild of this bot!",
		//					"If you feel like this is a problem please contact a admin!"
		//			)
		//			.error()
		//			.setFooter("")
		//			.build()).setEphemeral(true).queue();
		//	return;
		//}

		// If the button is pressed inside a NSFW channel
		if (!event.getChannel().asTextChannel().isNSFW() && module.getNsfwOnly()) {
			event.replyEmbeds(new SimpleEmbedBuilder("ERROR - Button NSFW only")
					.text(
							"The button you used is only usable in a NSWF channel!",
							"If you feel like this is a problem please contact a admin!"
					)
					.error()
					.setFooter("")
					.build()).setEphemeral(true).queue();
			return;
		}

		// TODO: Fix this ASAP AS WELL

//        // Get the member from the event
//        Member member = event.getMember();
//
//        // If the member is allowed to use the button
//        if(module.getDisabledUsers().contains(member.getIdLong())){
//            return;
//        }
//        if(!module.getEnabledUsers().isEmpty()){
//            if (!module.getEnabledUsers().contains(member.getIdLong())) {
//                return;
//            }
//        }
//
//        // If the member has the role to use the button
//        if (member.getRoles().contains(module.getDisabledRoles())) {
//            return;
//        }
//        if(!module.getEnabledRoles().isEmpty()){
//            if (member.getRoles().containsAll(module.getEnabledRoles())) {
//                //run
//            }
//        }

		// If all checks are oke than execute the button logic
		module.execute(event);
	}

	/**
	 * Get the total amount of registered buttons
	 *
	 * @return Total registered buttons
	 */
	public int getTotal() {
		return buttonList.size();
	}

}
