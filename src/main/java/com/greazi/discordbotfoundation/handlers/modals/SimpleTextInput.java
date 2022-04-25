package com.greazi.discordbotfoundation.handlers.modals;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class SimpleTextInput {

    private final TextInput.Builder builder;

    public SimpleTextInput(String id, String label){
        builder = TextInput.create(id, label, TextInputStyle.SHORT);
    }

    public void setParagraph(){
        builder.setStyle(TextInputStyle.PARAGRAPH);
    }

    public void setMinLength(int minLength){
        builder.setMinLength(minLength);
    }

    public void setMaxLength(int maxLength){
        builder.setMaxLength(maxLength);
    }

    public void setMinMaxLength(int minLength, int maxLength){
        builder.setRequiredRange(minLength, maxLength);
    }

    public void setRequired(String placeholder){
        builder.setPlaceholder(placeholder);
    }

    public void setPlaceholder(String placeholder){
        builder.setPlaceholder(placeholder);
    }

    public String getId(){
        return builder.getId();
    }

    public TextInput build(){
        return builder.build();
    }

}
