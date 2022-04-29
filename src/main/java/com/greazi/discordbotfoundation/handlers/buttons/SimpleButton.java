/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.buttons;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
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
    private List<Role> enabledRoles = new ArrayList<>();

    /**
     * Set the users that can use this button
     */
    private List<User> enabledUsers = new ArrayList<>();

    /**
     * Set the roles that can not use this button
     */
    private List<Role> disabledRoles = new ArrayList<>();

    /**
     * Set the users that can not use this button
     */
    private List<User> disabledUsers = new ArrayList<>();
    
    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the button logic
     *
     * @param event ButtonInteractionEvent
     */
    protected abstract void execute(ButtonInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the button
     */
    public void setButton(String button_id) {
        this.button_id = button_id;
    }

    /**
     * Set the button its style
     * OPTIONS: Primary, Success, Secondary, Destructive, Link
     */
    public void setButtonStyle(ButtonStyle button_style) {
        this.button_style = button_style;
    }

    /**
     * Set the emoji of the button
     * @param emoji The emoji that needs to be displayed
     */
    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    /**
     * Set the url for the button
     * @param url The url for the button
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Set the button disabled (Grayed out)
     * @param disabled Should the button be disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Set the label of the button
     * @param label The label name
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Set this button as main guild only
     */
    public void setMainGuildOnly() {
        this.guildOnly = true;
    }

    /**
     * Set whether the button can only be used inside a NSFW channel
     */
    public void setNsfwOnly() {
        this.nsfwOnly = true;
    }

    /**
     * Set the list of roles that can use this button
     */
    public void setEnabledRoles(List<Role> roles) {
        this.enabledRoles = roles;
    }

    /**
     * Set the list of roles that can use this button
     */
    public void setEnabledRoles(Role... roles) {
        this.enabledRoles = Arrays.asList(roles);
    }

    public void setEnabledUsers(List<User> users) {
        this.enabledUsers = users;
    }

    /**
     * Set the list users that can use this button
     */
    public void setEnabledUsers(User... users) {
        this.enabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of roles that can not use the button
     */
    public void setDisabledRoles(Role... roles) {
        this.disabledRoles = Arrays.asList(roles);
    }

    /**
     * Set the list of roles that can not use the button
     */
    public void setDisabledRoles(List<Role> roles) {
        this.disabledRoles = roles;
    }

    /**
     * Set the list of users that can not use the button
     */
    public void setDisabledUsers(User... users) {
        this.disabledUsers = Arrays.asList(users);
    }

    /**
     * Set the list of users that can not use the button
     */
    public void setDisabledUsers(List<User> users) {
        this.disabledUsers = users;
    }

    // ----------------------------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------------------------

    /**
     * Get the button ID
     *
     * @return The button ID
     */
    public String getButton() {
        return button_id;
    }

    /**
     * Get the button style
     *
     * @return The button style
     */
    public ButtonStyle getButtonStyle() {
        return button_style;
    }

    /**
     * Get the button Label
     * @return The button Label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the button Emoji
     * @return The button Emoji
     */
    public Emoji getEmoji() {
        return this.emoji;
    }

    /**
     * Get the URL for the button
     * @return The URL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Whether the button is restricted to the main guild of the bot
     *
     * @return The restricted guild
     */
    public boolean getGuildOnly() {
        return guildOnly;
    }

    /**
     * Whether the button can only be used inside a NSFW channel
     *
     * @return the restriction to a NSFW channel
     */
    public boolean getNsfwOnly() {
        return nsfwOnly;
    }

    /**
     * Returns a list of the roles that can use this button
     *
     * @return the allowed roles
     */
    public List<Role> getEnabledRoles() {
        return enabledRoles;
    }

    /**
     * Returns a list of users that can use this button
     *
     * @return the allowed users
     */
    public List<User> getEnabledUsers() {
        return enabledUsers;
    }

    /**
     * Returns a list of roles that can not use the button
     *
     * @return the disallowed roles
     */
    public List<Role> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * Returns a list of users that can not use the button
     *
     * @return the disallowed users
     */
    public List<User> getDisabledUsers() {
        return disabledUsers;
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
        Checks.notEmpty(this.label, "Label");
        Checks.notLonger(this.label, Button.LABEL_MAX_LENGTH, "Label");
        return new ButtonImpl(this.button_id, this.label, this.button_style, this.url, this.disabled, this.emoji);
    }
}
