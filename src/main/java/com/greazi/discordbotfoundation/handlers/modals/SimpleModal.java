package com.greazi.discordbotfoundation.handlers.modals;

import com.greazi.discordbotfoundation.SimpleBot;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;

import java.util.*;

public abstract class SimpleModal {

    // ----------------------------------------------------------------------------------------
    // Main options
    // ----------------------------------------------------------------------------------------

    private String id = "id";

    private String title = "example";

    private boolean guildOnly = false;

    private LinkedHashMap<String, SimpleTextInput> textInputs = new LinkedHashMap<>();

    // Disabled because select menus are not compatible with modals
    //private LinkedHashMap<String, SimpleSelect> selectMenus = new LinkedHashMap<>();

    // Disabled because buttons are not compatible with modals
    //private ArrayList<String> buttonIds = new ArrayList<>();

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the modal logic
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
    public SimpleModal(String id) {
        this.id = id;
    }

    /**
     * Set the title
     */
    public void title(String title) {
        this.title = title;
    }

    /**
     * Add a textInput
     */
    public void textInput(SimpleTextInput textInput) {
        this.textInputs.put(textInput.getId(), textInput);
    }

    /**
     * Add multiple textInputs
     */
    public void textInputs(SimpleTextInput... textInputs) {
        for (SimpleTextInput textInput : textInputs) {
            this.textInputs.put(textInput.getId(), textInput);
        }
    }

    /**
     * Set the textInputs
     */
    public void textInputs(LinkedHashMap<String, SimpleTextInput> textInputs) {
        this.textInputs = textInputs;
    }

    // Disabled because select menus are not compatible with modals
//    /**
//     * Set the selectMenus
//     */
//    public void setSelectMenus(LinkedHashMap<String, SimpleSelect> selectMenus) {
//        this.selectMenus = selectMenus;
//    }

    // Disabled because select menus are not compatible with modals
//    /**
//     * Add a selectMenu
//     */
//    public void addSelectInput(SimpleSelect selectMenu) {
//        this.selectMenus.put(selectMenu.getId(), selectMenu);
//    }

    // Disabled because button are not compatible with modals
//    /**
//     * Set the buttons
//     */
//    public void setButtons(ArrayList<String> buttonIds) {
//        this.buttonIds = buttonIds;
//    }

    // Disabled because button are not compatible with modals
//    /**
//     * Add a button
//     */
//    public void addButton(SimpleButton button) {
//        this.buttonIds.add(button.getButton());
//    }

    // Disabled because button are not compatible with modals
//    /**
//     * Add a button
//     */
//    public void addButton(String buttonId) {
//        this.buttonIds.add(buttonId);
//    }

    /**
     * Set this modal as main guild only
     */
    public void mainGuildOnly() {
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
        // Disabled because select menus are not compatible with modals
//        this.selectMenus.forEach((id, selectMenu) -> {
//            modalBuilder.addActionRow(selectMenu.build());
//        });
        // Disabled because button are not compatible with modals
//        this.buttonIds.forEach(buttonId -> {
//            SimpleButton button = SimpleBot.getButtonHandler().getButton(buttonId);
//            modalBuilder.addActionRow(button.build());
//        });

        SimpleBot.getModalHandler().addModalListener(this);

        return modalBuilder.build();
    }

}
