/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.handlers.buttons;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SimpleButton {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    /**
     * Set the button like "/button"
     */
    private String button_id = "example";

    /**
     * Set the help description for the button
     */
    private String description = "No Description";

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
    public void setbutton(String button_id) {
        this.button_id = button_id;
    }

    /**
     * Set the description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Returns the button
     *
     * @return the button
     */
    public String getbutton() {
        return button_id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Whether the button is restricted to the main guild of the bot
     *
     * @return the restricted guild
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
}
