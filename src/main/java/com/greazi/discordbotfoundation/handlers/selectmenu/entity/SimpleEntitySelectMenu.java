package com.greazi.discordbotfoundation.handlers.selectmenu.entity;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.Arrays;
import java.util.List;

// TODO: Duplicate this to the EntitySelectMenu
public abstract class SimpleEntitySelectMenu {

    private final String menu_id;
    private String placeholder = null;
    private boolean disabled = false;
    private int min = 1;
    private int max = 1;
    private List<EntitySelectMenu.SelectTarget> targetType = null;
    private Member member = null;
    private User user = null;
    private Guild guild = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the menu logic
     *
     * @param event StringSelectInteraction
     */
    public final boolean execute(final EntitySelectInteraction event) {
        this.member = event.getMember();
        this.user = event.getUser();
        this.guild = event.getGuild();

        // TODO: Add menu checks here
        this.onMenuInteract(event);
        return true;
    }

    protected abstract void onMenuInteract(EntitySelectInteraction event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Change the custom id used to identify the select menu.
     *
     * @param menu_id The new custom id to use
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided id is null, empty, or longer than 100 characters
     */
    public SimpleEntitySelectMenu(final String menu_id) {
        this.menu_id = menu_id;
    }

    /**
     * Configure the placeholder which is displayed when no selections have been made yet.
     *
     * @param placeholder The placeholder or null
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided placeholder is empty or longer than 100 characters
     */
    public SimpleEntitySelectMenu placeholder(final String placeholder) {
        this.placeholder = placeholder;
        return this;
    }


    /**
     * The minimum amount of values a user has to select.
     * <br>Default: {@code 1}
     *
     * <p>The minimum must not exceed the amount of available options.
     *
     * @param min The min values
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided amount is negative or greater than 25
     */
    public SimpleEntitySelectMenu min(final int min) {
        this.min = min;
        return this;
    }

    /**
     * The maximum amount of values a user can select.
     * <br>Default: {@code 1}
     *
     * <p>The maximum must not exceed the amount of available options.
     *
     * @param max The max values
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided amount is less than 1 or greater than 25
     */
    public SimpleEntitySelectMenu max(final int max) {
        this.max = max;
        return this;
    }

    /**
     * The minimum and maximum amount of values a user can select.
     * <br>Default: {@code 1} for both
     *
     * <p>The minimum or maximum must not exceed the amount of available options.
     *
     * @param min The min values
     * @param max The max values
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided amount is not a valid range ({@code 0 <= min <= max})
     */
    public SimpleEntitySelectMenu minMax(final int min, final int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    /**
     * Configure whether this select menu should be disabled.
     * <br>Default: {@code false}
     *
     * @param disabled Whether this menu is disabled
     * @return The same builder instance for chaining
     */
    public SimpleEntitySelectMenu disabled(final boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param targetType The {@link EntitySelectMenu.SelectTarget SelectTarget} to add
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25 or null is provided
     */
    public SimpleEntitySelectMenu targetType(final EntitySelectMenu.SelectTarget... targetType) {
        this.targetType = Arrays.asList(targetType);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param targetType The {@link EntitySelectMenu.SelectTarget SelectTarget} to add
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25 or null is provided
     */
    public SimpleEntitySelectMenu options(final List<EntitySelectMenu.SelectTarget> targetType) {
        this.targetType = targetType;
        return this;
    }

    // ----------------------------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------------------------

    /**
     * The custom id used to identify the select menu.
     *
     * @return The custom id
     */
    public String getId() {
        return menu_id;
    }

    /**
     * The placeholder which is displayed when no selections have been made yet.
     * @return The placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * The minimum amount of values a user has to select.
     *
     * @return The min values
     */
    public int getMin() {
        return min;
    }

    /**
     * The maximum amount of values a user can select at once.
     *
     * @return The max values
     */
    public int getMax() {
        return max;
    }

    /**
     * Whether the menu is disabled or not
     *
     * @return True if this menu is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * The Target Type of the select menu
     *
     * @return The list of {@link EntitySelectMenu.SelectTarget SelectTarget}
     */
    public List<EntitySelectMenu.SelectTarget> getTargetType() {
        return targetType;
    }

    public Member getMember() {
        return this.member;
    }

    public User getUser() {
        return this.user;
    }

    public Guild getGuild() {
        return this.guild;
    }

    /**
     * Get the select menu
     *
     * @return the select menu
     */
    public EntitySelectMenu build() {
        final EntitySelectMenu.Builder builder = EntitySelectMenu.create(this.menu_id, this.targetType);

        builder.setDisabled(this.disabled);
        builder.setRequiredRange(this.min, this.max);
        builder.setPlaceholder(this.placeholder);

        SimpleBot.getEntitySelectMenuHandler().addMenuListener(this);

        return builder.build();
    }

    public void remove() {
        SimpleBot.getEntitySelectMenuHandler().removeMenuListener(this);
    }
}
