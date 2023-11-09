package com.greazi.old.handlers.modals;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class SimpleTextInput {

    private final TextInput.Builder builder;

    public SimpleTextInput(final String id, final String label) {
        builder = TextInput.create(id, label, TextInputStyle.SHORT);
    }

    public void setParagraph() {
        builder.setStyle(TextInputStyle.PARAGRAPH);
    }

    public void setMinLength(final int minLength) {
        builder.setMinLength(minLength);
    }

    public void setMaxLength(final int maxLength) {
        builder.setMaxLength(maxLength);
    }

    public void setMinMaxLength(final int minLength, final int maxLength) {
        builder.setRequiredRange(minLength, maxLength);
    }

    public void setRequired() {
        builder.setRequired(true);
    }

    public void setValue(final String value) {
        builder.setValue(value);
    }

    public void setPlaceholder(final String placeholder) {
        builder.setPlaceholder(placeholder);
    }

    public String getId() {
        return builder.getId();
    }

    public TextInput build() {
        return builder.build();
    }

}
