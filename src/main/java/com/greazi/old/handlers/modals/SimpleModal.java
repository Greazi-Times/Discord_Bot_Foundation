package com.greazi.old.handlers.modals;

import com.greazi.old.SimpleBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.LinkedHashMap;

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

    private Member member = null;
    private User user = null;
    private Guild guild = null;

    // ----------------------------------------------------------------------------------------
    // Main methods
    // ----------------------------------------------------------------------------------------

    /**
     * Run the modal logic
     *
     * @param event ModalInteractionEvent
     */
    public final boolean execute(final ModalInteractionEvent event) {
        this.member = event.getMember();
        this.user = event.getUser();
        this.guild = event.getGuild();

        // TODO: Add modal checks here
        this.onModalInteract(event);
        return true;
    }

    protected abstract void onModalInteract(ModalInteractionEvent event);

    // ----------------------------------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------------------------------

    /**
     * Set the id
     */
    public SimpleModal(final String id) {
        this.id = id;
    }

    /**
     * Set the title
     */
    public void title(final String title) {
        this.title = title;
    }

    /**
     * Add a textInput
     */
    public void textInput(final SimpleTextInput textInput) {
        this.textInputs.put(textInput.getId(), textInput);
    }

    /**
     * Add multiple textInputs
     */
    public void textInputs(final SimpleTextInput... textInputs) {
        for (final SimpleTextInput textInput : textInputs) {
            this.textInputs.put(textInput.getId(), textInput);
        }
    }

    /**
     * Set the textInputs
     */
    public void textInputs(final LinkedHashMap<String, SimpleTextInput> textInputs) {
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
    protected final String getId() {
        return id;
    }

    /**
     * Get the title
     *
     * @return the title
     */
    protected final String getTitle() {
        return title;
    }

    /**
     * Whether the button is restricted to the main guild of the bot
     *
     * @return the restricted guild
     */
    protected final boolean isMainGuildOnly() {
        return guildOnly;
    }

    /**
     * Get the textInputs
     *
     * @return the textInputs
     */
    protected final LinkedHashMap<String, SimpleTextInput> getTextInputs() {
        return textInputs;
    }

    /**
     * Get a textInput
     *
     * @return the textInput
     */
    protected final SimpleTextInput getTextInput(final String id) {
        return textInputs.get(id);
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

    /**
     * Get the modal
     *
     * @return the modal
     */
    public Modal build() {
        final Modal.Builder modalBuilder = Modal.create(this.id, this.title);

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

    public void remove() {
        SimpleBot.getModalHandler().removeModalListener(this);
    }

}
