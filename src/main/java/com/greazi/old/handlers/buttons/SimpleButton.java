/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.old.handlers.buttons;

import com.greazi.old.SimpleBot;
import com.greazi.old.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class that lets you create a button
 */
public abstract class SimpleButton {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * Set the button like "example"
     */
    private String button_id = "example";

    /**
     * Set the button its style
     * OPTIONS: Primary, Success, Secondary, Destructive
     */
    private ButtonStyle button_style = ButtonStyle.PRIMARY;

    /**
     * Set the help description for the button
     */
    private String label = "example label";

    /**
     * Set the url for the button
     */
    private String url = null;

    /**
     * Add an emoji to the button
     */
    private Emoji emoji = null;

    /**
     * Make the button disabled (Grayed out)
     */
    private boolean disabled = false;

    /**
     * Is the button bound to only the main guild of the bot
     */
    private boolean guildOnly = false;

    /**
     * Is the button bound to a NSFW channel
     * (This means if it can only be used in a NSFW channel)
     */
    private boolean nsfwOnly = false;

    /**
     * Set the roles that can use this button
     */
    private List<Role> allowedRoles = new ArrayList<>();

    /**
     * Set the users that can use this button
     */
    private List<User> allowedUsers = new ArrayList<>();

    /**
     * Set the roles that can not use this button
     */
    private List<Role> blockedRoles = new ArrayList<>();

    /**
     * Set the users that can not use this button
     */
    private List<User> blockedUsers = new ArrayList<>();

    private Member member = null;
    private User user = null;
    private Guild guild = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    public final boolean execute(final ButtonInteractionEvent event) {
        this.member = event.getMember();
        this.user = event.getUser();
        this.guild = event.getGuild();

        if (this.guildOnly && !event.isFromGuild()) {
            event.replyEmbeds(
                    new SimpleEmbedBuilder("Main Guild Only", false)
                            .text("This command can only be used in the main guild of the bot")
                            .build()
            ).setEphemeral(true).queue();
            return false;
        }

        boolean canUse = true;

        if (!this.allowedUsers.isEmpty() && !this.allowedUsers.contains(event.getUser())) canUse = false;
        if (!this.blockedUsers.isEmpty() && this.blockedUsers.contains(event.getUser())) canUse = false;

        if (canUse) {
            this.onButtonInteract(event);
            return true;
        } else {
            event.replyEmbeds(
                    new SimpleEmbedBuilder("Missing Permissions", false)
                            .text("You are not allowed to execute this button")
                            .build()
            ).setEphemeral(true).queue();
            return false;
        }
    }

    /**
     * Run the button logic
     *
     * @param event ButtonInteractionEvent
     */
    protected abstract void onButtonInteract(ButtonInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the button
     */
    public SimpleButton(final String button_id) {
        this.button_id = button_id;
    }

    /**
     * Set the button its style
     * OPTIONS: Primary, Success, Secondary, Destructive, Link
     */
    public void buttonStyle(final ButtonStyle button_style) {
        this.button_style = button_style;
    }

    /**
     * Set the emoji of the button
     *
     * @param emoji The emoji that needs to be displayed
     */
    public void emoji(final Emoji emoji) {
        this.emoji = emoji;
    }

    /**
     * Set the url for the button
     *
     * @param url The url for the button
     */
    public void url(final String url) {
        this.url = url;
    }

    /**
     * Set the button disabled (Grayed out)
     *
     * @param disabled Should the button be disabled
     */
    public void disabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Set the label of the button
     *
     * @param label The label name
     */
    public void label(final String label) {
        this.label = label;
    }

    /**
     * Set this button as main guild only
     */
    public void mainGuildOnly() {
        this.guildOnly = true;
    }

    /**
     * Set whether the button can only be used inside a NSFW channel
     */
    public void nsfwOnly() {
        this.nsfwOnly = true;
    }

    /**
     * Set the list of roles that can use this button
     */
    public void enabledRoles(final List<Role> roles) {
        this.allowedRoles = roles;
    }

    /**
     * Set the list of roles that can use this button
     */
    public void enabledRoles(final Role... roles) {
        this.allowedRoles = Arrays.asList(roles);
    }

    public void enabledUsers(final List<User> users) {
        this.allowedUsers = users;
    }

    /**
     * Set the list users that can use this button
     */
    public void enabledUsers(final User... users) {
        this.allowedUsers = Arrays.asList(users);
    }

    /**
     * Set the list of roles that can not use the button
     */
    public void disabledRoles(final Role... roles) {
        this.blockedRoles = Arrays.asList(roles);
    }

    /**
     * Set the list of roles that can not use the button
     */
    public void disabledRoles(final List<Role> roles) {
        this.blockedRoles = roles;
    }

    /**
     * Set the list of users that can not use the button
     */
    public void disabledUsers(final User... users) {
        this.blockedUsers = Arrays.asList(users);
    }

    /**
     * Set the list of users that can not use the button
     */
    public void disabledUsers(final List<User> users) {
        this.blockedUsers = users;
    }

    // ----------------------------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------------------------

    /**
     * Get the button ID
     *
     * @return The button ID
     */
    protected final String getId() {
        return button_id;
    }

    /**
     * Get the button style
     *
     * @return The button style
     */
    protected final ButtonStyle getButtonStyle() {
        return button_style;
    }

    /**
     * Get the button Label
     *
     * @return The button Label
     */
    protected final String getLabel() {
        return label;
    }

    /**
     * Get the button Emoji
     *
     * @return The button Emoji
     */
    protected final Emoji getEmoji() {
        return this.emoji;
    }

    /**
     * Get the URL for the button
     *
     * @return The URL
     */
    protected final String getUrl() {
        return this.url;
    }

    /**
     * Whether the button is restricted to the main guild of the bot
     *
     * @return The restricted guild
     */
    protected final boolean getGuildOnly() {
        return guildOnly;
    }

    /**
     * Whether the button can only be used inside a NSFW channel
     *
     * @return the restriction to a NSFW channel
     */
    protected final boolean getNsfwOnly() {
        return nsfwOnly;
    }

    /**
     * Returns a list of the roles that can use this button
     *
     * @return the allowed roles
     */
    protected final List<Role> getAllowedRoles() {
        return allowedRoles;
    }

    /**
     * Returns a list of users that can use this button
     *
     * @return the allowed users
     */
    protected final List<User> getAllowedUsers() {
        return allowedUsers;
    }

    /**
     * Returns a list of roles that can not use the button
     *
     * @return the disallowed roles
     */
    protected final List<Role> getDisallowedRoles() {
        return blockedRoles;
    }

    /**
     * Returns a list of users that can not use the button
     *
     * @return the disallowed users
     */
    protected final List<User> getDisallowedUsers() {
        return blockedUsers;
    }

    protected final Member getMember() {
        return this.member;
    }

    protected final User getUser() {
        return this.user;
    }

    protected final Guild getGuild() {
        return this.guild;
    }

    // ----------------------------------------------------------------------------------------
    // Button creator
    // ----------------------------------------------------------------------------------------

    /**
     * Set the button its style
     * OPTIONS: Primary, Success, Secondary, Destructive
     */
    public Button build() {
        Checks.notEmpty(this.button_id, "ID");
        Checks.notLonger(this.button_id, Button.ID_MAX_LENGTH, "ID");
        Checks.notNull(this.button_style, "Style");
        Checks.check(this.button_style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
        if (this.emoji == null) {
            Checks.notEmpty(this.label, "Label");
        }
        Checks.notLonger(this.label, Button.LABEL_MAX_LENGTH, "Label");
        SimpleBot.getButtonHandler().addButtonListener(this);
        return new ButtonImpl(this.button_id, this.label, this.button_style, this.url, this.disabled, this.emoji);
    }

    public void remove() {
        SimpleBot.getButtonHandler().removeButtonListener(this);
    }
}
