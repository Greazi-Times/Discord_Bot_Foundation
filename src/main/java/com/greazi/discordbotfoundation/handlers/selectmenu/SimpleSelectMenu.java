package com.greazi.discordbotfoundation.handlers.selectmenu;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.*;

public abstract class SimpleSelectMenu {

    private String menu_id;
    private String placeholder;
    private boolean disabled = false;
    private int min = 1;
    private int max = 1;
    private List<SelectOption> options = new ArrayList<>();

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the menu logic
     *
     * @param event SelectMenuInteraction
     */
    protected abstract void execute(SelectMenuInteraction event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Change the custom id used to identify the select menu.
     *
     * @param  menu_id
     *         The new custom id to use
     *
     * @throws IllegalArgumentException
     *         If the provided id is null, empty, or longer than 100 characters
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu(String menu_id) {
        this.menu_id = menu_id;
    }

    /**
     * Configure the placeholder which is displayed when no selections have been made yet.
     *
     * @param  placeholder
     *         The placeholder or null
     *
     * @throws IllegalArgumentException
     *         If the provided placeholder is empty or longer than 100 characters
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * The minimum amount of values a user has to select.
     * <br>Default: {@code 1}
     *
     * <p>The minimum must not exceed the amount of available options.
     *
     * @param  min
     *         The min values
     *
     * @throws IllegalArgumentException
     *         If the provided amount is negative or greater than 25
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu min(int min) {
        this.min = min;
        return this;
    }

    /**
     * The maximum amount of values a user can select.
     * <br>Default: {@code 1}
     *
     * <p>The maximum must not exceed the amount of available options.
     *
     * @param  max
     *         The max values
     *
     * @throws IllegalArgumentException
     *         If the provided amount is less than 1 or greater than 25
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu max(int max) {
        this.max = max;
        return this;
    }

    /**
     * The minimum and maximum amount of values a user can select.
     * <br>Default: {@code 1} for both
     *
     * <p>The minimum or maximum must not exceed the amount of available options.
     *
     * @param  min
     *         The min values
     * @param  max
     *         The max values
     *
     * @throws IllegalArgumentException
     *         If the provided amount is not a valid range ({@code 0 <= min <= max})
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu minMax(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    /**
     * Configure whether this select menu should be disabled.
     * <br>Default: {@code false}
     *
     * @param  disabled
     *         Whether this menu is disabled
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  options
     *         The {@link SelectOption SelectOptions} to add
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25 or null is provided
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu options(SelectOption... options) {
        this.options = Arrays.asList(options);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  options
     *         The {@link SelectOption SelectOptions} to add
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25 or null is provided
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu options(List<SelectOption> options) {
        this.options = options;
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  label
     *         The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param  value
     *         The value for the option used to indicate which option was selected with {@link SelectMenuInteraction#getValues()},
     *         up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param  selected
     *         If selected is true this option is selected by default
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25, invalid null is provided,
     *         or any of the individual parameter requirements are violated.
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu option(String label, String value, boolean selected){
        SelectOption option = SelectOption.of(label, value)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  label
     *         The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param  value
     *         The value for the option used to indicate which option was selected with {@link SelectMenuInteraction#getValues()},
     *         up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param  description
     *         The description explaining the meaning of this option in more detail, up to 50 characters
     * @param  selected
     *         If selected is true this option is selected by default
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25, invalid null is provided,
     *         or any of the individual parameter requirements are violated.
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu option(String label, String value, String description, boolean selected){
        SelectOption option = SelectOption.of(label, value)
                .withDescription(description)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  label
     *         The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param  value
     *         The value for the option used to indicate which option was selected with {@link SelectMenuInteraction#getValues()},
     *         up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param  emoji
     *         The {@link Emoji} shown next to this option, or null
     * @param  selected
     *         If selected is true this option is selected by default
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25, invalid null is provided,
     *         or any of the individual parameter requirements are violated.
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu option(String label, String value, Emoji emoji, boolean selected){
        SelectOption option = SelectOption.of(label, value)
                .withEmoji(emoji)
                .withDefault(selected);
        this.options.add(option);
        return this;
    }

    /**
     * Adds up to 25 possible options to this select menu.
     *
     * @param  label
     *         The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters
     * @param  value
     *         The value for the option used to indicate which option was selected with {@link SelectMenuInteraction#getValues()},
     *         up to {@value SelectOption#VALUE_MAX_LENGTH} characters
     * @param  description
     *         The description explaining the meaning of this option in more detail, up to 50 characters
     * @param  emoji
     *         The {@link Emoji} shown next to this option, or null
     * @param  selected
     *         If selected is true this option is selected by default
     *
     * @throws IllegalArgumentException
     *         If the total amount of options is greater than 25, invalid null is provided,
     *         or any of the individual parameter requirements are violated.
     *
     * @return The same builder instance for chaining
     */
    public SimpleSelectMenu option(String label, String value, String description, Emoji emoji, boolean selected){
        SelectOption option = SelectOption.of(label, value)
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

    /**
     * Get the select menu
     *
     * @return the select menu
     */
    public SelectMenu build(){
        SelectMenu.Builder builder = SelectMenu.create(this.menu_id);
        builder.setPlaceholder(this.placeholder);
        builder.setDisabled(this.disabled);
        builder.setRequiredRange(this.min, this.max);
        builder.addOptions(this.options);
        return builder.build();
    }

}
