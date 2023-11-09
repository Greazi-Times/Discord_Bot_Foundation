package com.greazi.old.handlers.selectmenu.string;

import com.greazi.old.SimpleBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Duplicate this to the EntitySelectMenu
public abstract class SimpleStringSelectMenu {

    private final String menu_id;
    private String placeholder;
    private boolean disabled = false;
    private int min = 1;
    private int max = 1;
    private List<SelectOption> options = new ArrayList<>();
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
    public final boolean execute(final StringSelectInteraction event) {
        this.member = event.getMember();
        this.user = event.getUser();
        this.guild = event.getGuild();

        // TODO: Add menu checks here
        this.onMenuInteract(event);
        return true;
    }

    protected abstract void onMenuInteract(StringSelectInteraction event);

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
    public SimpleStringSelectMenu(final String menu_id) {
        this.menu_id = menu_id;
    }

    /**
     * Configure the placeholder which is displayed when no selections have been made yet.
     *
     * @param placeholder The placeholder or null
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the provided placeholder is empty or longer than 100 characters
     */
    public SimpleStringSelectMenu placeholder(final String placeholder) {
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
    public SimpleStringSelectMenu min(final int min) {
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
    public SimpleStringSelectMenu max(final int max) {
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
    public SimpleStringSelectMenu minMax(final int min, final int max) {
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
    public SimpleStringSelectMenu disabled(final boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param options The {@link SelectOption SelectOptions} to add
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25 or null is provided
     */
    public SimpleStringSelectMenu options(final SelectOption... options) {
        this.options = Arrays.asList(options);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param options The {@link SelectOption SelectOptions} to add
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25 or null is provided
     */
    public SimpleStringSelectMenu options(final List<SelectOption> options) {
        this.options = options;
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param label    The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param value    The value for the option used to indicate which option was selected with {@link StringSelectInteraction#getValues()},
     *                 up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param selected If selected is true this option is selected by default
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25, invalid null is provided,
     *                                  or any of the individual parameter requirements are violated.
     */
    public SimpleStringSelectMenu option(final String label, final String value, final boolean selected) {
        final SelectOption option = SelectOption.of(label, value)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param label       The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param value       The value for the option used to indicate which option was selected with {@link StringSelectInteraction#getValues()},
     *                    up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param description The description explaining the meaning of this option in more detail, up to 50 characters
     * @param selected    If selected is true this option is selected by default
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25, invalid null is provided,
     *                                  or any of the individual parameter requirements are violated.
     */
    public SimpleStringSelectMenu option(final String label, final String value, final String description, final boolean selected) {
        final SelectOption option = SelectOption.of(label, value)
                .withDescription(description)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param label    The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param value    The value for the option used to indicate which option was selected with {@link StringSelectInteraction#getValues()},
     *                 up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param emoji    The {@link Emoji} shown next to this option, or null
     * @param selected If selected is true this option is selected by default
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25, invalid null is provided,
     *                                  or any of the individual parameter requirements are violated.
     */
    public SimpleStringSelectMenu option(final String label, final String value, final Emoji emoji, final boolean selected) {
        final SelectOption option = SelectOption.of(label, value)
                .withEmoji(emoji)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param label       The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param value       The value for the option used to indicate which option was selected with {@link StringSelectInteraction#getValues()},
     *                    up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param description The description explaining the meaning of this option in more detail, up to 50 characters
     * @param emoji       The {@link Emoji} shown next to this option, or null
     * @param selected    If selected is true this option is selected by default
     * @return The same builder instance for chaining
     * @throws IllegalArgumentException If the total amount of options is greater than 25, invalid null is provided,
     *                                  or any of the individual parameter requirements are violated.
     */
    public SimpleStringSelectMenu option(final String label, final String value, final String description, final Emoji emoji, final boolean selected) {
        final SelectOption option = SelectOption.of(label, value)
                .withDescription(description)
                .withEmoji(emoji)
                .withDefault(selected);
        this.options.add(option);
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
     * Placeholder which is displayed when no selections have been made yet.
     *
     * @return The placeholder or null
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
     * List of options currently configured in this builder.
     *
     * @return The list of {@link SelectOption SelectOptions}
     */
    public List<SelectOption> getOptions() {
        return options;
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
    public StringSelectMenu build() {
        final StringSelectMenu.Builder builder = StringSelectMenu.create(this.menu_id);
        builder.setPlaceholder(this.placeholder);
        builder.setDisabled(this.disabled);
        builder.setRequiredRange(this.min, this.max);
        builder.addOptions(this.options);
        SimpleBot.getStringSelectMenuHandler().addMenuListener(this);
        return builder.build();
    }

    public void remove() {
        SimpleBot.getStringSelectMenuHandler().removeMenuListener(this);
    }
}
