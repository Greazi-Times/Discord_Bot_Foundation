package com.greazi.discordbotfoundation.handlers.modals;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SimpleModal {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    private String id = "id";

    private String title = "example";

    private boolean guildOnly = false;

    private LinkedHashMap<String, SimpleTextInput> textInputs = new LinkedHashMap<>();

    private LinkedHashMap<String, SimpleSelect> selectMenus = new LinkedHashMap<>();

    private List<String> buttonIds = Collections.emptyList();

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the command logic
     *
     * @param event ModalInteractionEvent
     */
    protected abstract void execute(ModalInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the textInputs
     */
    public void setTextInputs(LinkedHashMap<String, SimpleTextInput> textInputs) {
        this.textInputs = textInputs;
    }

    /**
     * Add a textInput
     */
    public void addTextInput(SimpleTextInput textInput) {
        this.textInputs.put(textInput.getId(), textInput);
    }

    /**
     * Set the selectMenus
     */
    public void setSelectMenus(LinkedHashMap<String, SimpleSelect> selectMenus) {
        this.selectMenus = selectMenus;
    }

    /**
     * Add a selectMenu
     */
    public void addSelectInput(SimpleSelect selectMenu) {
        this.selectMenus.put(selectMenu.getId(), selectMenu);
    }

    /**
     * Set the buttons
     */
    public void setButtons(List<String> buttonIds) {
        this.buttonIds = buttonIds;
    }

    /**
     * Add a button
     */
    public void addButton(SimpleButton button) {
        this.buttonIds.add(button.getButton());
    }

    /**
     * Add a button
     */
    public void addButton(String buttonId) {
        this.buttonIds.add(buttonId);
    }

    /**
     * Set this modal as main guild only
     */
    public void setMainGuildOnly() {
        this.guildOnly = true;
    }

    // ----------------------------------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------------------------------

    /**
     * Get the id
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the title
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Whether the button is restricted to the main guild of the bot
     *
     * @return the restricted guild
     */
    public boolean isMainGuildOnly() {
        return guildOnly;
    }

    /**
     * Get the textInputs
     *
     * @return the textInputs
     */
    public LinkedHashMap<String, SimpleTextInput> getTextInputs() {
        return textInputs;
    }

    /**
     * Get a textInput
     *
     * @return the textInput
     */
    public SimpleTextInput getTextInput(String id) {
        return textInputs.get(id);
    }

    /**
     * Get the modal
     *
     * @return the modal
     */
    public Modal build() {
        Modal.Builder modalBuilder = Modal.create(this.id, this.title);

        this.textInputs.forEach((id, textInput) -> {
            modalBuilder.addActionRow(textInput.build());
        });
        this.selectMenus.forEach((id, selectMenu) -> {
            modalBuilder.addActionRow(selectMenu.build());
        });
        this.buttonIds.forEach(buttonId -> {
            SimpleButton button = SimpleBot.getButtonHandler().getButton(buttonId);
            modalBuilder.addActionRow(button.build());
        });

        return modalBuilder.build();
    }

}
